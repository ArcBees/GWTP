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

import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.HasImports;
import com.gwtplatform.dispatch.rest.processors.domain.Type;

public class Resource implements HasImports {
    private final Type impl;
    private final Type resource;
    private final EndPointDetails endPoint;
    private final List<ResourceMethod> methods;

    public Resource(
            Type impl,
            Type resource,
            EndPointDetails endPoint,
            List<ResourceMethod> methods) {
        this.impl = impl;
        this.resource = resource;
        this.endPoint = endPoint;
        this.methods = methods;
    }

    public Type getImpl() {
        return impl;
    }

    public Type getResource() {
        return resource;
    }

    public EndPointDetails getEndPoint() {
        return endPoint;
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
