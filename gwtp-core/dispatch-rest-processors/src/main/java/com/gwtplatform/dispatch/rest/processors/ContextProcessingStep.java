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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.google.auto.common.BasicAnnotationProcessor.ProcessingStep;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;

public abstract class ContextProcessingStep<P extends ContextProcessor<Element, O>, O> implements ProcessingStep {
    private Logger logger;
    private ContextProcessors contextProcessors;
    private Collection<O> outputs;

    public synchronized void init(Logger logger, ProcessingEnvironment processingEnvironment) {
        this.logger = logger;
        this.contextProcessors = new ContextProcessors(processingEnvironment, logger);
        this.outputs = Collections.synchronizedCollection(new ArrayList<O>());
    }

    protected abstract Class<P> processorClass();

    protected abstract Class<? extends Annotation> annotation();

    @SuppressWarnings("unchecked")
    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return Sets.newHashSet(annotation());
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        process(elementsByAnnotation.get(annotation()));
    }

    private void process(Set<Element> elements) {
        // TODO: Filter elements outside GWT's paths
        for (Element element : elements) {
            process(element);
        }
    }

    private void process(Element element) {
        Optional<P> processor = contextProcessors.getOptionalProcessor(processorClass(), element);

        if (processor.isPresent()) {
            O output = process(element, processor.get());

            outputs.add(output);
        }
    }

    protected O process(Element element, P processor) {
        return processor.process(element);
    }

    public void processLast() {
        for (P processor : contextProcessors.getProcessors(processorClass())) {
            processor.processLast();
        }
    }

    public Collection<O> getOutputs() {
        return ImmutableList.copyOf(outputs);
    }

    protected Logger getLogger() {
        return logger;
    }
}
