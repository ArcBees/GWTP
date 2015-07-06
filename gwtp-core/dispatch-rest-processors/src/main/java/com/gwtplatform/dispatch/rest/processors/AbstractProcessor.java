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

import java.util.Arrays;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.BasicAnnotationProcessor.ProcessingStep;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;
import com.gwtplatform.dispatch.rest.processors.outputter.Outputter;

public abstract class AbstractProcessor extends BasicAnnotationProcessor implements ProcessingStep {
    protected ContextProcessors contextProcessors;
    protected Outputter outputter;
    protected Logger logger;

    @Override
    protected final Iterable<? extends ProcessingStep> initSteps() {
        contextProcessors = new ContextProcessors(processingEnv);
        outputter = new Outputter(new TypeDefinition(getClass()), processingEnv.getFiler());
        logger = new Logger(processingEnv.getMessager(), processingEnv.getOptions());

        init();

        return Arrays.asList(this);
    }

    protected void init() {
    }
}
