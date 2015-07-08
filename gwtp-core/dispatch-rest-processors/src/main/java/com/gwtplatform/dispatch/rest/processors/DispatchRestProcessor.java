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

import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.BasicAnnotationProcessor.ProcessingStep;
import com.google.auto.service.AutoService;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions(Logger.DEBUG_OPTION)
public class DispatchRestProcessor extends AbstractProcessor {
    private static final String UNABLE_TO_PROCESS_EXCEPTION = "Unable to process Rest-Dispatch classes. See previous "
            + "log entries for details or compile with -A%s for additional details.";
    private static final String UNRESOLVABLE_EXCEPTION =
            "Unresolvable exception. Compile with -A%s for additional details.";

    /**
     * {@link BasicAnnotationProcessor} doesn't give access to the last round ({@link
     * BasicAnnotationProcessor#process(Set, RoundEnvironment)} is private). We delegate the important stuff to it ans
     * use javax' {@link AbstractProcessor}.
     */
    private final BasicAnnotationProcessor delegate = new BasicAnnotationProcessor() {
        @Override
        protected Iterable<? extends ProcessingStep> initSteps() {
            return DispatchRestProcessor.this.initSteps();
        }
    };

    private Logger logger;
    private ServiceLoader<? extends ContextProcessingStep<?, ?>> processingStepsLoader;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return delegate.getSupportedAnnotationTypes();
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        logger = new Logger(processingEnv.getMessager(), processingEnv.getOptions());
        processingStepsLoader =
                ServiceLoader.<ContextProcessingStep<?, ?>>load((Class) ContextProcessingStep.class,
                        getClass().getClassLoader());

        delegate.init(processingEnv);
    }

    private Iterable<? extends ProcessingStep> initSteps() {
        for (ContextProcessingStep<?, ?> processingStep : processingStepsLoader) {
            processingStep.init(logger, processingEnv);
        }

        return processingStepsLoader;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            doProcess(annotations, roundEnv);
        } catch (UnableToProcessException e) {
            logger.error(UNABLE_TO_PROCESS_EXCEPTION, Logger.DEBUG_OPTION);
        } catch (Exception e) {
            logger.error().throwable(e).log(UNRESOLVABLE_EXCEPTION, Logger.DEBUG_OPTION);
        }

        return false;
    }

    public void doProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // TODO: Keep output
        delegate.process(annotations, roundEnv);

        if (roundEnv.processingOver()) {
            processLastRound();
        }
    }

    private void processLastRound() {
        for (ContextProcessingStep<?, ?> processingStep : processingStepsLoader) {
            processingStep.processLast();
        }
    }
}
