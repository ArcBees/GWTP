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

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.IncrementalGenerator;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.RebindMode;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;

public class DispatchRestIncrementalGenerator extends IncrementalGenerator {
    private static final String GENERATOR_EXTENSIONS_PROPERTY = "gwtp.dispatch.rest.generatorModules";
    private static final int VERSION = 140;
    private static final Stage STAGE = Stage.PRODUCTION;

    private TreeLogger logger;
    private GeneratorContext context;
    private String typeName;
    private ClassDefinition lastGeneration;

    @Override
    public long getVersionId() {
        return VERSION;
    }

    @Override
    public RebindResult generateIncrementally(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        if (lastGeneration != null) {
            // Prevents unnecessary calls to the generator for every permutations.
            // TODO: Try to really optimize for incremental generators (if possible at all)
            return new RebindResult(RebindMode.USE_ALL_CACHED, lastGeneration.getQualifiedName());
        }

        this.logger = logger;
        this.context = context;
        this.typeName = typeName;

        Injector injector = createInjector();
        DispatchRestGenerator generator = injector.getInstance(DispatchRestGenerator.class);
        lastGeneration = generate(generator);

        return new RebindResult(RebindMode.USE_ALL_NEW, lastGeneration.getQualifiedName());
    }

    private ClassDefinition generate(DispatchRestGenerator generator)
            throws UnableToCompleteException {
        if (!generator.canGenerate(typeName)) {
            logger.log(Type.ERROR, "Unable to locate " + typeName + ". Cannot initiate Rest-Dispatch generation.");
            throw new UnableToCompleteException();
        }

        return generator.generate(typeName).getEntryPointDefinition();
    }

    private Injector createInjector() {
        List<Module> modules = Lists.newArrayList();
        modules.add(new DispatchRestRebindModule(logger, context));

        loadExtensionModules(modules);

        return Guice.createInjector(STAGE, modules);
    }

    private void loadExtensionModules(List<Module> modules) {
        PropertyOracle propertyOracle = context.getPropertyOracle();

        try {
            ConfigurationProperty property = propertyOracle.getConfigurationProperty(GENERATOR_EXTENSIONS_PROPERTY);

            loadExtensionModules(property, modules);
        } catch (BadPropertyValueException e) {
            logger.log(Type.WARN, "Unable to read Dispatch-Rest generator extensions property. Extensions won't be "
                    + "loaded. Make sure '" + GENERATOR_EXTENSIONS_PROPERTY + "' is defined.", e);
        }
    }

    private void loadExtensionModules(ConfigurationProperty property, List<Module> modules) {
        List<String> moduleNames = property.getValues();
        for (String moduleName : moduleNames) {
            loadExtensionModule(moduleName, modules);
        }
    }

    private void loadExtensionModule(String moduleName, List<Module> modules) {
        try {
            Class<? extends Module> moduleClass = Class.forName(moduleName).asSubclass(Module.class);
            Module module = moduleClass.newInstance();

            modules.add(module);

            logger.log(Type.DEBUG, "Dispatch-Rest generator extension loaded '" + moduleName + "'.");
        } catch (Exception e) {
            logger.log(Type.WARN, "Unable to load extension module '" + moduleName + "'.");
        }
    }
}
