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

package com.gwtplatform.dispatch.rest.rebind2;

import java.util.Set;

import javax.inject.Inject;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.IncrementalGenerator;
import com.google.gwt.core.ext.RebindMode;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.gwtplatform.dispatch.rest.rebind2.entrypoint.EntryPointGenerator;
import com.gwtplatform.dispatch.rest.rebind2.gin.GinModuleGenerator;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceGenerator;
import com.gwtplatform.dispatch.rest.rebind2.serialization.ActionMetadataProviderGenerator;
import com.gwtplatform.dispatch.rest.rebind2.serialization.JacksonMapperProviderGenerator;
import com.gwtplatform.dispatch.rest.rebind2.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;

import static com.gwtplatform.dispatch.rest.rebind2.utils.Generators.findFirstGeneratorByWeightAndInput;
import static com.gwtplatform.dispatch.rest.rebind2.utils.Generators.getFirstGeneratorByWeightAndInput;

public class DispatchRestGenerator extends IncrementalGenerator implements GeneratorWithInput<String, ClassDefinition> {
    private static final int VERSION = 14;

    private final Logger logger;
    private final GeneratorContext context;
    private final Set<EntryPointGenerator> entryPointGenerators;
    private final Set<ResourceGenerator> resourceGenerators;
    private final ActionMetadataProviderGenerator actionMetadataProviderGenerator;
    private final JacksonMapperProviderGenerator jacksonMapperProviderGenerator;
    private final GinModuleGenerator ginModuleGenerator;

    /**
     * This constructor is used by GWT to create the generator.
     */
    @SuppressWarnings("UnusedDeclaration")
    public DispatchRestGenerator() {
        logger = null;
        context = null;
        entryPointGenerators = null;
        resourceGenerators = null;
        actionMetadataProviderGenerator = null;
        jacksonMapperProviderGenerator = null;
        ginModuleGenerator = null;
    }

    @Inject
    DispatchRestGenerator(
            Logger logger,
            GeneratorContext context,
            Set<EntryPointGenerator> entryPointGenerators,
            Set<ResourceGenerator> resourceGenerators,
            ActionMetadataProviderGenerator actionMetadataProviderGenerator,
            JacksonMapperProviderGenerator jacksonMapperProviderGenerator,
            GinModuleGenerator ginModuleGenerator) {
        this.logger = logger;
        this.context = context;
        this.entryPointGenerators = entryPointGenerators;
        this.resourceGenerators = resourceGenerators;
        this.actionMetadataProviderGenerator = actionMetadataProviderGenerator;
        this.jacksonMapperProviderGenerator = jacksonMapperProviderGenerator;
        this.ginModuleGenerator = ginModuleGenerator;
    }

    @Override
    public long getVersionId() {
        return VERSION;
    }

    @Override
    public RebindResult generateIncrementally(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new DispatchRestRebindModule(logger, context));
        DispatchRestGenerator generator = injector.getInstance(DispatchRestGenerator.class);
        ClassDefinition result = generator.generate(typeName);

        return new RebindResult(RebindMode.USE_ALL_NEW_WITH_NO_CACHING, result.toString());
    }

    @Override
    public boolean canGenerate(String typeName) throws UnableToCompleteException {
        return context.getTypeOracle().findType(typeName) != null;
    }

    @Override
    public ClassDefinition generate(String typeName) throws UnableToCompleteException {
        EntryPointGenerator entryPointGenerator
                = getFirstGeneratorByWeightAndInput(logger, entryPointGenerators, typeName);

        // TODO: Store the returned definitions, so it's possible to run "enhancer".
        // Maybe allow them to run before everything. After resources. Before GIN. After GIN.
        generateResources();
        generateMetadataProvider();
        generateJacksonMapperProvider();
        generateGinModule();

        return entryPointGenerator.generate(typeName);
    }

    private void generateResources() throws UnableToCompleteException {
        for (JClassType type : context.getTypeOracle().getTypes()) {
            maybeGenerateResource(type);
        }
    }

    private void maybeGenerateResource(JClassType type) throws UnableToCompleteException {
        ResourceGenerator generator = findFirstGeneratorByWeightAndInput(logger, resourceGenerators, type);

        if (generator != null) {
            generator.generate(type);
        }
    }

    private void generateMetadataProvider() throws UnableToCompleteException {
        if (!actionMetadataProviderGenerator.canGenerate()) {
            logger.die("Unable to generate metadata provider. See previous log entries.");
        } else {
            actionMetadataProviderGenerator.generate();
        }
    }

    private void generateJacksonMapperProvider() throws UnableToCompleteException {
        if (!jacksonMapperProviderGenerator.canGenerate()) {
            logger.die("Unable to generate jackson mapper provider. See previous log entries.");
        } else {
            jacksonMapperProviderGenerator.generate();
        }
    }

    private void generateGinModule() throws UnableToCompleteException {
        if (!ginModuleGenerator.canGenerate()) {
            logger.die("Unable to generate Gin Module. See previous log entries.");
        } else {
            ginModuleGenerator.generate();
        }
    }
}
