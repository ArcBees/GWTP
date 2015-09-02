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

import javax.annotation.processing.ProcessingEnvironment;

import com.gwtplatform.processors.tools.logger.Logger;

public class SerializationProcessors {
    private static final String NO_SERIALIZATION_POLICIES_FOUND = "Can not find a serialization policy for `%s`.";

    private static ServiceLoader<SerializationProcessor> processors;
    private static boolean processedLast;

    private final ProcessingEnvironment environment;
    private final Logger logger;

    public SerializationProcessors(ProcessingEnvironment environment) {
        this.environment = environment;
        this.logger = new Logger(environment.getMessager(), environment.getOptions());

        if (processors == null) {
            processors = ServiceLoader.load(SerializationProcessor.class, getClass().getClassLoader());
        }
    }

    public void process(SerializationContext context) {
        boolean processed = false;

        for (SerializationProcessor processor : processors) {
            if (processor.canProcess(context)) {
                ensureInitialized(processor);

                processor.process(context);
                processed = true;
            }
        }

        if (!processed) {
            logger.mandatoryWarning(NO_SERIALIZATION_POLICIES_FOUND, context);
        }
    }

    public void processLast() {
        // TODO: Better handling of processLast
        if (!processedLast) {
            processedLast = true;
            for (SerializationProcessor processor : processors) {
                ensureInitialized(processor);

                processor.processLast();
            }
        }
    }

    private void ensureInitialized(SerializationProcessor processor) {
        if (!processor.isInitialized()) {
            processor.init(environment);
        }
    }
}
