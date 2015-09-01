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

import java.util.Collection;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.ResourceType;
import com.gwtplatform.dispatch.rest.processors.domain.Variable;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodFactories;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static javax.lang.model.util.ElementFilter.methodsIn;

import static com.google.auto.common.MoreElements.asType;

public class SubResource implements ResourceType, HasImports {
    private final TypeElement element;
    private final Logger logger;
    private final Utils utils;
    private final SubResourceMethod method;

    private final Type impl;
    private final Type subResource;
    private final List<Variable> fields;
    private final List<ResourceMethod> methods;
    private final EndPointDetails endPointDetails;

    public SubResource(
            Logger logger,
            Utils utils,
            SubResourceMethod method,
            TypeElement element) {
        this.logger = logger;
        this.utils = utils;
        this.method = method;
        this.element = ensureTypeElement(element);

        this.subResource = new Type(this.element);
        this.impl = processImplType(method);
        this.endPointDetails = new EndPointDetails(logger, utils, element, method.getEndPointDetails());
        this.fields = processFields();
        this.methods = processMethods();
    }

    private TypeElement ensureTypeElement(Element element) {
        // TODO: Copied from Resource
        if (element.getKind() == ElementKind.INTERFACE) {
            return asType(element);
        }

        logger.mandatoryWarning().context(element).log("Element annotated with @Path must be an interface.");
        throw new UnableToProcessException();
    }

    private Type processImplType(SubResourceMethod method) {
        Type parentImpl = method.getParentImpl();

        return new Type(subResource.getPackageName(), parentImpl.getSimpleName() + "_" + subResource.getSimpleName());
    }

    private List<Variable> processFields() {
        // TODO
        return null;
    }

    private List<ResourceMethod> processMethods() {
        // TODO: Copied from Resource
        final ResourceMethodFactories resourceMethodFactories = new ResourceMethodFactories(logger, utils);
        List<ExecutableElement> methodElements = methodsIn(utils.getAllMembers(element, Object.class));

        return FluentIterable.from(methodElements)
                .transform(new Function<ExecutableElement, ResourceMethod>() {
                    @Override
                    public ResourceMethod apply(ExecutableElement element) {
                        return resourceMethodFactories.resolve(SubResource.this, element);
                    }
                })
                .filter(Predicates.notNull())
                .toList();
    }

    @Override
    public Type getImpl() {
        return impl;
    }

    @Override
    public EndPointDetails getEndPointDetails() {
        return endPointDetails;
    }

    public List<Variable> getFields() {
        return fields;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(methods)
                .transformAndConcat(HasImports.EXTRACT_IMPORTS_FUNCTION)
                .append(subResource.getImports())
                .toList();
    }
}
