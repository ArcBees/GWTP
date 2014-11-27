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

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.gwtplatform.dispatch.rest.rebind2.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind2.HttpVerbs;
import com.gwtplatform.dispatch.rest.rebind2.Parameter;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceDefinition;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceGenerator;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodContext;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodDefinition;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodGenerator;
import com.gwtplatform.dispatch.rest.rebind2.utils.Arrays;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import static com.gwtplatform.dispatch.rest.rebind2.utils.Generators.findGenerator;

// TODO: Remove duplication between method generators
public class SubResourceMethodGenerator extends AbstractVelocityGenerator implements ResourceMethodGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind2/resource/SubResourceMethod.vm";

    private final Provider<Set<ResourceGenerator>> resourceGeneratorsProvider;

    private ResourceMethodContext context;
    private JMethod method;
    private ResourceDefinition parentDefinition;
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
    public boolean canGenerate(ResourceMethodContext context) throws UnableToCompleteException {
        setContext(context);

        JClassType restActionType = getType(RestAction.class);

        return returnInterface != null
                && !returnInterface.isAssignableTo(restActionType)
                && !HttpVerbs.isHttpMethod(method)
                && canGenerateSubResource();
    }

    @Override
    public ResourceMethodDefinition generate(ResourceMethodContext context) throws UnableToCompleteException {
        setContext(context);

        List<Parameter> parameters = resolveParameters();
        List<Parameter> inheritedParameters = resolveInheritedParameters();

        methodDefinition = new SubResourceMethodDefinition(method, parameters, inheritedParameters);
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
    protected String getPackageName() {
        return parentDefinition.getPackageName();
    }

    @Override
    protected String getImplName() {
        return parentDefinition.getClassName() + "#" + method.getName();
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        ResourceDefinition subResource = methodDefinition.getResourceDefinitions().get(0);
        List<Parameter> subResourceParameters;
        if (subResource instanceof SubResourceDefinition) {
            subResourceParameters = ((SubResourceDefinition) subResource).getParameters();
        } else {
            subResourceParameters = Lists.newArrayList();
        }

        variables.put("resourceType", returnInterface.getSimpleSourceName());
        variables.put("methodName", method.getName());
        variables.put("parameters", methodDefinition.getParameters());
        variables.put("subResourceParameters", subResourceParameters);
        variables.put("subResource", subResource);
    }

    private void setContext(ResourceMethodContext context) {
        this.context = context;
        this.method = context.getMethod();
        this.parentDefinition = context.getResourceDefinition();
        this.returnInterface = method.getReturnType().isInterface();
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

    private List<Parameter> resolveInheritedParameters() {
        List<Parameter> inheritedParameters;

        if (parentDefinition instanceof SubResourceDefinition) {
            inheritedParameters = ((SubResourceDefinition) parentDefinition).getParameters();
        } else {
            inheritedParameters = Lists.newArrayList();
        }

        return inheritedParameters;
    }

    private void generateResource() throws UnableToCompleteException {
        SubResourceContext subResourceContext = new SubResourceContext(returnInterface, context, methodDefinition);
        ResourceGenerator generator =
                findGenerator(getLogger(), resourceGeneratorsProvider.get(), subResourceContext);
        ResourceDefinition resourceDefinition = generator.generate(subResourceContext);

        methodDefinition.addResource(resourceDefinition);
    }

    private void generateMethod() throws UnableToCompleteException {
        StringWriter writer = new StringWriter();

        mergeTemplate(writer);

        methodDefinition.setOutput(writer.toString());
    }

    private boolean canGenerateSubResource() {
        SubResourceContext subResourceContext = new SubResourceContext(returnInterface, context, null);
        ResourceGenerator generator =
                findGenerator(getLogger(), resourceGeneratorsProvider.get(), subResourceContext);
        String resourceTypeName = returnInterface.getQualifiedSourceName();

        boolean canGenerate = generator != null;
        if (!canGenerate) {
            getLogger().debug("Cannot find a sub resource generator for `%s`.", resourceTypeName);
        }

        return canGenerate;
    }
}
