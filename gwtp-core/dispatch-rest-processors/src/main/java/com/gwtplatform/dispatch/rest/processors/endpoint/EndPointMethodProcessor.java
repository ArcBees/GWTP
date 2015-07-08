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

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Generated;

import com.google.auto.service.AutoService;
import com.gwtplatform.dispatch.rest.processors.AbstractContextProcessor;
import com.gwtplatform.dispatch.rest.processors.ContextProcessors;
import com.gwtplatform.dispatch.rest.processors.definitions.CodeSnippet;
import com.gwtplatform.dispatch.rest.processors.definitions.EndPointDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.MethodDefinition;
import com.gwtplatform.dispatch.rest.processors.resolvers.EndPointResolver;
import com.gwtplatform.dispatch.rest.processors.resolvers.MethodResolver;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodContext;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodProcessor;

import static com.gwtplatform.dispatch.rest.processors.NameFactory.methodName;

@AutoService(ResourceMethodProcessor.class)
public class EndPointMethodProcessor extends AbstractContextProcessor<ResourceMethodContext, EndPointMethodDefinition>
        implements ResourceMethodProcessor {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/endpoint/EndPointMethod.vm";

    private ContextProcessors contextProcessors;
    private EndPointResolver endPointResolver;
    private MethodResolver methodResolver;

    @Override
    public void init() {
        contextProcessors = new ContextProcessors(processingEnv, logger);
        endPointResolver = new EndPointResolver(logger, utils);
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
        String methodName = methodName(context.getParent(), context.getElement());
        logger.debug("Generating end-point method `%s`.", methodName);

        // TODO: Add an order to HttpVariables and converge both HttpVariables and Variables. This is not an issue until
        // sub-resources are implemented
        MethodDefinition method = methodResolver.resolve(context.getElement());
        EndPointDefinition endPoint = endPointResolver.resolve(context.getElement(), context.getEndPoint());
        EndPointImplDefinition impl = processEndPointImpl(context, method, endPoint);

        CodeSnippet codeSnippet = parse(methodName, method, impl);

        return new EndPointMethodDefinition(method, endPoint, impl, codeSnippet);
    }

    private EndPointImplDefinition processEndPointImpl(ResourceMethodContext context, MethodDefinition method,
            EndPointDefinition endPoint) {
        EndPointImplContext endPointImplContext = new EndPointImplContext(context, method, endPoint);
        EndPointImplProcessor processor =
                contextProcessors.getProcessor(EndPointImplProcessor.class, endPointImplContext);

        return processor.process(endPointImplContext);
    }

    public CodeSnippet parse(String methodName, MethodDefinition method, EndPointImplDefinition impl) {
        String code = outputter.withTemplateFile(TEMPLATE)
                .withParam("method", method)
                .withParam("endPointImpl", impl)
                .withErrorLogParameter(methodName)
                .parse();
        Collection<String> imports = Arrays.asList(Generated.class.getCanonicalName());

        return new CodeSnippet(code, imports);
    }
}
