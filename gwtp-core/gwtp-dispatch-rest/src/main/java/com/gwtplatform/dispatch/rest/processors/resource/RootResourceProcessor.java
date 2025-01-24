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

import com.gwtplatform.dispatch.rest.processors.DispatchRestContextProcessor;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import jakarta.inject.Singleton;

import static com.gwtplatform.dispatch.rest.processors.NameUtils.findRestModuleType;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newBinding;

public class RootResourceProcessor extends DispatchRestContextProcessor<RootResource, Void> {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/resource/Resource.vm";

    private BindingsProcessors bindingsProcessors;
    private ResourceMethodProcessors methodProcessors;

    public RootResourceProcessor(Logger logger, Utils utils, Outputter outputter) {
        init(logger, utils, outputter);
    }

    @Override
    public void init(Logger logger, Utils utils, Outputter outputter) {
        super.init(logger, utils, outputter);

        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
        methodProcessors = new ResourceMethodProcessors(logger, utils, outputter);
    }

    @Override
    public Void process(RootResource resource) {
        Type impl = resource.getType();
        Type resourceType = resource.getResourceType();

        logger.debug("Generating resource implementation `%s`.", impl);

        List<CodeSnippet> processedMethods = methodProcessors.processAll(resource.getMethods());

        outputter.configure(TEMPLATE)
                .withParam("resource", resourceType)
                .withParam("methods", processedMethods)
                .writeTo(impl);

        bindingsProcessors.process(newBinding(findRestModuleType(utils), resourceType, impl, Singleton.class));

        return null;
    }

    @Override
    public void processLast() {
        methodProcessors.processLast();
    }
}
