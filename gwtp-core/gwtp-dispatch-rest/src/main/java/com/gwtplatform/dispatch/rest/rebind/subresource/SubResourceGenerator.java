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

package com.gwtplatform.dispatch.rest.rebind.subresource;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.dispatch.rest.rebind.Parameter;
import com.gwtplatform.dispatch.rest.rebind.resource.AbstractResourceGenerator;
import com.gwtplatform.dispatch.rest.rebind.resource.MethodContext;
import com.gwtplatform.dispatch.rest.rebind.resource.MethodGenerator;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceContext;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassNameGenerator;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;
import com.gwtplatform.dispatch.rest.rebind.utils.PathResolver;
import com.gwtplatform.dispatch.rest.shared.NoXsrfHeader;

public class SubResourceGenerator extends AbstractResourceGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/subresource/SubResource.vm";

    private SubResourceDefinition subResourceDefinition;

    @Inject
    SubResourceGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine,
            Set<MethodGenerator> methodGenerators) {
        super(logger, context, velocityEngine, methodGenerators);
    }

    @Override
    public byte getPriority() {
        // Need to run before the top-level resource generator so we can detect sub-resources annotated with @Path.
        return DEFAULT_PRIORITY - 1;
    }

    @Override
    public boolean canGenerate(ResourceContext context) {
        boolean canGenerate = context instanceof SubResourceContext;

        if (canGenerate) {
            setContext(context);

            if (getResourceType().isAnnotationPresent(Path.class)) {
                warn("`%s#%s` returns a sub-resource, but the returned type is annotated with @Path. The latter will "
                        + "be ignored.");
            }
        }

        return canGenerate && super.canGenerate(context);
    }

    @Override
    public ResourceDefinition generate(ResourceContext context) throws UnableToCompleteException {
        subResourceDefinition = null;

        return super.generate(context);
    }

    @Override
    protected SubResourceContext getResourceContext() {
        return (SubResourceContext) super.getResourceContext();
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getImplName() {
        String resourceClassName = getParentDefinition().getClassName();
        String resourceName = getResourceType().getSimpleSourceName();

        return ClassNameGenerator.prefixName(getMethod(), resourceClassName, resourceName);
    }

    @Override
    protected ResourceDefinition getResourceDefinition() {
        return getSubResourceDefinition();
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        super.populateTemplateVariables(variables);

        variables.put("parameters", getSubResourceDefinition().getParameters());
    }

    private MethodContext getMethodContext() {
        return getResourceContext().getMethodContext();
    }

    private JMethod getMethod() {
        return getMethodContext().getMethod();
    }

    private ResourceDefinition getParentDefinition() {
        return getMethodContext().getResourceDefinition();
    }

    private SubResourceDefinition getSubResourceDefinition() {
        if (subResourceDefinition == null) {
            String path = resolvePath();
            boolean secured = resolveSecured();
            List<Parameter> parameters = resolveParameters();

            subResourceDefinition = new SubResourceDefinition(getResourceType(), getPackageName(), getImplName(),
                    parameters, path, secured);
        }

        return subResourceDefinition;
    }

    private String resolvePath() {
        return PathResolver.resolve(getParentDefinition().getPath(), getMethod());
    }

    private boolean resolveSecured() {
        return getParentDefinition().isSecured()
                && !getMethod().isAnnotationPresent(NoXsrfHeader.class)
                && !getResourceType().isAnnotationPresent(NoXsrfHeader.class);
    }

    private List<Parameter> resolveParameters() {
        SubResourceMethodDefinition methodDefinition = getResourceContext().getMethodDefinition();
        List<Parameter> parameters = methodDefinition.getInheritedParameters();
        parameters.addAll(methodDefinition.getParameters());

        return parameters;
    }

    private void warn(String message) {
        String resourceName = getResourceType().getQualifiedSourceName();
        String methodName = getMethod().getName();

        getLogger().warn(message, resourceName, methodName);
    }
}
