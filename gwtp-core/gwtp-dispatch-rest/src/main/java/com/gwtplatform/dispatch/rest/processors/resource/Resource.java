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

import java.util.Collection;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static javax.lang.model.util.ElementFilter.methodsIn;

import static com.google.auto.common.MoreElements.asType;

public class Resource implements HasImports {
    private static final String NAME_SUFFIX = "Impl";

    private final Logger logger;
    private final Utils utils;
    private final TypeElement element;

    private final Type impl;
    private final Type resource;
    private final EndPointDetails endPointDetails;
    private final List<ResourceMethod> methods;

    public Resource(
            Logger logger,
            Utils utils,
            Element element) {
        this.logger = logger;
        this.utils = utils;
        this.element = ensureTypeElement(element);

        this.resource = new Type(this.element);
        this.impl = new Type(resource.getPackageName(), resource.getSimpleName() + NAME_SUFFIX);
        this.endPointDetails = new EndPointDetails(logger, utils, this.element);
        this.methods = processMethods();
    }

    public TypeElement ensureTypeElement(Element element) {
        if (element.getKind() == ElementKind.INTERFACE) {
            return asType(element);
        }

        logger.mandatoryWarning().context(element).log("Element annotated with @Path must be an interface.");
        throw new UnableToProcessException();
    }

    private List<ResourceMethod> processMethods() {
        final ResourceMethodFactories resourceMethodFactories = new ResourceMethodFactories(logger, utils);
        List<ExecutableElement> methodElements = methodsIn(utils.getAllMembers(element, Object.class));

        return FluentIterable.from(methodElements)
                .transform(new Function<ExecutableElement, ResourceMethod>() {
                    @Override
                    public ResourceMethod apply(ExecutableElement element) {
                        return resourceMethodFactories.resolve(Resource.this, element);
                    }
                })
                .filter(Predicates.notNull())
                .toList();
    }

    public Type getImpl() {
        return impl;
    }

    public Type getResource() {
        return resource;
    }

    public EndPointDetails getEndPointDetails() {
        return endPointDetails;
    }

    public List<ResourceMethod> getMethods() {
        return methods;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(methods)
                .transformAndConcat(HasImports.EXTRACT_IMPORTS_FUNCTION)
                .append(resource.getImports())
                .toList();
    }
}
