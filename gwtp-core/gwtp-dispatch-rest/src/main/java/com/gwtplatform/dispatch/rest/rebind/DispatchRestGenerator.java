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

package com.gwtplatform.dispatch.rest.rebind;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Lists;
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
import com.gwtplatform.dispatch.rest.rebind.serialization.ActionMetadataProviderGenerator;
import com.gwtplatform.dispatch.rest.rebind.serialization.JacksonMapperProviderGenerator;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.findGenerator;
import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.getGenerator;

public class DispatchRestGenerator extends AbstractGenerator implements GeneratorWithInput<String, ClassDefinition> {
    private final Set<ExtensionGenerator> extensionGenerators;
    private final Set<EntryPointGenerator> entryPointGenerators;
    private final Set<ResourceGenerator> resourceGenerators;
    private final ActionMetadataProviderGenerator actionMetadataProviderGenerator;
    private final JacksonMapperProviderGenerator jacksonMapperProviderGenerator;
    private final GinModuleGenerator ginModuleGenerator;
    private final List<ClassDefinition> extensionDefinitions;

    private List<ResourceDefinition> resourceDefinitions;
    private ClassDefinition metadataProviderDefinition;
    private ClassDefinition jacksonMapperProviderDefinition;
    private ClassDefinition ginModuleDefinition;
    private ClassDefinition entryPointDefinition;

    @Inject
    DispatchRestGenerator(
            Logger logger,
            GeneratorContext context,
            Set<ExtensionGenerator> extensionGenerators,
            Set<EntryPointGenerator> entryPointGenerators,
            Set<ResourceGenerator> resourceGenerators,
            ActionMetadataProviderGenerator actionMetadataProviderGenerator,
            JacksonMapperProviderGenerator jacksonMapperProviderGenerator,
            GinModuleGenerator ginModuleGenerator) {
        super(logger, context);

        this.extensionGenerators = extensionGenerators;
        this.entryPointGenerators = entryPointGenerators;
        this.resourceGenerators = resourceGenerators;
        this.actionMetadataProviderGenerator = actionMetadataProviderGenerator;
        this.jacksonMapperProviderGenerator = jacksonMapperProviderGenerator;
        this.ginModuleGenerator = ginModuleGenerator;
        this.extensionDefinitions = Lists.newArrayList();
    }

    @Override
    public boolean canGenerate(String typeName) {
        return findType(typeName) != null;
    }

    @Override
    public ClassDefinition generate(String typeName) throws UnableToCompleteException {
        executeExtensions(ExtensionPoint.BEFORE_EVERYTHING);

        generateResources();
        generateMetadataProvider();
        generateJacksonMapperProvider();
        generateGinModule();
        generateEntryPoint(typeName);

        executeExtensions(ExtensionPoint.AFTER_EVERYTHING);

        return entryPointDefinition;
    }

    private void executeExtensions(ExtensionPoint extensionPoint) {
        ExtensionContext extensionContext =
                new ExtensionContext(extensionPoint, extensionDefinitions, resourceDefinitions,
                        metadataProviderDefinition, jacksonMapperProviderDefinition, ginModuleDefinition,
                        entryPointDefinition);

        for (ExtensionGenerator generator : extensionGenerators) {
            maybeExecuteExtension(extensionContext, generator);
        }
    }

    private void maybeExecuteExtension(ExtensionContext extensionContext, ExtensionGenerator generator) {
        try {
            if (generator.canGenerate(extensionContext)) {
                Collection<ClassDefinition> definitions = generator.generate(extensionContext);
                extensionDefinitions.addAll(definitions);
            }
        } catch (UnableToCompleteException e) {
            getLogger().warn("Unexpected exception executing extension.", e);
        }
    }

    private void generateResources() throws UnableToCompleteException {
        resourceDefinitions = Lists.newArrayList();
        for (JClassType type : getContext().getTypeOracle().getTypes()) {
            maybeGenerateResource(type);
        }

        executeExtensions(ExtensionPoint.AFTER_RESOURCES);
    }

    private void maybeGenerateResource(JClassType type) throws UnableToCompleteException {
        ResourceContext resourceContext = new ResourceContext(type);
        ResourceGenerator generator = findGenerator(resourceGenerators, resourceContext);

        if (generator != null) {
            ResourceDefinition resourceDefinition = generator.generate(resourceContext);
            resourceDefinitions.add(resourceDefinition);
        }
    }

    private void generateMetadataProvider() throws UnableToCompleteException {
        // TODO: Use multibinder and access through `Generators`
        if (!actionMetadataProviderGenerator.canGenerate()) {
            getLogger().die("Unable to generate metadata provider. See previous log entries.");
        } else {
            metadataProviderDefinition = actionMetadataProviderGenerator.generate();
        }
    }

    private void generateJacksonMapperProvider() throws UnableToCompleteException {
        // TODO: Use multibinder and access through `Generators`
        if (!jacksonMapperProviderGenerator.canGenerate()) {
            getLogger().die("Unable to generate jackson mapper provider. See previous log entries.");
        } else {
            jacksonMapperProviderDefinition = jacksonMapperProviderGenerator.generate();
        }
    }

    private void generateGinModule() throws UnableToCompleteException {
        // TODO: Use multibinder and access through `Generators`
        if (!ginModuleGenerator.canGenerate()) {
            getLogger().die("Unable to generate Gin Module. See previous log entries.");
        } else {
            executeExtensions(ExtensionPoint.BEFORE_GIN);

            ginModuleDefinition = ginModuleGenerator.generate();

            executeExtensions(ExtensionPoint.AFTER_GIN);
        }
    }

    private void generateEntryPoint(String typeName) throws UnableToCompleteException {
        // TODO: Use multibinder and access through `Generators`
        EntryPointGenerator entryPointGenerator = getGenerator(getLogger(), entryPointGenerators, typeName);
        entryPointDefinition = entryPointGenerator.generate(typeName);
    }
}
