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

package com.gwtplatform.dispatch.rest.processors.subresource;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.DispatchRestContextProcessor;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;

public class SubResourceProcessor extends DispatchRestContextProcessor<SubResource, Void> {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/subresource/SubResource.vm";

    private ResourceMethodProcessors methodProcessors;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        methodProcessors = new ResourceMethodProcessors(processingEnv);
    }

    @Override
    public Void process(SubResource subResource) {
        Type impl = subResource.getImpl();
        Type subResourceType = subResource.getSubResource();

        logger.debug("Generating sub-resource implementation `%s`.", impl);

        List<CodeSnippet> processedMethods = processMethods(subResource);

        outputter.withTemplateFile(TEMPLATE)
                .withParam("resource", subResourceType)
                .withParam("methods", processedMethods)
                .withParam("fields", subResource.getFields())
                .writeTo(impl);

        return null;
    }

    private List<CodeSnippet> processMethods(SubResource subResource) {
        // TODO: Copied from ResourceProcessor
        return FluentIterable.from(subResource.getMethods())
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
