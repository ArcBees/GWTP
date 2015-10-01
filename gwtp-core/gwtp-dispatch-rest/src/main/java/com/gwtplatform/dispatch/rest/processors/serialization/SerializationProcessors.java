/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.processors.serialization;

import java.util.ServiceLoader;

import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

public class SerializationProcessors {
    private static final String NO_SERIALIZATION_POLICIES_FOUND = "Can not find a serialization policy for `%s`.";

    private static ServiceLoader<SerializationProcessor> processors;
    private static boolean initialized;

    private final Logger logger;
    private final Utils utils;
    private final Outputter outputter;

    public SerializationProcessors(
            Logger logger,
            Utils utils,
            Outputter outputter) {
        this.logger = logger;
        this.utils = utils;
        this.outputter = outputter;

        if (processors == null) {
            processors = ServiceLoader.load(SerializationProcessor.class, getClass().getClassLoader());
        }
    }

    public void process(SerializationContext context) {
        ensureInitialized();
        boolean processed = false;

        for (SerializationProcessor processor : processors) {
            if (processor.canProcess(context)) {
                processor.process(context);
                processed = true;
            }
        }

        if (!processed) {
            logger.mandatoryWarning(NO_SERIALIZATION_POLICIES_FOUND, context);
        }
    }

    public void processLast() {
        ensureInitialized();

        for (SerializationProcessor processor : processors) {
            processor.processLast();
        }
    }

    private void ensureInitialized() {
        if (!initialized) {
            for (SerializationProcessor processor : processors) {
                processor.init(logger, utils, outputter);
            }

            initialized = true;
        }
    }
}
