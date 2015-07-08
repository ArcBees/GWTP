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

package com.gwtplatform.dispatch.rest.processors;

import javax.annotation.processing.ProcessingEnvironment;

import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;
import com.gwtplatform.dispatch.rest.processors.outputter.Outputter;

public abstract class AbstractContextProcessor<I, O> implements ContextProcessor<I, O> {
    protected ProcessingEnvironment processingEnv;
    protected Outputter outputter;
    protected Logger logger;

    private boolean initialized;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.outputter = new Outputter(logger, new TypeDefinition(getClass()), processingEnv.getFiler());
        this.logger = new Logger(processingEnv.getMessager(), processingEnv.getOptions());

        init();

        this.initialized = true;
    }

    protected void init() {
    }

    @Override
    public void processLast() {
    }

    @Override
    public final boolean isInitialized() {
        return initialized;
    }
}
