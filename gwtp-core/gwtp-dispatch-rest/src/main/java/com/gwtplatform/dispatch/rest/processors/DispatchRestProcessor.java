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

import com.google.auto.service.AutoService;
import com.gwtplatform.dispatch.rest.processors.resource.RootResource;
import com.gwtplatform.dispatch.rest.processors.resource.RootResourceProcessor;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationProcessors;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreElements.isType;
import static com.gwtplatform.processors.tools.logger.Logger.DEBUG_OPTION;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions(DEBUG_OPTION)
public class DispatchRestProcessor extends AbstractProcessor {
    private static final String SEE_LOG =
            "See previous log entries for details or compile with `-A" + DEBUG_OPTION + "` for additional details.";
    private static final String UNABLE_TO_PROCESS_GENERAL = "Unable to process Rest-Dispatch classes. " + SEE_LOG;
    private static final String UNABLE_TO_PROCESS_RESOURCE = "Unable to process resource. " + SEE_LOG;
    private static final String UNRESOLVABLE_EXCEPTION = "Unresolvable exception. " + SEE_LOG;

    private Logger logger;
    private Utils utils;
    private RootResourceProcessor resourceProcessor;
    private SerializationProcessors serializationProcessors;
    private BindingsProcessors bindingsProcessors;

    public DispatchRestProcessor() {
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        logger = new Logger(processingEnv.getMessager(), processingEnv.getOptions());
        utils = new Utils(processingEnv.getTypeUtils(), processingEnv.getElementUtils());
        resourceProcessor = new RootResourceProcessor(processingEnv);
        serializationProcessors = new SerializationProcessors(processingEnv);
        bindingsProcessors = new BindingsProcessors(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Path.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            processPathElements(roundEnv.getElementsAnnotatedWith(Path.class), roundEnv);
        } catch (UnableToProcessException e) {
            logger.error(UNABLE_TO_PROCESS_GENERAL);
        } catch (Exception e) {
            logger.error().throwable(e).log(UNRESOLVABLE_EXCEPTION);
        }

        return false;
    }

    public void processPathElements(Set<? extends Element> elements, RoundEnvironment roundEnv) {
        for (Element element : elements) {
            if (isType(element)) {
                process(element);
            }
        }

        if (roundEnv.processingOver()) {
            processLastRound();
        }
    }

    private void process(Element element) {
        try {
            RootResource resource = new RootResource(logger, utils, element);

            resourceProcessor.process(resource);
        } catch (UnableToProcessException e) {
            logger.mandatoryWarning().context(element).log(UNABLE_TO_PROCESS_RESOURCE, DEBUG_OPTION);
        }
    }

    private void processLastRound() {
        resourceProcessor.processLast();
        serializationProcessors.processLast();
        bindingsProcessors.processLast();
    }
}
