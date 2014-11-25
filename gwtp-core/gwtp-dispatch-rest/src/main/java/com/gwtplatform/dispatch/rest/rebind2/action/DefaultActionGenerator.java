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
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodContext;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;
import com.gwtplatform.dispatch.rest.rebind2.utils.PathResolver;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.NoXsrfHeader;

public class DefaultActionGenerator extends AbstractVelocityGenerator implements ActionGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind2/action/Action.vm";

    private ActionContext context;
    private JMethod method;
    private String packageName;
    private String className;
    private HttpMethod httpVerb;
    private String path;
    private boolean secured;

    @Inject
    DefaultActionGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine) {
        super(logger, context, velocityEngine);
    }

    @Override
    public boolean canGenerate(ActionContext context) throws UnableToCompleteException {
        // TODO: reuse code from ActionMethodGenerator + verify the param annotations
        return true;
    }

    @Override
    public ActionDefinition generate(ActionContext context) throws UnableToCompleteException {
        // TODO: Input should include parent's Path, Secured, ClassDefinition, Ctor Params (sub-resource)

        this.context = context;
        this.method = context.getMethodContext().getMethod();

        resolveClassName();
        resolveHttpVerb();
        resolvePath();
        resolveSecured();

        ActionDefinition actionDefinition = new ActionDefinition(packageName, className);

        PrintWriter printWriter = tryCreate();
        if (printWriter != null) {
            mergeTemplate(printWriter);
            getContext().commit(getLogger(), printWriter);
        }

        return actionDefinition;
    }

    @Override
    protected Map<String, Object> createTemplateVariables() {
        Map<String, Object> variables = Maps.newHashMap();
        String result = method.getReturnType().isParameterized().getTypeArgs()[0].getParameterizedQualifiedSourceName();
        variables.put("result", result);
        variables.put("secured", secured);
        variables.put("httpVerb", httpVerb);
        variables.put("path", path);
        variables.put("parameters", context.getMethodDefinition().getParameters());

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

    private void resolveHttpVerb() throws UnableToCompleteException {
        // Should always resolve to a verb, as has already been verified
        for (SupportedHttpAnnotations annotation : SupportedHttpAnnotations.values()) {
            if (method.isAnnotationPresent(annotation.getAnnotationClass())) {
                httpVerb = annotation.getVerb();
            }
        }
    }

    private void resolvePath() {
        path = PathResolver.resolve(context.getMethodContext().getResourceDefinition().getPath(), method);
    }

    private void resolveSecured() {
        ResourceMethodContext methodContext = context.getMethodContext();

        secured = methodContext.getResourceDefinition().isSecured();
        secured &= !methodContext.getMethod().isAnnotationPresent(NoXsrfHeader.class);
    }
}
