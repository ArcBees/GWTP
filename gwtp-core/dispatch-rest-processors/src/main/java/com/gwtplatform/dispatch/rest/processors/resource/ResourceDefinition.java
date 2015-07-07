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
import com.gwtplatform.dispatch.rest.processors.definitions.EndPointDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.HasImports;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.endpoint.EndPointMethodDefinition;

public class ResourceDefinition implements HasImports {
    private final TypeDefinition impl;
    private final TypeDefinition resource;
    private final EndPointDefinition endPoint;
    private final List<EndPointMethodDefinition> methods;

    public ResourceDefinition(
            TypeDefinition impl,
            TypeDefinition resource,
            EndPointDefinition endPoint,
            List<EndPointMethodDefinition> methods) {
        this.impl = impl;
        this.resource = resource;
        this.endPoint = endPoint;
        this.methods = methods;
    }

    public TypeDefinition getImpl() {
        return impl;
    }

    public TypeDefinition getResource() {
        return resource;
    }

    public EndPointDefinition getEndPoint() {
        return endPoint;
    }

    public List<EndPointMethodDefinition> getMethods() {
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
