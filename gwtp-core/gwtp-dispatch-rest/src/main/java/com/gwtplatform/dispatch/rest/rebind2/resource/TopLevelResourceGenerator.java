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

package com.gwtplatform.dispatch.rest.rebind2.resource;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.dispatch.rest.rebind2.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind2.events.RegisterGinBindingEvent;
import com.gwtplatform.dispatch.rest.rebind2.utils.Arrays;
import com.gwtplatform.dispatch.rest.rebind2.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;
import com.gwtplatform.dispatch.rest.rebind2.utils.PathResolver;
import com.gwtplatform.dispatch.rest.shared.NoXsrfHeader;

import static com.gwtplatform.dispatch.rest.rebind2.utils.Generators.getFirstGeneratorByWeightAndInput;

public class TopLevelResourceGenerator extends AbstractVelocityGenerator implements ResourceGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind2/resource/Resource.vm";

    private final EventBus eventBus;
    private final Set<ResourceMethodGenerator> resourceMethodGenerators;

    private JClassType resourceType;
    private String resourceTypeName;
    private String packageName;
    private String implName;
    private String path;
    private boolean secured;
    private Set<String> imports;
    private ResourceDefinition resourceDefinition;

    @Inject
    TopLevelResourceGenerator(
            Logger logger,
            GeneratorContext context,
            EventBus eventBus,
            VelocityEngine velocityEngine,
            Set<ResourceMethodGenerator> resourceMethodGenerators) {
        super(logger, context, velocityEngine);

        this.eventBus = eventBus;
        this.resourceMethodGenerators = resourceMethodGenerators;
    }

    @Override
    public boolean canGenerate(JClassType resourceType) throws UnableToCompleteException {
        return resourceType.isInterface() != null && resourceType.isAnnotationPresent(Path.class);
    }

    @Override
    public ResourceDefinition generate(JClassType resourceType) throws UnableToCompleteException {
        this.resourceType = resourceType;

        resolveImplName();
        resolvePath();
        resolveSecured();

        resourceDefinition = new ResourceDefinition(resourceType, packageName, implName, path, secured);

        PrintWriter printWriter = tryCreate();
        if (printWriter != null) {
            imports = Sets.newTreeSet();
            imports.add(resourceType.getQualifiedSourceName());

            generateMethods();

            mergeTemplate(printWriter);
            getContext().commit(getLogger(), printWriter);

            registerResourceBinding();
        }

        return resourceDefinition;
    }

    @Override
    protected Map<String, Object> createTemplateVariables() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("imports", imports);
        variables.put("resourceType", resourceTypeName);
        variables.put("methods", resourceDefinition.getMethodDefinitions());

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
        return implName;
    }

    private void resolveImplName() {
        // TODO: Maybe try moving package from shared to client. May cause issues if there are no client sources

        this.resourceTypeName = resourceType.getSimpleSourceName();
        this.packageName = resourceType.getPackage().getName();
        this.implName = resourceTypeName + IMPL;
    }

    private void resolvePath() {
        path = PathResolver.resolve(resourceType);
    }

    private void resolveSecured() {
        secured = !resourceType.isAnnotationPresent(NoXsrfHeader.class);
    }

    private void generateMethods() throws UnableToCompleteException {
        List<JMethod> methods = Arrays.asList(resourceType.getInheritableMethods());

        for (JMethod method : methods) {
            generateMethod(method);
        }
    }

    private void generateMethod(JMethod method) throws UnableToCompleteException {
        ResourceMethodContext context = new ResourceMethodContext(resourceDefinition, method);
        ResourceMethodGenerator generator =
                getFirstGeneratorByWeightAndInput(getLogger(), resourceMethodGenerators, context);
        ResourceMethodDefinition methodDefinition = generator.generate(context);

        resourceDefinition.addMethodDefinition(methodDefinition);
        imports.addAll(methodDefinition.getImports());
    }

    private void registerResourceBinding() {
        RegisterGinBindingEvent event =
                new RegisterGinBindingEvent(new ClassDefinition(resourceType), getClassDefinition(), true);
        eventBus.post(event);
    }
}
