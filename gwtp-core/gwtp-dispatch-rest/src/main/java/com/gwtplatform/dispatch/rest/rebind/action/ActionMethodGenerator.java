/*
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.rebind.HttpVerb;
import com.gwtplatform.dispatch.rest.rebind.Parameter;
import com.gwtplatform.dispatch.rest.rebind.resource.AbstractMethodGenerator;
import com.gwtplatform.dispatch.rest.rebind.resource.MethodContext;
import com.gwtplatform.dispatch.rest.rebind.resource.MethodDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import jakarta.inject.Inject;

import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.findGenerator;
import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.getGenerator;

public class ActionMethodGenerator extends AbstractMethodGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/action/ActionMethod.vm";

    private final Set<ActionGenerator> actionGenerators;

    private ActionMethodDefinition methodDefinition;

    @Inject
    protected ActionMethodGenerator(
            Logger logger,
            GeneratorContext context,
            Set<ActionGenerator> actionGenerators,
            VelocityEngine velocityEngine) {
        super(logger, context, velocityEngine);

        this.actionGenerators = actionGenerators;
    }

    @Override
    public boolean canGenerate(MethodContext context) {
        setContext(context);

        return hasValidReturnType()
                && hasExactlyOneHttpVerb()
                && canGenerateAction();
    }

    @Override
    public MethodDefinition generate(MethodContext context) throws UnableToCompleteException {
        setContext(context);

        List<Parameter> parameters = resolveParameters();
        List<Parameter> inheritedParameters = resolveInheritedParameters();
        JClassType resultType = resolveResultType();

        methodDefinition = new ActionMethodDefinition(getMethod(), parameters, inheritedParameters, resultType);
        methodDefinition.addImport(RestAction.class.getName());

        generateAction();
        generateMethod();

        return methodDefinition;
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        String resultTypeName = methodDefinition.getResultType().getParameterizedQualifiedSourceName();
        List<Parameter> actionParameters = methodDefinition.getInheritedParameters();
        actionParameters.addAll(methodDefinition.getParameters());

        variables.put("resultType", resultTypeName);
        variables.put("methodName", getMethod().getName());
        variables.put("methodParameters", methodDefinition.getParameters());
        variables.put("actionParameters", actionParameters);
        variables.put("action", methodDefinition.getActionDefinitions().get(0));
    }

    protected boolean hasExactlyOneHttpVerb() {
        int annotationsCount = 0;

        for (HttpVerb annotation : HttpVerb.values()) {
            if (getMethod().isAnnotationPresent(annotation.getAnnotationClass())) {
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

            getLogger().warn("End-Point method detected but %s http annotations found. Verify `%s#%s` is properly "
                    + "annotated.", count, getContextParentName(), getMethod().getName());
        }

        return hasOneAnnotation;
    }

    protected boolean canGenerateAction() {
        ActionContext actionContext = new ActionContext(getMethodContext(), null);
        ActionGenerator actionGenerator = findGenerator(actionGenerators, actionContext);

        boolean canGenerate = actionGenerator != null;
        if (!canGenerate) {
            getLogger().debug("Cannot find an action generator for `%s#%s`.", getContextParentName(),
                    getMethod().getName());
        }

        return canGenerate;
    }

    protected ActionDefinition generateAction(ActionContext actionContext) throws UnableToCompleteException {
        ActionGenerator generator = getGenerator(getLogger(), actionGenerators, actionContext);
        return generator.generate(actionContext);
    }

    private JClassType resolveResultType() {
        return getMethod().getReturnType().isParameterized().getTypeArgs()[0];
    }

    private void generateAction() throws UnableToCompleteException {
        ActionContext actionContext = new ActionContext(getMethodContext(), methodDefinition);
        ActionDefinition definition = generateAction(actionContext);

        methodDefinition.addAction(definition);
    }

    private void generateMethod() throws UnableToCompleteException {
        String output = mergeTemplate();
        methodDefinition.setOutput(output);
    }

    private boolean hasValidReturnType() {
        JType returnType = getMethod().getReturnType();
        String restActionName = RestAction.class.getName();

        if (returnType != null && restActionName.equals(returnType.getQualifiedSourceName())) {
            JParameterizedType parameterizedType = returnType.isParameterized();

            boolean isValid = parameterizedType != null && parameterizedType.getTypeArgs().length == 1;
            if (!isValid) {
                getLogger().warn("RestAction<?> specified as a return type for `%s#%s`, but type argument is missing.",
                        getContextParentName(), getMethod().getName());
            }

            return isValid;
        }

        return false;
    }

    private String getContextParentName() {
        return getMethodContext().getResourceContext().getResourceType().getQualifiedSourceName();
    }
}
