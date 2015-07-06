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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.ws.rs.Path;

import com.google.auto.service.AutoService;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.gwtplatform.dispatch.rest.processors.AbstractProcessor;
import com.gwtplatform.dispatch.rest.processors.NameFactory;
import com.gwtplatform.dispatch.rest.processors.definitions.EndPointDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.endpoint.EndPointMethodDefinition;
import com.gwtplatform.dispatch.rest.processors.resolvers.EndPointResolver;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;

import static javax.lang.model.util.ElementFilter.methodsIn;

import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreTypes.asDeclared;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions(Logger.DEBUG_OPTION)
public class RootResourceProcessor extends AbstractProcessor {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/resource/RootResource.vm";

    private EndPointResolver endPointResolver;

    @Override
    protected void init() {
        endPointResolver = new EndPointResolver(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<? extends Class<? extends Annotation>> annotations() {
        return Sets.newHashSet(Path.class);
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        Set<Element> elementsWithPath = elementsByAnnotation.get(Path.class);

        // TODO: Filter out classes outside GWT's paths
        try {
            process(elementsWithPath);
        } catch (Exception e) {
            logger.error("Unresolvable exception.", e);
        }
    }

    private void process(Set<Element> elementsWithPath) {
        for (Element element : elementsWithPath) {
            if (element.getKind() == ElementKind.INTERFACE) {
                process(asType(element));
            } else if (element.getKind() == ElementKind.CLASS) {
                logger.note("Resource `%s` is concrete. To be generated it must be an interface.",
                        asType(element).getQualifiedName());
            }
        }
    }

    private void process(TypeElement element) {
        Name name = element.getQualifiedName();

        logger.debug("Generating resource implementation for `%s`.", name);

        DeclaredType type = asDeclared(element.asType());
        TypeDefinition resource = new TypeDefinition(type);
        TypeDefinition impl = NameFactory.resourceName(resource);
        EndPointDefinition endPoint = endPointResolver.resolve(type);
        List<EndPointMethodDefinition> methods = processMethods(impl, endPoint, type);

        try {
            outputter.withTemplateFile(TEMPLATE)
                    .withParam("resource", resource)
                    .withParam("methods", methods)
                    .writeTo(impl);
        } catch (IOException e) {
            logger.error("Can not write resource implementation `%s`.", e, name);
        }
    }

    private List<EndPointMethodDefinition> processMethods(final TypeDefinition impl, final EndPointDefinition endPoint,
            DeclaredType type) {
        List<ExecutableElement> methodElements = methodsIn(type.asElement().getEnclosedElements());

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
        ResourceMethodProcessor processor = contextProcessors.getResourceMethodProcessor(context);

        EndPointMethodDefinition method = processor.process(context);
        if (method == null) {
            logger.error("Can not generate resource method `%s`.", context);
        }

        return method;
    }
}
