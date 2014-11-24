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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.gwtplatform.dispatch.rest.rebind2.entrypoint.EntryPointGenerator;
import com.gwtplatform.dispatch.rest.rebind2.gin.GinModuleGenerator;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;

import static com.gwtplatform.dispatch.rest.rebind2.utils.Generators.getFirstWeightedGeneratorForInput;

public class DispatchRestGenerator extends IncrementalGenerator {
    private static final int VERSION = 14;

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
    public long getVersionId() {
        return VERSION;
    }

    @Override
    public RebindResult generateIncrementally(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new DispatchRestRebindModule(logger, context));
        DispatchRestGenerator generator = injector.getInstance(DispatchRestGenerator.class);
        String resultType = generator.generate(typeName);

        return new RebindResult(RebindMode.USE_ALL_NEW_WITH_NO_CACHING, resultType);
    }

    /**
     * Do the actual generation of Rest-Dispatch compatible code. This should only be called on an instance that has
     * been created through DI.
     */
    private String generate(String typeName) throws UnableToCompleteException {
        EntryPointGenerator entryPointGenerator
                = getFirstWeightedGeneratorForInput(logger, entryPointGenerators, typeName);

        // TODO: Generate services, actions, serializers

        generateGinModule();

        return entryPointGenerator.generate(typeName);
    }

    private void generateGinModule() throws UnableToCompleteException {
        if (!ginModuleGenerator.canGenerate()) {
            logger.die("Unable to generate Gin Module. See previous log entries.");
        } else {
            ginModuleGenerator.generate();
        }
    }
}
