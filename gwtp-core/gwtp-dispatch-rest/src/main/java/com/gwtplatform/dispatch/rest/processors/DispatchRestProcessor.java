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

import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.ws.rs.Path;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.gwtplatform.dispatch.rest.processors.resource.ResourcePostProcessors;
import com.gwtplatform.dispatch.rest.processors.resource.RootResource;
import com.gwtplatform.dispatch.rest.processors.resource.RootResourceFactory;
import com.gwtplatform.dispatch.rest.processors.resource.RootResourceProcessor;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationProcessors;
import com.gwtplatform.processors.tools.AbstractProcessor;
import com.gwtplatform.processors.tools.SupportedAnnotationClasses;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;

import static com.gwtplatform.dispatch.rest.processors.NameUtils.findRestModuleType;
import static com.gwtplatform.processors.tools.bindings.BindingContext.flushModule;

@AutoService(Processor.class)
@SupportedAnnotationClasses(Path.class)
public class DispatchRestProcessor extends AbstractProcessor {
    private static final String UNABLE_TO_PROCESS_RESOURCE = "Unable to process resource.";
    private static final String DISPATCH_MACROS = "com/gwtplatform/dispatch/rest/processors/macros.vm";

    private RootResource.Factory rootResourceFactory;
    private RootResourceProcessor resourceProcessor;
    private ResourcePostProcessors resourcePostProcessors;
    private SerializationProcessors serializationProcessors;
    private BindingsProcessors bindingsProcessors;

    @Override
    protected Set<String> getMacroFiles() {
        return Sets.newHashSet(DISPATCH_MACROS);
    }

    @Override
    protected void initSafe() {
        rootResourceFactory = new RootResourceFactory(logger, utils);
        resourceProcessor = new RootResourceProcessor(logger, utils, outputter);
        resourcePostProcessors = new ResourcePostProcessors(logger, utils, outputter);
        serializationProcessors = new SerializationProcessors(logger, utils, outputter);
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
    }

    @Override
    protected void processSafe(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean elementsProcessed = processGwtElements(roundEnv);

        if (elementsProcessed) {
            bindingsProcessors.process(flushModule(findRestModuleType(utils)));
        }

        maybeProcessLastRound(roundEnv);
    }

    private boolean processGwtElements(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Path.class);
        elements = utils.getSourceFilter().filterElements(elements);

        elements.stream().filter(MoreElements::isType).forEach(this::process);

        return !elements.isEmpty();
    }

    private void process(Element element) {
        try {
            RootResource resource = rootResourceFactory.create(element);

            resourceProcessor.process(resource);
            resourcePostProcessors.process(resource);
        } catch (UnableToProcessException e) {
            logger.mandatoryWarning().context(element).log(UNABLE_TO_PROCESS_RESOURCE);
        }
    }

    private void maybeProcessLastRound(RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            resourceProcessor.processLast();
            serializationProcessors.processLast();
        }
    }
}
