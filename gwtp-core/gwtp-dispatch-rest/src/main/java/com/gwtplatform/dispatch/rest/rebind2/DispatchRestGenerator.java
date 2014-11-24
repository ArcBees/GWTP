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
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.gwtplatform.dispatch.rest.rebind2.entrypoint.EntryPointGenerator;
import com.gwtplatform.dispatch.rest.rebind2.gin.GinModuleGenerator;
import com.gwtplatform.dispatch.rest.rebind2.utils.Generators;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;

public class DispatchRestGenerator extends com.google.gwt.core.ext.Generator {
    private final Logger logger;
    private final Set<EntryPointGenerator> entryPointGenerators;
    private final GinModuleGenerator ginModuleGenerator;

    /**
     * This constructor is used by GWT to create the generator.
     */
    public DispatchRestGenerator() {
        logger = null;
        entryPointGenerators = null;
        ginModuleGenerator = null;
    }

    @Inject
    DispatchRestGenerator(
            Logger logger,
            Set<EntryPointGenerator> entryPointGenerators,
            GinModuleGenerator ginModuleGenerator) {
        this.logger = logger;
        this.entryPointGenerators = entryPointGenerators;
        this.ginModuleGenerator = ginModuleGenerator;
    }

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new DispatchRestRebindModule(logger, context));
        DispatchRestGenerator generator = injector.getInstance(DispatchRestGenerator.class);

        return generator.generate(typeName);
    }

    private String generate(String typeName) throws UnableToCompleteException {
        EntryPointGenerator entryPointGenerator = getGeneratorForType(entryPointGenerators, typeName);

        // TODO: Generate services, actions, serializers

        ginModuleGenerator.generate();

        return entryPointGenerator.generate(typeName);
    }

    private <T extends Generator> T getGeneratorForType(Set<T> generators, String typeName)
            throws UnableToCompleteException {
        return Generators.getGeneratorForType(logger, generators, typeName);
    }
}
