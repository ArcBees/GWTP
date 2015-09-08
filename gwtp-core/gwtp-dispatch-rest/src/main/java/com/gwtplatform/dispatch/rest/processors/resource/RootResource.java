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
import javax.lang.model.element.TypeElement;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetails;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class RootResource implements Resource {
    public interface Factory {
        RootResource create(Element element);
    }

    private static final String NAME_SUFFIX = "Impl";

    private final ResourceUtils resourceUtils;
    private final EndPointDetails.Factory endPointDetailsFactory;
    private final TypeElement element;

    private Optional<Type> resourceType = absent();
    private Optional<Type> type = absent();
    private Optional<EndPointDetails> endPointDetails = absent();
    private Optional<List<ResourceMethod>> methods = absent();

    RootResource(
            ResourceUtils resourceUtils,
            EndPointDetails.Factory endPointDetailsFactory,
            Element element) {
        this.resourceUtils = resourceUtils;
        this.endPointDetailsFactory = endPointDetailsFactory;
        this.element = resourceUtils.asResourceTypeElement(element);
    }

    @Override
    public Type getResourceType() {
        if (!resourceType.isPresent()) {
            resourceType = of(new Type(element));
        }

        return resourceType.get();
    }

    @Override
    public Type getType() {
        if (!type.isPresent()) {
            createType();
        }

        return type.get();
    }

    private void createType() {
        Type resource = getResourceType();

        type = of(new Type(resource.getPackageName(), resource.getSimpleName() + NAME_SUFFIX));
    }

    @Override
    public EndPointDetails getEndPointDetails() {
        if (!endPointDetails.isPresent()) {
            endPointDetails = of(endPointDetailsFactory.create(element));
        }

        return endPointDetails.get();
    }

    @Override
    public List<ResourceMethod> getMethods() {
        if (!methods.isPresent()) {
            methods = of(resourceUtils.processMethods(element, this));
        }

        return methods.get();
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(getMethods())
                .transformAndConcat(HasImports.EXTRACT_IMPORTS_FUNCTION)
                .append(getResourceType().getImports())
                .append(getType().getImports())
                .toList();
    }
}
