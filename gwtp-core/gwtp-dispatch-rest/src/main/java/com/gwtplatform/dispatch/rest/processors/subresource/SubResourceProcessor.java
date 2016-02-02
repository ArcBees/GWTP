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

import com.gwtplatform.dispatch.rest.processors.DispatchRestContextProcessor;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

public class SubResourceProcessor extends DispatchRestContextProcessor<SubResource, Void> {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/subresource/SubResource.vm";

    private ResourceMethodProcessors methodProcessors;

    @Override
    public void init(Logger logger, Utils utils, Outputter outputter) {
        super.init(logger, utils, outputter);

        methodProcessors = new ResourceMethodProcessors(logger, utils, outputter);
    }

    @Override
    public Void process(SubResource subResource) {
        Type impl = subResource.getType();
        Type subResourceType = subResource.getResourceType();

        logger.debug("Generating sub-resource implementation `%s`.", impl);

        List<CodeSnippet> methods = methodProcessors.processAll(subResource.getMethods());

        outputter.configure(TEMPLATE)
                .withParam("subResourceType", subResourceType)
                .withParam("methods", methods)
                .withParam("fields", subResource.getFields())
                .writeTo(impl);

        return null;
    }
}
