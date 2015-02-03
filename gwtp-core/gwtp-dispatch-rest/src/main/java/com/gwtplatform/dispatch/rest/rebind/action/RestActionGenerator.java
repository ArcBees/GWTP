/**
 * Copyright 2014 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.dispatch.rest.rebind.action;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.rebind.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind.HttpVerb;
import com.gwtplatform.dispatch.rest.rebind.Parameter;
import com.gwtplatform.dispatch.rest.rebind.parameter.HttpParameter;
import com.gwtplatform.dispatch.rest.rebind.parameter.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.rebind.resource.MethodContext;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceContext;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceDefinition;
import com.gwtplatform.dispatch.rest.rebind.serialization.SerializationContext;
import com.gwtplatform.dispatch.rest.rebind.serialization.SerializationGenerator;
import com.gwtplatform.dispatch.rest.rebind.subresource.SubResourceContext;
import com.gwtplatform.dispatch.rest.rebind.utils.Arrays;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassNameGenerator;
import com.gwtplatform.dispatch.rest.rebind.utils.ContentType;
import com.gwtplatform.dispatch.rest.rebind.utils.ContentTypeResolver;
import com.gwtplatform.dispatch.rest.rebind.utils.Generators;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;
import com.gwtplatform.dispatch.rest.rebind.utils.PathResolver;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.NoXsrfHeader;

import static com.gwtplatform.dispatch.rest.rebind.parameter.HttpParameterType.FORM;
import static com.gwtplatform.dispatch.rest.rebind.parameter.HttpParameterType.isHttpParameter;

public class RestActionGenerator extends AbstractVelocityGenerator implements ActionGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/action/Action.vm";
    private static final String MANY_POTENTIAL_BODY = "`%s#%s` has more than one potential body parameter.";
    private static final String FORM_AND_BODY_PARAM = "`%s#%s` has both @FormParam and a body parameter. "
            + "You must specify one or the other.";
    private static final String GET_WITH_BODY = "`%s#%s` annotated with @GET or @HEAD contains illegal Form or Body"
            + "parameters.";

    private final HttpParameterFactory httpParameterFactory;
    private final Set<SerializationGenerator> serializationGenerators;

    private ActionContext context;
    private ResourceDefinition resourceDefinition;
    private ActionMethodDefinition methodDefinition;
    private JMethod method;
    private ActionDefinition actionDefinition;
    private String packageName;
    private String className;
    private List<HttpParameter> httpParameters;
    private Parameter bodyParameter;

    @Inject
    RestActionGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine,
            HttpParameterFactory httpParameterFactory,
            Set<SerializationGenerator> serializationGenerators) {
        super(logger, context, velocityEngine);

        this.httpParameterFactory = httpParameterFactory;
        this.serializationGenerators = serializationGenerators;
    }

    @Override
    public boolean canGenerate(ActionContext context) {
        setContext(context);

        int potentialBodyParametersCount = 0;
        boolean formParamDetected = false;

        List<JParameter> parameters = findAllParameters(context.getMethodContext());
        for (JParameter parameter : parameters) {
            boolean isValidHttpParam = httpParameterFactory.validate(parameter);

            formParamDetected |= parameter.isAnnotationPresent(FORM.getAnnotationClass());
            if (!isValidHttpParam && !isHttpParameter(parameter)) {
                ++potentialBodyParametersCount;
            }
        }

        return findPotentialErrors(potentialBodyParametersCount, formParamDetected);
    }

    @Override
    public ActionDefinition generate(ActionContext context) throws UnableToCompleteException {
        setContext(context);
        resolveClassName();

        HttpMethod verb = resolveHttpVerb();
        String path = resolvePath();
        boolean secured = resolveSecured();
        JClassType resultType = resolveResultType();
        Set<ContentType> consumes = resolveConsumes();
        Set<ContentType> produces = resolveProduces();
        filterParameters();

        PrintWriter printWriter = tryCreate();

        if (printWriter != null) {
            actionDefinition = new ActionDefinition(getPackageName(), getImplName(), verb, path, secured, consumes,
                    produces, resultType, httpParameters, bodyParameter);

            mergeTemplate(printWriter);
            commit(printWriter);

            generateSerializers();
        }

        return actionDefinition;
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        String resultTypeName = actionDefinition.getResultType().getParameterizedQualifiedSourceName();

        String bodyTypeName = null;
        String bodyParameterName = null;
        if (actionDefinition.hasBody()) {
            bodyParameterName = actionDefinition.getBodyParameter().getVariableName();
            bodyTypeName = bodyParameter.getParameterizedQualifiedName();
        }

        List<Parameter> parameters = methodDefinition.getInheritedParameters();
        parameters.addAll(methodDefinition.getParameters());

        variables.put("result", resultTypeName);
        variables.put("secured", actionDefinition.isSecured());
        variables.put("httpVerb", actionDefinition.getVerb());
        variables.put("path", actionDefinition.getPath());
        variables.put("body", bodyTypeName);
        variables.put("bodyParameterName", bodyParameterName);
        variables.put("parameters", parameters);
        variables.put("httpParameters", actionDefinition.getHttpParameters());
        // TODO: Don't generate but rather let the serializer add the header at runtime
        variables.put("contentType", actionDefinition.getConsumes().iterator().next());
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getPackageName() {
        return packageName;
    }

    @Override
    protected String getImplName() {
        return className;
    }

    private void setContext(ActionContext context) {
        MethodContext methodContext = context.getMethodContext();

        this.context = context;
        this.methodDefinition = context.getMethodDefinition();
        this.resourceDefinition = methodContext.getResourceDefinition();
        this.method = methodContext.getMethod();
    }

    private void resolveClassName() {
        this.packageName = generatePackageName();
        this.className = generateTypeName();
    }

    private String generatePackageName() {
        return resourceDefinition.getPackageName();
    }

    private String generateTypeName() {
        String resourceClassName = resourceDefinition.getClassName();
        String methodName = method.getName();

        return ClassNameGenerator.prefixName(method, resourceClassName, methodName);
    }

    private HttpMethod resolveHttpVerb() {
        HttpMethod verb = null;

        // Should always resolve to a verb, as has already been verified
        for (HttpVerb annotation : HttpVerb.values()) {
            if (method.isAnnotationPresent(annotation.getAnnotationClass())) {
                verb = annotation.getVerb();
            }
        }

        return verb;
    }

    private String resolvePath() {
        return PathResolver.resolve(resourceDefinition.getPath(), method);
    }

    private boolean resolveSecured() {
        return resourceDefinition.isSecured()
                && !method.isAnnotationPresent(NoXsrfHeader.class);
    }

    private JClassType resolveResultType() {
        return methodDefinition.getResultType();
    }

    private Set<ContentType> resolveConsumes() {
        return ContentTypeResolver.resolveConsumes(method, resourceDefinition.getConsumes());
    }

    private Set<ContentType> resolveProduces() {
        return ContentTypeResolver.resolveProduces(method, resourceDefinition.getConsumes());
    }

    private void filterParameters() {
        List<Parameter> parameters = methodDefinition.getInheritedParameters();
        parameters.addAll(methodDefinition.getParameters());

        httpParameters = Lists.newArrayList();
        bodyParameter = null;

        for (Parameter parameter : parameters) {
            JParameter jParameter = parameter.getParameter();

            if (httpParameterFactory.validate(jParameter)) {
                HttpParameter httpParameter = httpParameterFactory.create(jParameter);
                httpParameters.add(httpParameter);
            } else {
                assert bodyParameter == null; // We already verified we have only one in #canGenerate()
                bodyParameter = parameter;
            }
        }
    }

    private void generateSerializers() throws UnableToCompleteException {
        if (bodyParameter != null) {
            generateSerializer(bodyParameter.getParameter().getType(), actionDefinition.getConsumes());
        }

        generateSerializer(actionDefinition.getResultType(), actionDefinition.getProduces());
    }

    private void generateSerializer(JType type, Set<ContentType> contentTypes) throws UnableToCompleteException {
        SerializationContext serializationContext = new SerializationContext(context, type, contentTypes);
        Generators.executeAll(serializationGenerators, serializationContext);
    }

    private List<JParameter> findAllParameters(MethodContext methodContext) {
        List<JParameter> jParameters = Lists.newArrayList();

        ResourceContext resourceContext = methodContext.getResourceContext();
        if (resourceContext instanceof SubResourceContext) {
            MethodContext parentMethodContext = ((SubResourceContext) resourceContext).getMethodContext();
            List<JParameter> parentParameters = findAllParameters(parentMethodContext);

            jParameters.addAll(parentParameters);
        }

        jParameters.addAll(Arrays.asList(methodContext.getMethod().getParameters()));
        return jParameters;
    }

    private boolean findPotentialErrors(int potentialBodyParametersCount, boolean formParamDetected) {
        HttpMethod verb = resolveHttpVerb();
        boolean canGenerate = true;

        if (potentialBodyParametersCount > 1) {
            canGenerate = false;
            error(MANY_POTENTIAL_BODY);
        }
        if (potentialBodyParametersCount >= 1 && formParamDetected) {
            canGenerate = false;
            error(FORM_AND_BODY_PARAM);
        }
        if ((verb == HttpMethod.GET || verb == HttpMethod.HEAD)
                && (potentialBodyParametersCount >= 1 || formParamDetected)) {
            canGenerate = false;
            error(GET_WITH_BODY);
        }

        return canGenerate;
    }

    private void error(String message) {
        method = context.getMethodContext().getMethod();
        String typeName = method.getEnclosingType().getQualifiedSourceName();
        String methodName = method.getName();

        getLogger().error(String.format(message, typeName, methodName));
    }
}
