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
import com.gwtplatform.dispatch.rest.processors.DispatchRestContextProcessor;
import com.gwtplatform.processors.tools.bindings.BindingContext;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;

import static com.gwtplatform.dispatch.rest.processors.NameFactory.REST_GIN_MODULE;

public class ResourceProcessor extends DispatchRestContextProcessor<Resource, Void> {
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
        Type impl = resource.getImpl();
        Type resourceType = resource.getResource();

        logger.debug("Generating resource implementation `%s`.", impl);

        List<CodeSnippet> processedMethods = processMethods(resource);

        outputter.withTemplateFile(TEMPLATE)
                .withParam("resource", resourceType)
                .withParam("methods", processedMethods)
                .writeTo(impl);

        bindingsProcessors.process(new BindingContext(REST_GIN_MODULE, impl, resourceType, Singleton.class));

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
