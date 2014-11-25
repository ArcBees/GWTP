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

package com.gwtplatform.dispatch.rest.rebind2.action;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.client.MetadataType;
import com.gwtplatform.dispatch.rest.rebind.event.RegisterMetadataEvent;
import com.gwtplatform.dispatch.rest.rebind.event.RegisterSerializableTypeEvent;
import com.gwtplatform.dispatch.rest.rebind2.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind2.HttpParameter;
import com.gwtplatform.dispatch.rest.rebind2.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.rebind2.HttpVerbs;
import com.gwtplatform.dispatch.rest.rebind2.Parameter;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodContext;
import com.gwtplatform.dispatch.rest.rebind2.utils.Arrays;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;
import com.gwtplatform.dispatch.rest.rebind2.utils.PathResolver;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.NoXsrfHeader;

import static com.gwtplatform.dispatch.rest.client.MetadataType.BODY_TYPE;
import static com.gwtplatform.dispatch.rest.client.MetadataType.RESPONSE_TYPE;
import static com.gwtplatform.dispatch.rest.rebind2.HttpParameterType.FORM;
import static com.gwtplatform.dispatch.rest.rebind2.HttpParameterType.isHttpParameter;

public class DefaultActionGenerator extends AbstractVelocityGenerator implements ActionGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind2/action/Action.vm";
    private static final String MANY_POTENTIAL_BODY = "`%s#%s` has more than one potential body parameter.";
    private static final String FORM_AND_BODY_PARAM = "`%s#%s` has both @FormParam and a body parameter. "
            + "You must specify one or the other.";

    private final EventBus eventBus;
    private final HttpParameterFactory httpParameterFactory;

    private ActionContext context;
    private JMethod method;
    private ActionDefinition actionDefinition;
    private String packageName;
    private String className;
    private JClassType resultType;
    private Parameter bodyParameter;
    private List<HttpParameter> httpParameters;
    private String contentType;

    @Inject
    DefaultActionGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine,
            EventBus eventBus,
            HttpParameterFactory httpParameterFactory) {
        super(logger, context, velocityEngine);

        this.eventBus = eventBus;
        this.httpParameterFactory = httpParameterFactory;
    }

    @Override
    public boolean canGenerate(ActionContext context) {
        this.context = context;
        int potentialBodyParametersCount = 0;
        boolean formParamDetected = false;
        boolean canGenerate = true;

        List<JParameter> parameters = Arrays.asList(context.getMethodContext().getMethod().getParameters());
        for (JParameter parameter : parameters) {
            boolean isValidHttpParam = httpParameterFactory.validate(parameter);

            formParamDetected |= parameter.isAnnotationPresent(FORM.getAnnotationClass());
            if (!isValidHttpParam && !isHttpParameter(parameter)) {
                ++potentialBodyParametersCount;
            }
        }

        if (potentialBodyParametersCount > 1) {
            canGenerate = false;
            error(MANY_POTENTIAL_BODY);
        }
        if (potentialBodyParametersCount >= 1 && formParamDetected) {
            canGenerate = false;
            error(FORM_AND_BODY_PARAM);
        }

        return canGenerate;
    }

    @Override
    public ActionDefinition generate(ActionContext context) throws UnableToCompleteException {
        // TODO: Input should include parent's Ctor Params (sub-resource)

        this.context = context;
        this.method = context.getMethodContext().getMethod();

        resolveClassName();
        HttpMethod verb = resolveHttpVerb();
        String path = resolvePath();
        boolean secured = resolveSecured();
        resolveResultType();
        filterParameters();
        resolveContentType();

        actionDefinition = new ActionDefinition(packageName, className, verb, path, secured);

        PrintWriter printWriter = tryCreate();
        if (printWriter != null) {
            mergeTemplate(printWriter);
            getContext().commit(getLogger(), printWriter);

            registerMetadata();
        }

        return actionDefinition;
    }

    @Override
    protected Map<String, Object> createTemplateVariables() {
        Map<String, Object> variables = Maps.newHashMap();
        String result = method.getReturnType().isParameterized().getTypeArgs()[0].getParameterizedQualifiedSourceName();
        String bodyParameterName = bodyParameter != null ? bodyParameter.getVariableName() : null;

        variables.put("result", result);
        variables.put("secured", actionDefinition.isSecured());
        variables.put("httpVerb", actionDefinition.getVerb());
        variables.put("path", actionDefinition.getPath());
        variables.put("bodyParameterName", bodyParameterName);
        variables.put("parameters", context.getMethodDefinition().getParameters());
        variables.put("httpParameters", httpParameters);
        variables.put("contentType", contentType);

        return variables;
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

    private void resolveClassName() {
        this.packageName = generatePackageName();
        this.className = generateTypeName();
    }

    private String generatePackageName() {
        return context.getMethodContext().getResourceDefinition().getPackageName();
    }

    private String generateTypeName() {
        // The service may define overloads, in which case the method name is not unique.
        // The method index will ensure the generated class uniqueness.
        int methodIndex = Arrays.asList(method.getEnclosingType().getInheritableMethods()).indexOf(method);

        String resourceClassName = context.getMethodContext().getResourceDefinition().getClassName();
        return String.format("%s_%d_%s", resourceClassName, methodIndex, method.getName());
    }

    private HttpMethod resolveHttpVerb() throws UnableToCompleteException {
        HttpMethod verb = null;

        // Should always resolve to a verb, as has already been verified
        for (HttpVerbs annotation : HttpVerbs.values()) {
            if (method.isAnnotationPresent(annotation.getAnnotationClass())) {
                verb = annotation.getVerb();
            }
        }

        return verb;
    }

    private String resolvePath() {
        return PathResolver.resolve(context.getMethodContext().getResourceDefinition().getPath(), method);
    }

    private boolean resolveSecured() {
        ResourceMethodContext methodContext = context.getMethodContext();

        return methodContext.getResourceDefinition().isSecured()
                && !method.isAnnotationPresent(NoXsrfHeader.class);
    }

    private void resolveResultType() {
        resultType = method.getReturnType().isParameterized().getTypeArgs()[0];
    }

    private void filterParameters() throws UnableToCompleteException {
        List<Parameter> parameters = context.getMethodDefinition().getParameters();
        httpParameters = Lists.newArrayList();
        bodyParameter = null;

        for (Parameter parameter : parameters) {
            JParameter jParameter = parameter.getParameter();

            if (httpParameterFactory.validate(jParameter)) {
                HttpParameter httpParameter = httpParameterFactory.create(jParameter);
                httpParameters.add(httpParameter);
            } else {
                // We already verified we have only one in #canGenerate()
                assert bodyParameter == null;
                bodyParameter = parameter;
            }
        }
    }

    private void resolveContentType() {
        Consumes consumes = method.getAnnotation(Consumes.class);

        if (consumes != null && consumes.value().length > 0) {
            contentType = consumes.value()[0];
        } else {
            contentType = null;
        }
    }

    // TODO: Revisit when rewriting serialization generators
    private void registerMetadata() throws UnableToCompleteException {
        if (bodyParameter != null) {
            registerMetadatum(BODY_TYPE, bodyParameter.getParameter().getType());
        }

        registerMetadatum(RESPONSE_TYPE, resultType);
    }

    private void registerMetadatum(MetadataType metadataType, JType type) {
        String typeLiteral = "\"" + type.getParameterizedQualifiedSourceName() + "\"";

        eventBus.post(new RegisterMetadataEvent(getClassDefinition().toString(), metadataType, typeLiteral));

        if (!Void.class.getCanonicalName().equals(type.getQualifiedSourceName()) && type.isPrimitive() == null) {
            eventBus.post(new RegisterSerializableTypeEvent(type));
        }
    }

    private void error(String message) {
        method = context.getMethodContext().getMethod();
        String typeName = method.getEnclosingType().getQualifiedSourceName();
        String methodName = method.getName();

        getLogger().error(String.format(message, typeName, methodName));
    }
}
