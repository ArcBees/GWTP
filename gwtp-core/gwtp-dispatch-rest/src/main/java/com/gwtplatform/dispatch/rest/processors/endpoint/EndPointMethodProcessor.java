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

package com.gwtplatform.dispatch.rest.processors.endpoint;

import com.google.auto.service.AutoService;
import com.gwtplatform.dispatch.rest.processors.DispatchRestContextProcessor;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodProcessor;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.dispatch.rest.processors.NameUtils.qualifiedMethodName;

@AutoService(ResourceMethodProcessor.class)
public class EndPointMethodProcessor extends DispatchRestContextProcessor<ResourceMethod, CodeSnippet>
        implements ResourceMethodProcessor {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/endpoint/EndPointMethod.vm";

    private final EndPointProcessor endPointProcessor;

    public EndPointMethodProcessor() {
        endPointProcessor = new EndPointProcessor();
    }

    @Override
    public synchronized void init(Logger logger, Utils utils, Outputter outputter) {
        super.init(logger, utils, outputter);

        endPointProcessor.init(logger, utils, outputter);
    }

    @Override
    public boolean canProcess(ResourceMethod method) {
        return method instanceof EndPointMethod;
    }

    @Override
    public CodeSnippet process(ResourceMethod resourceMethod) {
        EndPointMethod endPointMethod = (EndPointMethod) resourceMethod;
        String methodName = qualifiedMethodName(resourceMethod);
        EndPoint endPoint = endPointMethod.getEndPoint();

        logger.debug("Generating end-point method `%s`.", methodName);

        CodeSnippet code = outputter.configure(TEMPLATE)
                .withParam("method", endPointMethod.getMethod())
                .withParam("endPointType", endPoint.getType())
                .withParam("endPointArguments", endPoint.getFields())
                .withErrorLogParameter(methodName)
                .parse();

        endPointProcessor.process(endPoint);

        return code;
    }

    @Override
    public void processLast() {
        endPointProcessor.processLast();
    }
}
