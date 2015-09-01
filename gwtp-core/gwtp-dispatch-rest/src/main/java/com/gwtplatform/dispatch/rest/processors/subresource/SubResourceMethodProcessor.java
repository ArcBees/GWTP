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

import javax.annotation.processing.ProcessingEnvironment;

import com.google.auto.service.AutoService;
import com.gwtplatform.processors.tools.AbstractContextProcessor;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodProcessor;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;

import static com.gwtplatform.dispatch.rest.processors.NameFactory.methodName;

@AutoService(ResourceMethodProcessor.class)
public class SubResourceMethodProcessor extends AbstractContextProcessor<ResourceMethod, CodeSnippet>
        implements ResourceMethodProcessor {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/subresource/SubResourceMethod.vm";

    private final SubResourceProcessor subResourceProcessor;

    public SubResourceMethodProcessor() {
        subResourceProcessor = new SubResourceProcessor();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        subResourceProcessor.init(processingEnv);
    }

    @Override
    public boolean canProcess(ResourceMethod method) {
        return method instanceof SubResourceMethod;
    }

    @Override
    public CodeSnippet process(ResourceMethod resourceMethod) {
        String methodName = methodName(resourceMethod);

        logger.debug("Generating sub-resource method `%s`.", methodName);

        return null;
    }

    @Override
    public void processLast() {
        subResourceProcessor.processLast();
    }
}
