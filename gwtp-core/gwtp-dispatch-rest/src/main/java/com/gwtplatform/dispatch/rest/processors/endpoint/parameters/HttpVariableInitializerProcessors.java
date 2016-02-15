/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.processors.endpoint.parameters;

import java.util.Collections;
import java.util.ServiceLoader;

import com.gwtplatform.dispatch.rest.processors.details.HttpVariable;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

public class HttpVariableInitializerProcessors {
    private static final String UNSUPPORTED_VARIABLE_TYPE = "Can not find a processor for HTTP variable of type `%s`.";

    private static ServiceLoader<HttpVariableInitializerProcessor> processors;
    private static boolean initialized;

    private final Logger logger;
    private final Utils utils;
    private final Outputter outputter;

    public HttpVariableInitializerProcessors(
            Logger logger,
            Utils utils,
            Outputter outputter) {
        this.logger = logger;
        this.utils = utils;
        this.outputter = outputter;

        if (processors == null) {
            processors = ServiceLoader.load(HttpVariableInitializerProcessor.class, getClass().getClassLoader());
        }
    }

    public CodeSnippet process(HttpVariable variable) {
        ensureInitialized();

        for (HttpVariableInitializerProcessor processor : processors) {
            if (processor.canProcess(variable)) {
                return processor.process(variable);
            }
        }

        logger.mandatoryWarning(UNSUPPORTED_VARIABLE_TYPE, variable.getHttpAnnotation().get().getParameterType());
        return new CodeSnippet("null", Collections.<String>emptyList());
    }

    public void processLast() {
        ensureInitialized();

        for (HttpVariableInitializerProcessor processor : processors) {
            processor.processLast();
        }
    }

    private void ensureInitialized() {
        if (!initialized) {
            for (HttpVariableInitializerProcessor processor : processors) {
                processor.init(logger, utils, outputter);
            }

            initialized = true;
        }
    }
}
