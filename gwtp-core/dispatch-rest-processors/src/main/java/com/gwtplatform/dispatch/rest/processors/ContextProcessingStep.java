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

import javax.lang.model.element.Element;

import com.google.auto.common.BasicAnnotationProcessor.ProcessingStep;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;

public class ContextProcessingStep<P extends ContextProcessor<Element, O>, O> implements ProcessingStep {
    private final Logger logger;
    private final ContextProcessors contextProcessors;
    private final Class<? extends Annotation> annotationClass;
    private final boolean ignoreUnresolvableElements;
    private final Class<P> processorClass;
    private final Collection<O> outputs;

    public ContextProcessingStep(
            Logger logger,
            ContextProcessors contextProcessors,
            Class<? extends Annotation> annotationClass,
            boolean ignoreUnresolvableElements,
            Class<P> processorClass) {
        this.logger = logger;
        this.contextProcessors = contextProcessors;
        this.annotationClass = annotationClass;
        this.ignoreUnresolvableElements = ignoreUnresolvableElements;
        this.processorClass = processorClass;
        this.outputs = Collections.synchronizedCollection(new ArrayList<O>());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return Sets.newHashSet(annotationClass);
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        Set<Element> elements = elementsByAnnotation.get(annotationClass);

        try {
            process(elements);
        } catch (UnableToProcessException e) {
            logger.error("Unable to process Rest-Dispatch classes. See previous log entries for details.");
        } catch (Exception e) {
            logger.error("Unresolvable exception.", e);
        }
    }

    private void process(Set<Element> elements) {
        // TODO: Filter elements outside GWT's paths
        for (Element element : elements) {
            process(element);
        }
    }

    private void process(Element element) {
        Optional<P> processor = contextProcessors.getOptionalProcessor(processorClass, element);

        if (processor.isPresent()) {
            O output = processor.get().process(element);

            outputs.add(output);
        }
    }

    public Collection<O> getOutputs() {
        return ImmutableList.copyOf(outputs);
    }
}
