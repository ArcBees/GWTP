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

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.rebind.HttpVerb;
import com.gwtplatform.dispatch.rest.rebind.Parameter;
import com.gwtplatform.dispatch.rest.rebind.resource.AbstractResourceMethodGenerator;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceMethodContext;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceMethodDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.findGenerator;
import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.getGenerator;

public class ActionMethodGenerator extends AbstractResourceMethodGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind2/action/ActionMethod.vm";

    private final Set<ActionGenerator> actionGenerators;

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
        setContext(context);

        JType returnType = getMethod().getReturnType();

        return returnType != null
                && isValidRestAction(returnType)
                && hasExactlyOneHttpVerb()
                && canGenerateAction();
    }

    @Override
    public ResourceMethodDefinition generate(ResourceMethodContext context) throws UnableToCompleteException {
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

    private JClassType resolveResultType() {
        return getMethod().getReturnType().isParameterized().getTypeArgs()[0];
    }

    private void generateAction() throws UnableToCompleteException {
        ActionContext actionContext = new ActionContext(getMethodContext(), methodDefinition);
        ActionGenerator generator = getGenerator(getLogger(), actionGenerators, actionContext);
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
                getLogger().warn("RestAction<?> specified as a return type for `%s#%s`, but type argument is missing.",
                        getContextParentName(), getMethod().getName());
            }

            return isValid;
        }

        return false;
    }

    private boolean hasExactlyOneHttpVerb() {
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

    private boolean canGenerateAction() {
        ActionContext actionContext = new ActionContext(getMethodContext(), null);
        ActionGenerator actionGenerator = findGenerator(getLogger(), actionGenerators, actionContext);

        boolean canGenerate = actionGenerator != null;
        if (!canGenerate) {
            getLogger().debug("Cannot find an action generator for `%s#%s`.", getContextParentName(),
                    getMethod().getName());
        }

        return canGenerate;
    }

    private String getContextParentName() {
        return getMethodContext().getResourceContext().getResourceType().getQualifiedSourceName();
    }
}
