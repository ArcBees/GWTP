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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.processors.DispatchRestContextProcessor;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.details.HttpVariable;
import com.gwtplatform.dispatch.rest.processors.endpoint.parameters.HttpVariableInitializerProcessors;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext.IO.READ;
import static com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext.IO.WRITE;

public class EndPointProcessor extends DispatchRestContextProcessor<EndPoint, Void> {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/endpoint/EndPoint.vm";

    private SerializationProcessors serializationProcessors;
    private HttpVariableInitializerProcessors httpVariableInitializerProcessors;

    @Override
    public void init(Logger logger, Utils utils, Outputter outputter) {
        super.init(logger, utils, outputter);

        serializationProcessors = new SerializationProcessors(logger, utils, outputter);
        httpVariableInitializerProcessors = new HttpVariableInitializerProcessors(logger, utils, outputter);
    }

    @Override
    public Void process(EndPoint endPoint) {
        Type type = endPoint.getType();

        logger.debug("Generating end-point implementation `%s`.", type);

        EndPointDetails endPointDetails = endPoint.getEndPointDetails();
        List<CodeSnippet> paramSnippets = createInitializerSnippets(endPointDetails.getHttpVariables());

        outputter.configure(TEMPLATE)
                .withParam("endPoint", endPointDetails)
                .withParam("paramSnippets", paramSnippets)
                .withParam("fields", endPoint.getFields())
                .writeTo(type);

        generateSerializers(endPoint);

        return null;
    }

    private List<CodeSnippet> createInitializerSnippets(Collection<HttpVariable> httpVariables) {
        List<CodeSnippet> codeSnippets = new ArrayList<>();

        for (HttpVariable httpVariable : httpVariables) {
            codeSnippets.add(httpVariableInitializerProcessors.process(httpVariable));
        }

        return codeSnippets;
    }

    private void generateSerializers(EndPoint endPoint) {
        EndPointDetails endPointDetails = endPoint.getEndPointDetails();
        Optional<HttpVariable> body = endPointDetails.getBody();
        SerializationContext context;

        if (body.isPresent()) {
            context = new SerializationContext(body.get().getType(), endPointDetails.getConsumes(), READ);
            serializationProcessors.process(context);
        }

        context = new SerializationContext(endPointDetails.getResultType(), endPointDetails.getProduces(), WRITE);
        serializationProcessors.process(context);
    }

    @Override
    public void processLast() {
        httpVariableInitializerProcessors.processLast();
    }
}
