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

package com.gwtplatform.mvp.processors.proxy;

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

import com.google.auto.service.AutoService;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.processors.proxy.ProxyDetails.Factory;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.processors.tools.GwtSourceFilter.GWTP_MODULE_OPTION;
import static com.gwtplatform.processors.tools.logger.Logger.DEBUG_OPTION;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({DEBUG_OPTION, GWTP_MODULE_OPTION})
public class ProxyProcessorEntry extends AbstractProcessor {
    private static final String UNABLE_TO_PROCESS_PROXY = "Unable to process proxy.";
    private static final String UNRESOLVABLE_EXCEPTION = "Unresolvable exception.";

    private Logger logger;
    private Outputter outputter;
    private Utils utils;

    private Factory proxyFactory;

    private BindingsProcessors bindingsProcessors;
    private ProxyProcessors proxyProcessors;
    private ProxyModules proxyModules;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ProxyStandard.class.getCanonicalName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        initializeTools(processingEnv);
        initializeDomainFactories();
        initializeProcessors();
    }

    private void initializeTools(ProcessingEnvironment processingEnv) {
        logger = new Logger(processingEnv.getMessager(), processingEnv.getOptions());
        utils = new Utils(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils(),
                processingEnv.getOptions());
        outputter = new Outputter(logger, this, processingEnv.getFiler());
    }

    private void initializeDomainFactories() {
        proxyFactory = new ProxyDetailsFactory(logger, utils);
    }

    private void initializeProcessors() {
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
        proxyModules = new ProxyModules(utils, bindingsProcessors);
        proxyProcessors = new ProxyProcessors(logger, utils, outputter, proxyModules);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            utils.incrementRoundNumber();
            process(roundEnv);
        } catch (UnableToProcessException e) {
            logger.error(UNRESOLVABLE_EXCEPTION);
        } catch (Exception e) {
            logger.error().throwable(e).log(UNRESOLVABLE_EXCEPTION);
        }

        return false;
    }

    private void process(RoundEnvironment roundEnv) {
        boolean elementsProcessed = processGwtElements(roundEnv);

        if (elementsProcessed) {
            proxyModules.flush();
        }

        maybeProcessLastRound(roundEnv);
    }

    private boolean processGwtElements(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ProxyStandard.class);
        elements = utils.getSourceFilter().filterElements(elements);

        for (Element element : elements) {
            process(element);
        }

        return !elements.isEmpty();
    }

    private void process(Element element) {
        try {
            ProxyDetails proxyDetails = proxyFactory.create(element);

            proxyProcessors.process(proxyDetails);
        } catch (UnableToProcessException e) {
            logger.mandatoryWarning().context(element).log(UNABLE_TO_PROCESS_PROXY);
        }
    }

    private void maybeProcessLastRound(RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            proxyProcessors.processLast();
            bindingsProcessors.processLast();
        }
    }
}
