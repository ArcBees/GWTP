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
import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Maps;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.dispatch.rest.rebind2.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind2.SupportedHttpAnnotations;
import com.gwtplatform.dispatch.rest.rebind2.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;

public class DefaultActionGenerator extends AbstractVelocityGenerator implements ActionGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind2/action/Action.vm";

    private JMethod method;
    private String packageName;
    private String className;
    private String result;
    private HttpMethod httpVerb;

    @Inject
    DefaultActionGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine) {
        super(logger, context, velocityEngine);
    }

    @Override
    public boolean canGenerate(JMethod method) throws UnableToCompleteException {
        // TODO: reuse code from ActionMethodGenerator + verify the param annotations
        return true;
    }

    @Override
    public ClassDefinition generate(JMethod method) throws UnableToCompleteException {
        // TODO: Input should include parent's Path, Secured, ClassDefinition, Ctor Params (sub-resource)

        this.method = method;
        this.packageName = method.getEnclosingType().getPackage().getName();
        this.className = generateTypeName();
        this.result = method.getReturnType().isParameterized().getTypeArgs()[0].getParameterizedQualifiedSourceName();
        this.httpVerb = resolveHttpVerb();

        PrintWriter printWriter = tryCreate();
        if (printWriter != null) {
            mergeTemplate(printWriter);
            getContext().commit(getLogger(), printWriter);
        }

        return getClassDefinition();
    }

    @Override
    protected Map<String, Object> createTemplateVariables() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("result", result);
        variables.put("secured", false);
        variables.put("httpVerb", httpVerb);
        variables.put("path", "");

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

    private HttpMethod resolveHttpVerb() throws UnableToCompleteException {
        for (SupportedHttpAnnotations annotation : SupportedHttpAnnotations.values()) {
            if (method.isAnnotationPresent(annotation.getAnnotationClass())) {
                return annotation.getVerb();
            }
        }

        // Should never happen since this has been verified at this point
        return null;
    }

    private String generateTypeName() {
        // The service may define overloads, in which case the method name is not unique.
        // The method index will ensure the generated class uniqueness.
        int methodIndex = Arrays.asList(method.getEnclosingType().getMethods()).indexOf(method);

        // TODO: Prefix with `parentTypeName_`
        return "Action_" + methodIndex + "_" + method.getName();
    }
}
