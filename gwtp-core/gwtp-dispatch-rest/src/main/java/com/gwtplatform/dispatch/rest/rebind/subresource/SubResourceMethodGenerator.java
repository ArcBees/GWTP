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

package com.gwtplatform.dispatch.rest.rebind.subresource;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;

import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.gwtplatform.dispatch.rest.rebind.HttpVerb;
import com.gwtplatform.dispatch.rest.rebind.Parameter;
import com.gwtplatform.dispatch.rest.rebind.resource.AbstractMethodGenerator;
import com.gwtplatform.dispatch.rest.rebind.resource.MethodContext;
import com.gwtplatform.dispatch.rest.rebind.resource.MethodDefinition;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceDefinition;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceGenerator;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.findGenerator;

public class SubResourceMethodGenerator extends AbstractMethodGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/subresource/SubResourceMethod.vm";

    private final Provider<Set<ResourceGenerator>> resourceGeneratorsProvider;

    private JClassType returnInterface;
    private SubResourceMethodDefinition methodDefinition;

    @Inject
    SubResourceMethodGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine,
            Provider<Set<ResourceGenerator>> resourceGeneratorsProvider) {
        super(logger, context, velocityEngine);

        this.resourceGeneratorsProvider = resourceGeneratorsProvider;
    }

    @Override
    public boolean canGenerate(MethodContext context) {
        setContext(context);

        JClassType restActionType = findType(RestAction.class);

        return restActionType != null
                && returnInterface != null
                && !returnInterface.isAssignableTo(restActionType)
                && getMethod().isAnnotationPresent(Path.class)
                && !HttpVerb.isHttpMethod(getMethod())
                && canGenerateSubResource();
    }

    @Override
    public MethodDefinition generate(MethodContext context) throws UnableToCompleteException {
        setContext(context);

        List<Parameter> parameters = resolveParameters();
        List<Parameter> inheritedParameters = resolveInheritedParameters();

        methodDefinition = new SubResourceMethodDefinition(getMethod(), parameters, inheritedParameters);
        methodDefinition.addImport(returnInterface.getQualifiedSourceName());

        generateResource();
        generateMethod();

        return methodDefinition;
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        ResourceDefinition subResource = methodDefinition.getResourceDefinitions().get(0);
        List<Parameter> subResourceParameters;
        if (subResource instanceof SubResourceDefinition) {
            subResourceParameters = ((SubResourceDefinition) subResource).getParameters();
        } else {
            subResourceParameters = new ArrayList<Parameter>();
        }

        variables.put("resourceType", new ClassDefinition(returnInterface).getParameterizedClassName());
        variables.put("methodName", getMethod().getName());
        variables.put("parameters", methodDefinition.getParameters());
        variables.put("subResourceParameters", subResourceParameters);
        variables.put("subResource", subResource);
    }

    @Override
    protected void setContext(MethodContext methodContext) {
        super.setContext(methodContext);

        this.returnInterface = getMethod().getReturnType().isInterface();
    }

    private void generateResource() throws UnableToCompleteException {
        SubResourceContext subResourceContext =
                new SubResourceContext(returnInterface, getMethodContext(), methodDefinition);
        ResourceGenerator generator = findGenerator(resourceGeneratorsProvider.get(), subResourceContext);
        ResourceDefinition resourceDefinition = generator.generate(subResourceContext);

        methodDefinition.addResource(resourceDefinition);
    }

    private void generateMethod() throws UnableToCompleteException {
        StringWriter writer = new StringWriter();

        mergeTemplate(writer);

        methodDefinition.setOutput(writer.toString());
    }

    private boolean canGenerateSubResource() {
        SubResourceContext subResourceContext = new SubResourceContext(returnInterface, getMethodContext(), null);
        ResourceGenerator generator = findGenerator(resourceGeneratorsProvider.get(), subResourceContext);
        String resourceTypeName = returnInterface.getQualifiedSourceName();

        boolean canGenerate = generator != null;
        if (!canGenerate) {
            getLogger().debug("Cannot find a sub resource generator for `%s`.", resourceTypeName);
        }

        return canGenerate;
    }
}
