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

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.processors.bundle.NamedProviderBundleProcessor;
import com.gwtplatform.mvp.processors.proxy.ProxyDetails.Factory;
import com.gwtplatform.processors.tools.AbstractProcessor;
import com.gwtplatform.processors.tools.SupportedAnnotationClasses;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;

@AutoService(Processor.class)
@SupportedAnnotationClasses({ProxyStandard.class, ProxyCodeSplit.class, ProxyCodeSplitBundle.class})
public class MainProxyProcessor extends AbstractProcessor {
    private static final String PROXY_MACROS = "com/gwtplatform/mvp/processors/proxy/macros.vm";
    private static final String UNABLE_TO_PROCESS_PROXY = "Unable to process proxy.";

    private Factory proxyFactory;

    private ProxyProcessors proxyProcessors;
    private ProxyModules proxyModules;
    private NamedProviderBundleProcessor providerBundleProcessor;

    @Override
    protected Set<String> getMacroFiles() {
        return Sets.newHashSet(PROXY_MACROS);
    }

    @Override
    protected void initSafe() {
        proxyFactory = new ProxyDetailsFactory(logger, utils);
        proxyModules = new ProxyModules(utils, new BindingsProcessors(logger, utils, outputter));
        providerBundleProcessor = new NamedProviderBundleProcessor();
        proxyProcessors = new ProxyProcessors(logger, utils, outputter, proxyModules, providerBundleProcessor);

        providerBundleProcessor.init(logger, utils, outputter);
    }

    @Override
    protected void processSafe(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean elementsProcessed = processGwtElements(roundEnv);

        if (elementsProcessed) {
            proxyModules.flush();
            providerBundleProcessor.flush();
        }

        maybeProcessLastRound(roundEnv);
    }

    private boolean processGwtElements(RoundEnvironment roundEnv) {
        Set<Element> elements = new HashSet<>(roundEnv.getElementsAnnotatedWith(ProxyStandard.class));
        elements.addAll(roundEnv.getElementsAnnotatedWith(ProxyCodeSplit.class));
        elements.addAll(roundEnv.getElementsAnnotatedWith(ProxyCodeSplitBundle.class));

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
            providerBundleProcessor.processLast();
            proxyProcessors.processLast();
        }
    }
}
