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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.ws.rs.Path;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.BasicAnnotationProcessor.ProcessingStep;
import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.google.common.collect.SetMultimap;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceProcessor;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions(Logger.DEBUG_OPTION)
public class DispatchRestProcessor extends AbstractProcessor implements ProcessingStep {
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
            return Collections.singletonList(DispatchRestProcessor.this);
        }
    };

    private Logger logger;
    private ContextProcessors contextProcessors;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        logger = new Logger(processingEnv.getMessager(), processingEnv.getOptions());
        contextProcessors = new ContextProcessors(processingEnv, logger);

        delegate.init(processingEnv);
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return Collections.singleton(Path.class);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return delegate.getSupportedAnnotationTypes();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            delegateProcess(annotations, roundEnv);
        } catch (UnableToProcessException e) {
            logger.error(UNABLE_TO_PROCESS_EXCEPTION, Logger.DEBUG_OPTION);
        } catch (Exception e) {
            logger.error().throwable(e).log(UNRESOLVABLE_EXCEPTION, Logger.DEBUG_OPTION);
        }

        return false;
    }

    public void delegateProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        delegate.process(annotations, roundEnv);

        if (roundEnv.processingOver()) {
            processLastRound();
        }
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        // TODO: Filter elements outside GWT's paths
        for (Element element : elementsByAnnotation.get(Path.class)) {
            process(element);
        }
    }

    private void process(Element element) {
        Optional<ResourceProcessor> processor =
                contextProcessors.getOptionalProcessor(ResourceProcessor.class, element);

        if (processor.isPresent()) {
            processor.get().process(element);
        }
    }

    private void processLastRound() {
        for (ContextProcessor<?, ?> processor : contextProcessors.getAllProcessors()) {
            processor.processLast();
        }
    }
}
