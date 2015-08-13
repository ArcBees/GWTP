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

package com.gwtplatform.dispatch.rest.processors.resource;

import java.util.ServiceLoader;

import javax.annotation.processing.ProcessingEnvironment;

import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;

public class ResourceMethodProcessors {
    private static final String NO_PROCESSORS_FOUND = "Can not find a resource method processor for `%s`.";

    private static ServiceLoader<ResourceMethodProcessor> processors;

    private final ProcessingEnvironment environment;
    private final Logger logger;

    public ResourceMethodProcessors(ProcessingEnvironment environment) {
        this.environment = environment;
        this.logger = new Logger(environment.getMessager(), environment.getOptions());

        if (processors == null) {
            processors = ServiceLoader.load(ResourceMethodProcessor.class, getClass().getClassLoader());
        }
    }

    public CodeSnippet process(ResourceMethod context) {
        for (ResourceMethodProcessor processor : processors) {
            if (processor.canProcess(context)) {
                ensureInitialized(processor);

                return processor.process(context);
            }
        }

        logger.error(NO_PROCESSORS_FOUND, context.getMethod().getName());
        throw new UnableToProcessException();
    }

    public void processLast() {
        for (ResourceMethodProcessor processor : processors) {
            ensureInitialized(processor);

            processor.processLast();
        }
    }

    private void ensureInitialized(ResourceMethodProcessor processor) {
        if (!processor.isInitialized()) {
            processor.init(environment);
        }
    }
}