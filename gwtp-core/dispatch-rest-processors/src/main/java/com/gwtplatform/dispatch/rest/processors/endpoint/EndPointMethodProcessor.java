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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Generated;

import com.google.auto.service.AutoService;
import com.gwtplatform.dispatch.rest.processors.AbstractContextProcessor;
import com.gwtplatform.dispatch.rest.processors.definitions.CodeSnippet;
import com.gwtplatform.dispatch.rest.processors.definitions.EndPointDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.MethodDefinition;
import com.gwtplatform.dispatch.rest.processors.resolvers.EndPointResolver;
import com.gwtplatform.dispatch.rest.processors.resolvers.MethodResolver;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodContext;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodProcessor;

@AutoService(ResourceMethodProcessor.class)
public class EndPointMethodProcessor extends AbstractContextProcessor<ResourceMethodContext, EndPointMethodDefinition>
        implements ResourceMethodProcessor {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/endpoint/EndPointMethod.vm";

    private EndPointResolver endPointResolver;
    private MethodResolver methodResolver;

    @Override
    public void init() {
        endPointResolver = new EndPointResolver(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils());
        methodResolver = new MethodResolver();
    }

    @Override
    public byte getPriority() {
        return DEFAULT_PRIORITY;
    }

    @Override
    public boolean canProcess(ResourceMethodContext context) {
        return endPointResolver.canResolve(context.getElement(), context.getEndPoint());
    }

    @Override
    public EndPointMethodDefinition process(ResourceMethodContext context) {
        logger.debug("Generating end-point method `%s`.", context);

        // TODO: HttpVariables and Variables are different. Add an order to HttpVariables and converge both
        MethodDefinition method = methodResolver.resolve(context.getElement());
        EndPointDefinition endPoint = endPointResolver.resolve(context.getElement(), context.getEndPoint());
        EndPointImplDefinition impl = processEndPointImpl(context, method, endPoint);

        try {
            CodeSnippet codeSnippet = write(method, impl);

            return new EndPointMethodDefinition(method, endPoint, impl, codeSnippet);
        } catch (IOException e) {
            logger.warning("Can not write end-point method `%s`.", e, context);
            return null;
        }
    }

    private EndPointImplDefinition processEndPointImpl(ResourceMethodContext context, MethodDefinition method,
            EndPointDefinition endPoint) {
        EndPointImplProcessor processor = new EndPointImplProcessor();
        processor.init(processingEnv);

        EndPointImplContext endPointImplContext = new EndPointImplContext(context, method, endPoint);

        if (processor.canProcess(endPointImplContext)) {
            EndPointImplDefinition definition = processor.process(endPointImplContext);
            if (definition == null) {
                logger.error("Can not generate end-point implementation `%s`.", context);
            }

            return definition;
        } else {
            logger.error("Can not find a generator for input `%s`.", context);

            return null;
        }
    }

    private CodeSnippet write(MethodDefinition method, EndPointImplDefinition impl) throws IOException {
        String code = outputter.withTemplateFile(TEMPLATE)
                .withParam("method", method)
                .withParam("endPointImpl", impl)
                .parse();
        Collection<String> imports = Arrays.asList(Generated.class.getCanonicalName());

        return new CodeSnippet(code, imports);
    }
}
