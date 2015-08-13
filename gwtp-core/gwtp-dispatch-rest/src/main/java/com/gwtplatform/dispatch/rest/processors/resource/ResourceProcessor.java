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

package com.gwtplatform.dispatch.rest.processors.resource;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.AbstractContextProcessor;
import com.gwtplatform.dispatch.rest.processors.bindings.BindingContext;
import com.gwtplatform.dispatch.rest.processors.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;

public class ResourceProcessor extends AbstractContextProcessor<Resource, Void> {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/resource/Resource.vm";

    private BindingsProcessors bindingsProcessors;
    private ResourceMethodProcessors methodProcessors;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        bindingsProcessors = new BindingsProcessors(processingEnv);
        methodProcessors = new ResourceMethodProcessors(processingEnv);
    }

    @Override
    public Void process(Resource resource) {
        logger.debug("Generating resource implementation `%s`.", resource.getImpl());

        List<CodeSnippet> processedMethods = processMethods(resource);

        outputter.withTemplateFile(TEMPLATE)
                .withParam("resource", resource.getResource())
                .withParam("methods", processedMethods)
                .writeTo(resource.getImpl());

        bindingsProcessors.process(new BindingContext(resource.getImpl(), resource.getResource(), Singleton.class));

        return null;
    }

    private List<CodeSnippet> processMethods(Resource resource) {
        return FluentIterable.from(resource.getMethods())
                .transform(new Function<ResourceMethod, CodeSnippet>() {
                    @Override
                    public CodeSnippet apply(ResourceMethod resourceMethod) {
                        return methodProcessors.process(resourceMethod);
                    }
                })
                .toList();
    }

    @Override
    public void processLast() {
        methodProcessors.processLast();
    }
}
