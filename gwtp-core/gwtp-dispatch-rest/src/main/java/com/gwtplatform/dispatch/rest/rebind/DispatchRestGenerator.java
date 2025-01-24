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

package com.gwtplatform.dispatch.rest.rebind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.gwtplatform.dispatch.rest.rebind.entrypoint.EntryPointGenerator;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionContext;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionGenerator;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionPoint;
import com.gwtplatform.dispatch.rest.rebind.gin.GinModuleGenerator;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceContext;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceDefinition;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceGenerator;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

import jakarta.inject.Inject;

import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.findGenerator;
import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.findGeneratorWithoutInput;

public class DispatchRestGenerator extends AbstractGenerator implements GeneratorWithInput<String, ExtensionContext> {
    private final Set<ExtensionGenerator> extensionGenerators;
    private final Set<EntryPointGenerator> entryPointGenerators;
    private final Set<ResourceGenerator> resourceGenerators;
    private final Set<GinModuleGenerator> ginModuleGenerators;
    private final List<ClassDefinition> extensionDefinitions;

    private List<ResourceDefinition> resourceDefinitions;
    private ClassDefinition ginModuleDefinition;
    private ClassDefinition entryPointDefinition;

    @Inject
    DispatchRestGenerator(
            Logger logger,
            GeneratorContext context,
            Set<ExtensionGenerator> extensionGenerators,
            Set<EntryPointGenerator> entryPointGenerators,
            Set<ResourceGenerator> resourceGenerators,
            Set<GinModuleGenerator> ginModuleGenerators) {
        super(logger, context);

        this.extensionGenerators = extensionGenerators;
        this.entryPointGenerators = entryPointGenerators;
        this.resourceGenerators = resourceGenerators;
        this.ginModuleGenerators = ginModuleGenerators;
        this.extensionDefinitions = new ArrayList<ClassDefinition>();
    }

    @Override
    public boolean canGenerate(String typeName) {
        return findType(typeName) != null;
    }

    @Override
    public ExtensionContext generate(String typeName) throws UnableToCompleteException {
        executeExtensions(ExtensionPoint.BEFORE_EVERYTHING);

        generateResources();
        executeExtensions(ExtensionPoint.AFTER_RESOURCES);

        executeExtensions(ExtensionPoint.BEFORE_GIN);
        generateGinModule();
        executeExtensions(ExtensionPoint.AFTER_GIN);

        generateEntryPoint(typeName);

        executeExtensions(ExtensionPoint.AFTER_EVERYTHING);

        return createExtensionContext(null);
    }

    private void generateResources() throws UnableToCompleteException {
        resourceDefinitions = new ArrayList<ResourceDefinition>();
        for (JClassType type : getContext().getTypeOracle().getTypes()) {
            maybeGenerateResource(type);
        }
    }

    private void maybeGenerateResource(JClassType type) throws UnableToCompleteException {
        ResourceContext resourceContext = new ResourceContext(type);
        ResourceGenerator generator = findGenerator(resourceGenerators, resourceContext);

        if (generator != null) {
            ResourceDefinition resourceDefinition = generator.generate(resourceContext);
            resourceDefinitions.add(resourceDefinition);
        }
    }

    private void generateGinModule() throws UnableToCompleteException {
        GinModuleGenerator generator = findGeneratorWithoutInput(ginModuleGenerators);

        if (generator != null) {
            ginModuleDefinition = generator.generate();
        } else {
            getLogger().die("No gin module generators was found.");
        }
    }

    private void generateEntryPoint(String typeName) throws UnableToCompleteException {
        EntryPointGenerator generator = findGenerator(entryPointGenerators, typeName);

        if (generator != null) {
            entryPointDefinition = generator.generate(typeName);
        } else {
            getLogger().die("No entry point generators was found.");
        }
    }

    private void executeExtensions(ExtensionPoint extensionPoint) {
        ExtensionContext extensionContext = createExtensionContext(extensionPoint);

        for (ExtensionGenerator generator : extensionGenerators) {
            maybeExecuteExtension(extensionContext, generator);
        }
    }

    private void maybeExecuteExtension(ExtensionContext extensionContext, ExtensionGenerator generator) {
        if (generator.canGenerate(extensionContext)) {
            try {
                Collection<ClassDefinition> definitions = generator.generate(extensionContext);
                extensionDefinitions.addAll(definitions);
            } catch (UnableToCompleteException e) {
                getLogger().error("Unexpected exception executing extension.", e);
            }
        }
    }

    private ExtensionContext createExtensionContext(ExtensionPoint extensionPoint) {
        return new ExtensionContext(extensionPoint, extensionDefinitions, resourceDefinitions, ginModuleDefinition,
                entryPointDefinition);
    }
}
