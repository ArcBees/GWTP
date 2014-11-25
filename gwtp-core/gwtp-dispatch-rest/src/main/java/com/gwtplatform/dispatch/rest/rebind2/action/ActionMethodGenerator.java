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
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.rebind2.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind2.SupportedHttpAnnotations;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodDefinition;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodGenerator;
import com.gwtplatform.dispatch.rest.rebind2.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;
import com.gwtplatform.dispatch.rest.shared.RestAction;

public class ActionMethodGenerator extends AbstractVelocityGenerator implements ResourceMethodGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind2/action/ActionMethod.vm";

    private final ActionGenerator actionGenerator;

    private JMethod method;
    private ClassDefinition actionDefinition;

    @Inject
    ActionMethodGenerator(
            Logger logger,
            GeneratorContext context,
            ActionGenerator actionGenerator,
            VelocityEngine velocityEngine) {
        super(logger, context, velocityEngine);

        this.actionGenerator = actionGenerator;
    }

    @Override
    public boolean canGenerate(JMethod method) throws UnableToCompleteException {
        this.method = method;
        JType returnType = method.getReturnType();

        return returnType != null
                && isValidRestAction(returnType)
                && hasExactlyOneHttpVerb()
                && actionGenerator.canGenerate(method);
    }

    @Override
    public ResourceMethodDefinition generate(JMethod method) throws UnableToCompleteException {
        this.method = method;
        this.actionDefinition = actionGenerator.generate(method);

        Set<String> imports = Sets.newHashSet(actionDefinition.toString());
        StringWriter writer = new StringWriter();

        // TODO: Add the defaultDateFormat parameter
        // TODO: Carry parameter from the method
        // TODO: Carry parameter from the context (sub-resources)

        mergeTemplate(writer);

        return new ResourceMethodDefinition(method, imports, writer.toString());
    }

    @Override
    protected Map<String, Object> createTemplateVariables() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("methodDeclaration", method.getReadableDeclaration(false, true, true, true, true));
        variables.put("action", actionDefinition);

        return variables;
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getPackageName() {
        return method.getEnclosingType().getPackage().getName();
    }

    @Override
    protected String getImplName() {
        return method.getEnclosingType().getSimpleSourceName() + "#" + method.getName();
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

        for (SupportedHttpAnnotations annotation : SupportedHttpAnnotations.values()) {
            if (method.isAnnotationPresent(annotation.getAnnotationClass())) {
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
        getLogger().warn(message, method.getEnclosingType().getQualifiedSourceName(), method.getName());
    }
}
