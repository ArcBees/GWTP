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

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.rebind2.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind2.HttpVerbs;
import com.gwtplatform.dispatch.rest.rebind2.Parameter;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodContext;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodDefinition;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodGenerator;
import com.gwtplatform.dispatch.rest.rebind2.utils.Arrays;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import static com.gwtplatform.dispatch.rest.rebind2.utils.Generators.findFirstGeneratorByInput;
import static com.gwtplatform.dispatch.rest.rebind2.utils.Generators.getFirstGeneratorByWeightAndInput;

public class ActionMethodGenerator extends AbstractVelocityGenerator implements ResourceMethodGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind2/action/ActionMethod.vm";

    private final Set<ActionGenerator> actionGenerators;

    private ResourceMethodContext context;
    private ActionMethodDefinition methodDefinition;

    @Inject
    ActionMethodGenerator(
            Logger logger,
            GeneratorContext context,
            Set<ActionGenerator> actionGenerators,
            VelocityEngine velocityEngine) {
        super(logger, context, velocityEngine);

        this.actionGenerators = actionGenerators;
    }

    @Override
    public boolean canGenerate(ResourceMethodContext context) throws UnableToCompleteException {
        this.context = context;
        JType returnType = context.getMethod().getReturnType();

        boolean canGenerate = returnType != null
                && isValidRestAction(returnType)
                && hasExactlyOneHttpVerb();

        // Make sure we have at least one action generator suited for the work
        if (canGenerate) {
            ActionContext actionContext = new ActionContext(context, null);
            ActionGenerator actionGenerator = findFirstGeneratorByInput(getLogger(), actionGenerators, actionContext);

            canGenerate = actionGenerator != null;
        }

        return canGenerate;
    }

    @Override
    public ResourceMethodDefinition generate(ResourceMethodContext context) throws UnableToCompleteException {
        this.context = context;

        // TODO: Carry parameters from the resource context (sub-resources)
        List<Parameter> parameters = resolveParameters();

        methodDefinition = new ActionMethodDefinition(context.getMethod(), parameters);
        methodDefinition.addImport(RestAction.class.getName());

        generateAction();
        generateMethod();

        return methodDefinition;
    }

    @Override
    protected Map<String, Object> createTemplateVariables() {
        Map<String, Object> variables = Maps.newHashMap();
        JMethod method = context.getMethod();
        String resultType =
                method.getReturnType().isParameterized().getTypeArgs()[0].getParameterizedQualifiedSourceName();

        variables.put("resultType", resultType);
        variables.put("methodName", method.getName());
        variables.put("parameters", methodDefinition.getParameters());
        variables.put("action", methodDefinition.getActionDefinitions().get(0));

        return variables;
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getPackageName() {
        return context.getMethod().getEnclosingType().getPackage().getName();
    }

    @Override
    protected String getImplName() {
        JMethod method = context.getMethod();
        return method.getEnclosingType().getSimpleSourceName() + "#" + method.getName();
    }

    private List<Parameter> resolveParameters() {
        List<JParameter> jParameters = Arrays.asList(context.getMethod().getParameters());

        return Lists.transform(jParameters, new Function<JParameter, Parameter>() {
            @Override
            public Parameter apply(JParameter jParameter) {
                return new Parameter(jParameter);
            }
        });
    }

    private void generateAction() throws UnableToCompleteException {
        ActionContext actionContext = new ActionContext(context, methodDefinition);
        ActionGenerator generator = getFirstGeneratorByWeightAndInput(getLogger(), actionGenerators, actionContext);
        ActionDefinition definition = generator.generate(actionContext);

        methodDefinition.addAction(definition);
    }

    private void generateMethod() throws UnableToCompleteException {
        StringWriter writer = new StringWriter();

        mergeTemplate(writer);

        methodDefinition.setOutput(writer.toString());
    }

    private boolean isValidRestAction(JType type) throws UnableToCompleteException {
        String restActionName = getType(RestAction.class).getQualifiedSourceName();
        if (restActionName.equals(type.getQualifiedSourceName())) {
            JParameterizedType parameterizedType = type.isParameterized();

            boolean isValid = parameterizedType != null && parameterizedType.getTypeArgs().length == 1;
            if (!isValid) {
                warn("RestAction<?> specified as a return type for `%s#%s`, but type argument is missing.");
            }

            return isValid;
        }

        return false;
    }

    private boolean hasExactlyOneHttpVerb() {
        int annotationsCount = 0;

        for (HttpVerbs annotation : HttpVerbs.values()) {
            if (context.getMethod().isAnnotationPresent(annotation.getAnnotationClass())) {
                annotationsCount += 1;
            }
        }

        boolean hasOneAnnotation = annotationsCount == 1;
        if (!hasOneAnnotation) {
            String count;

            if (annotationsCount == 0) {
                count = "no";
            } else {
                count = "more than one";
            }

            warn("End-Point method detected but " + count
                    + " http annotations found. Verify `%s#%s` is properly annotated.");
        }

        return hasOneAnnotation;
    }

    private void warn(String message) {
        JMethod method = context.getMethod();
        getLogger().warn(message, method.getEnclosingType().getQualifiedSourceName(), method.getName());
    }
}
