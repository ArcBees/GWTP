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

package com.gwtplatform.dispatch.rest.processors.bindings;

import java.util.ServiceLoader;

import javax.annotation.processing.ProcessingEnvironment;

import com.gwtplatform.dispatch.rest.processors.logger.Logger;

public class BindingsProcessors {
    private static final String NO_BINDING_POLICIES_FOUND = "Can not find a binding policy for `%s`.";

    private static ServiceLoader<BindingsProcessor> processors;

    private final ProcessingEnvironment environment;
    private final Logger logger;

    public BindingsProcessors(ProcessingEnvironment environment) {
        this.environment = environment;
        this.logger = new Logger(environment.getMessager(), environment.getOptions());

        if (processors == null) {
            processors = ServiceLoader.load(BindingsProcessor.class, getClass().getClassLoader());
        }
    }

    public void process(BindingContext context) {
        boolean processed = false;

        for (BindingsProcessor processor : processors) {
            if (processor.canProcess(context)) {
                ensureInitialized(processor);

                processor.process(context);
                processed = true;
            }
        }

        if (!processed) {
            logger.mandatoryWarning(NO_BINDING_POLICIES_FOUND, context);
        }
    }

    public void processLast() {
        for (BindingsProcessor processor : processors) {
            ensureInitialized(processor);

            processor.processLast();
        }
    }

    private void ensureInitialized(BindingsProcessor processor) {
        if (!processor.isInitialized()) {
            processor.init(environment);
        }
    }
}
