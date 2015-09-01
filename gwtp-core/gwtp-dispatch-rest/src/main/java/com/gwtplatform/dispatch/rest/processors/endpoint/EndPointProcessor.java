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

import javax.annotation.processing.ProcessingEnvironment;

import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.processors.DispatchRestContextProcessor;
import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.HttpVariable;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationProcessors;

import static com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext.IO.READ;
import static com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext.IO.WRITE;

public class EndPointProcessor extends DispatchRestContextProcessor<EndPoint, Void> {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/endpoint/EndPoint.vm";

    private SerializationProcessors serializationProcessors;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        serializationProcessors = new SerializationProcessors(processingEnv);
    }

    @Override
    public Void process(EndPoint endPoint) {
        logger.debug("Generating end-point implementation `%s`.", endPoint.getImpl());

        outputter.withTemplateFile(TEMPLATE)
                .withParam("endPoint", endPoint.getEndPointDetails())
                .withParam("fields", endPoint.getFields())
                .writeTo(endPoint.getImpl());

        generateSerializers(endPoint);

        return null;
    }

    private void generateSerializers(EndPoint endPoint) {
        EndPointDetails endPointDetails = endPoint.getEndPointDetails();
        Optional<HttpVariable> body = endPointDetails.getBody();
        SerializationContext context;

        if (body.isPresent()) {
            context = new SerializationContext(body.get().getType(), endPointDetails.getConsumes(), READ);
            serializationProcessors.process(context);
        }

        context = new SerializationContext(endPointDetails.getResult(), endPointDetails.getProduces(), WRITE);
        serializationProcessors.process(context);
    }
}
