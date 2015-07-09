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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;

import com.google.auto.service.AutoService;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.AbstractContextProcessor;
import com.gwtplatform.dispatch.rest.processors.ContextProcessors;
import com.gwtplatform.dispatch.rest.processors.bindings.BindingContext;
import com.gwtplatform.dispatch.rest.processors.bindings.BindingsProcessor;
import com.gwtplatform.dispatch.rest.processors.definitions.EndPointDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.endpoint.EndPointMethodDefinition;
import com.gwtplatform.dispatch.rest.processors.resolvers.EndPointResolver;

import static javax.lang.model.util.ElementFilter.methodsIn;

import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreTypes.asDeclared;
import static com.google.auto.common.MoreTypes.asTypeElement;
import static com.google.common.collect.Iterables.isEmpty;
import static com.gwtplatform.dispatch.rest.processors.NameFactory.resourceName;

@AutoService(ResourceProcessor.class)
public class RootResourceProcessor extends AbstractContextProcessor<Element, ResourceDefinition>
        implements ResourceProcessor {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/resource/RootResource.vm";

    private ContextProcessors contextProcessors;
    private EndPointResolver endPointResolver;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        contextProcessors = new ContextProcessors(processingEnv, logger);
        endPointResolver = new EndPointResolver(logger, utils);
    }

    @Override
    public boolean canProcess(Element element) {
        ElementKind kind = element.getKind();
        if (kind == ElementKind.CLASS) {
            logger.warning().context(element).log("Resource `%s` is concrete. To be generated it must be an interface.",
                    asType(element).getQualifiedName());
        }

        return kind == ElementKind.INTERFACE;
    }

    @Override
    public ResourceDefinition process(Element element) {
        logger.debug("Generating resource implementation for `%s`.", asType(element).getQualifiedName());

        ResourceDefinition resource = processResource(element);

        outputter.withTemplateFile(TEMPLATE)
                .withParam("resource", resource.getResource())
                .withParam("methods", resource.getMethods())
                .writeTo(resource.getImpl());

        processBinding(resource);

        return resource;
    }

    public ResourceDefinition processResource(Element element) {
        DeclaredType type = asDeclared(element.asType());
        TypeDefinition resourceInterface = new TypeDefinition(type);
        TypeDefinition impl = resourceName(resourceInterface);
        EndPointDefinition endPoint = endPointResolver.resolve(type);
        List<EndPointMethodDefinition> methods = processMethods(impl, endPoint, type);

        return new ResourceDefinition(impl, resourceInterface, endPoint, methods);
    }

    private List<EndPointMethodDefinition> processMethods(final TypeDefinition impl, final EndPointDefinition endPoint,
            DeclaredType type) {
        List<ExecutableElement> methodElements = methodsIn(utils.getAllMembers(asTypeElement(type), Object.class));

        return FluentIterable.from(methodElements)
                .transform(new Function<ExecutableElement, EndPointMethodDefinition>() {
                    @Override
                    public EndPointMethodDefinition apply(ExecutableElement element) {
                        return processMethod(impl, endPoint, element);
                    }
                })
                .filter(Predicates.notNull())
                .toList();
    }

    private EndPointMethodDefinition processMethod(TypeDefinition impl, EndPointDefinition endPoint,
            ExecutableElement element) {
        ResourceMethodContext context = new ResourceMethodContext(impl, endPoint, element);
        ResourceMethodProcessor processor = contextProcessors.getProcessor(ResourceMethodProcessor.class, context);

        return processor.process(context);
    }

    private void processBinding(ResourceDefinition resource) {
        TypeDefinition impl = resource.getImpl();

        BindingContext context = new BindingContext(impl);
        context.setImplemented(resource.getResource());
        context.setScope(Singleton.class);

        Iterable<BindingsProcessor> processors = contextProcessors.getProcessors(BindingsProcessor.class, context);
        for (BindingsProcessor processor : processors) {
            processor.process(context);
        }

        if (isEmpty(processors)) {
            logger.mandatoryWarning("No binding policy found for resource `%s`.", impl.getQualifiedName());
        }
    }
}
