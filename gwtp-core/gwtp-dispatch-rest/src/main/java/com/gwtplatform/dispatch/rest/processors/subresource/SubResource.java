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

import javax.lang.model.element.TypeElement;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.endpoint.EndPointUtils;
import com.gwtplatform.dispatch.rest.processors.endpoint.IsEndPoint;
import com.gwtplatform.dispatch.rest.processors.resource.Resource;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceUtils;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.domain.Variable;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class SubResource implements Resource, IsEndPoint {
    public interface Factory {
        SubResource create(SubResourceMethod method, TypeElement element);
    }

    private final ResourceUtils resourceUtils;
    private final EndPointUtils endPointUtils;
    private final EndPointDetails.Factory endPointDetailsFactory;
    private final SubResourceMethod method;
    private final TypeElement element;

    private Optional<Type> subResourceType = absent();
    private Optional<Type> type = absent();
    private Optional<List<Variable>> fields = absent();
    private Optional<List<ResourceMethod>> methods = absent();
    private Optional<EndPointDetails> endPointDetails = absent();

    SubResource(
            ResourceUtils resourceUtils,
            EndPointUtils endPointUtils,
            EndPointDetails.Factory endPointDetailsFactory,
            SubResourceMethod method,
            TypeElement element) {
        this.resourceUtils = resourceUtils;
        this.endPointUtils = endPointUtils;
        this.endPointDetailsFactory = endPointDetailsFactory;
        this.method = method;
        this.element = resourceUtils.asResourceTypeElement(element);
    }

    @Override
    public Type getResourceType() {
        if (!subResourceType.isPresent()) {
            subResourceType = of(new Type(element));
        }

        return subResourceType.get();
    }

    @Override
    public Type getType() {
        if (!type.isPresent()) {
            createImplType();
        }

        return type.get();
    }

    private void createImplType() {
        Type resourceType = getResourceType();
        Type parentImpl = method.getParentResource().getType();
        String simpleName = parentImpl.getSimpleName() + "_" + resourceType.getSimpleName();

        type = of(new Type(resourceType.getPackageName(), simpleName));
    }

    @Override
    public List<Variable> getFields() {
        if (!fields.isPresent()) {
            fields = of(endPointUtils.processFields(method));
        }

        return fields.get();
    }

    @Override
    public List<ResourceMethod> getMethods() {
        if (!methods.isPresent()) {
            methods = of(resourceUtils.processMethods(element, this));
        }

        return methods.get();
    }

    @Override
    public EndPointDetails getEndPointDetails() {
        if (!endPointDetails.isPresent()) {
            endPointDetails = of(endPointDetailsFactory.create(method.getEndPointDetails(), element));
        }

        return endPointDetails.get();
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
