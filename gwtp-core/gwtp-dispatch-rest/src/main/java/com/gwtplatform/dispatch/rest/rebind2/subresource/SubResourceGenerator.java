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

package com.gwtplatform.dispatch.rest.rebind2.subresource;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Sets;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.dispatch.rest.rebind2.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind2.Parameter;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceContext;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceDefinition;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceGenerator;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodContext;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodDefinition;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodGenerator;
import com.gwtplatform.dispatch.rest.rebind2.utils.Arrays;
import com.gwtplatform.dispatch.rest.rebind2.utils.ClassNameGenerator;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;
import com.gwtplatform.dispatch.rest.rebind2.utils.PathResolver;
import com.gwtplatform.dispatch.rest.shared.NoXsrfHeader;

import static com.gwtplatform.dispatch.rest.rebind2.utils.Generators.findGenerator;
import static com.gwtplatform.dispatch.rest.rebind2.utils.Generators.getGenerator;

// TODO: Remove duplication between top-level and sub resource generators
public class SubResourceGenerator extends AbstractVelocityGenerator implements ResourceGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind2/resource/SubResource.vm";

    private final Set<ResourceMethodGenerator> resourceMethodGenerators;

    private SubResourceContext context;
    private JClassType resourceType;
    private ResourceDefinition parentDefinition;
    private SubResourceMethodDefinition methodDefinition;
    private JMethod method;
    private String packageName;
    private String implName;
    private String path;
    private boolean secured;
    private Set<String> imports;
    private SubResourceDefinition subResourceDefinition;

    @Inject
    SubResourceGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine,
            Set<ResourceMethodGenerator> resourceMethodGenerators) {
        super(logger, context, velocityEngine);

        this.resourceMethodGenerators = resourceMethodGenerators;
    }

    @Override
    public byte getPriority() {
        // Need to run before the top-level resource generator so we can detect sub-resources annotated with @Path.
        return DEFAULT_PRIORITY - 1;
    }

    @Override
    public boolean canGenerate(ResourceContext context) throws UnableToCompleteException {
        boolean canGenerate = context instanceof SubResourceContext;

        if (canGenerate && context.getResourceType().isAnnotationPresent(Path.class)) {
            setContext((SubResourceContext) context);

            warn("`%s#%s` returns a sub-resource, but the returned type is annotated with @Path. The latter will be "
                    + "ignored.");
        }

        return canGenerate && canGenerateAllMethods();
    }

    @Override
    public ResourceDefinition generate(ResourceContext resourceContext) throws UnableToCompleteException {
        assert resourceContext instanceof SubResourceContext; // Should already be verified, but removes IDE warning
        setContext((SubResourceContext) resourceContext);

        resolveImplName();
        resolvePath();
        resolveSecured();
        List<Parameter> parameters = resolveParameters();

        subResourceDefinition =
                new SubResourceDefinition(resourceType, packageName, implName, parameters, path, secured);

        PrintWriter printWriter = tryCreate();
        if (printWriter != null) {
            imports = Sets.newTreeSet();
            imports.add(resourceType.getQualifiedSourceName());

            generateMethods();

            mergeTemplate(printWriter);
            getContext().commit(getLogger(), printWriter);
        }

        return subResourceDefinition;
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
        return implName;
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        variables.put("imports", imports);
        variables.put("resourceType", resourceType.getSimpleSourceName());
        variables.put("methods", subResourceDefinition.getMethodDefinitions());
        variables.put("parameters", subResourceDefinition.getParameters());
    }

    private void setContext(SubResourceContext context) {
        ResourceMethodContext methodContext = context.getMethodContext();

        this.context = context;
        this.resourceType = context.getResourceType();
        this.parentDefinition = methodContext.getResourceDefinition();
        this.methodDefinition = context.getMethodDefinition();
        this.method = methodContext.getMethod();
    }

    private void resolveImplName() {
        packageName = context.getResourceType().getPackage().getName();
        implName = generateTypeName();
    }

    private String generateTypeName() {
        String resourceClassName = parentDefinition.getClassName();
        String resourceName = resourceType.getName();

        return ClassNameGenerator.prefixName(method, resourceClassName, resourceName);
    }

    private void resolvePath() {
        path = PathResolver.resolve(parentDefinition.getPath(), method);
    }

    private void resolveSecured() {
        secured = parentDefinition.isSecured()
                && !method.isAnnotationPresent(NoXsrfHeader.class)
                && !resourceType.isAnnotationPresent(NoXsrfHeader.class);
    }

    private List<Parameter> resolveParameters() {
        List<Parameter> parameters = methodDefinition.getInheritedParameters();
        parameters.addAll(methodDefinition.getParameters());

        return parameters;
    }

    private void generateMethods() throws UnableToCompleteException {
        List<JMethod> methods = Arrays.asList(resourceType.getInheritableMethods());

        for (JMethod enclosedMethod : methods) {
            generateMethod(enclosedMethod);
        }
    }

    private void generateMethod(JMethod method) throws UnableToCompleteException {
        ResourceMethodContext methodContext = new ResourceMethodContext(subResourceDefinition, context, method);
        ResourceMethodGenerator generator =
                getGenerator(getLogger(), resourceMethodGenerators, methodContext);
        ResourceMethodDefinition enclosedMethodDefinition = generator.generate(methodContext);

        subResourceDefinition.addMethodDefinition(enclosedMethodDefinition);
        imports.addAll(enclosedMethodDefinition.getImports());
    }

    private void warn(String message) {
        ResourceMethodContext methodContext = context.getMethodContext();
        String resourceName = methodContext.getResourceContext().getResourceType().getQualifiedSourceName();
        String methodName = methodContext.getMethod().getName();

        getLogger().warn(message, resourceName, methodName);
    }

    private boolean canGenerateAllMethods() {
        List<JMethod> methods = Arrays.asList(resourceType.getInheritableMethods());
        boolean canGenerate = true;

        for (JMethod enclosedMethod : methods) {
            ResourceMethodContext methodContext = new ResourceMethodContext(null, context, enclosedMethod);
            ResourceMethodGenerator generator =
                    findGenerator(getLogger(), resourceMethodGenerators, methodContext);

            if (generator == null) {
                canGenerate = false;
                getLogger().debug("Cannot find a resource method generator for `%s#%s`.",
                        context.getResourceType().getQualifiedSourceName(), enclosedMethod.getName());
            }
        }

        return canGenerate;
    }
}
