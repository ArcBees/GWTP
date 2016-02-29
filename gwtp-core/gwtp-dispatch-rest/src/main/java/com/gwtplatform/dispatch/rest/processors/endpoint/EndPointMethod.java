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

import java.util.Collection;

import javax.lang.model.element.ExecutableElement;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetails;
import com.gwtplatform.processors.tools.domain.Method;
import com.gwtplatform.dispatch.rest.processors.resource.Resource;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodUtils;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class EndPointMethod implements ResourceMethod {
    private final ResourceMethodUtils resourceMethodUtils;
    private final EndPointDetails.Factory endPointDetailsFactory;
    private final EndPoint.Factory endPointFactory;
    private final Resource parentResource;
    private final ExecutableElement element;

    private Optional<Method> method = absent();
    private Optional<EndPointDetails> endPointDetails = absent();
    private Optional<EndPoint> endPoint = absent();

    EndPointMethod(
            ResourceMethodUtils resourceMethodUtils,
            EndPointDetails.Factory endPointDetailsFactory,
            EndPoint.Factory endPointFactory,
            Resource parentResource,
            ExecutableElement element) {
        this.resourceMethodUtils = resourceMethodUtils;
        this.endPointDetailsFactory = endPointDetailsFactory;
        this.endPointFactory = endPointFactory;
        this.parentResource = parentResource;
        this.element = element;
    }

    @Override
    public Resource getParentResource() {
        return parentResource;
    }

    @Override
    public Method getMethod() {
        if (!method.isPresent()) {
            method = of(resourceMethodUtils.processMethod(parentResource, element));
        }

        return method.get();
    }

    @Override
    public EndPointDetails getEndPointDetails() {
        if (!endPointDetails.isPresent()) {
            endPointDetails = of(endPointDetailsFactory.create(parentResource.getEndPointDetails(), getMethod()));
        }

        return endPointDetails.get();
    }

    public EndPoint getEndPoint() {
        if (!endPoint.isPresent()) {
            endPoint = of(endPointFactory.create(this, element));
        }

        return endPoint.get();
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(getMethod().getImports())
                .append(getEndPoint().getType().getImports())
                .toList();
    }
}
