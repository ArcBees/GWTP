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
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.definitions.EndPointDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.HasImports;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.VariableDefinition;

public class EndPointImplDefinition implements HasImports {
    private final TypeDefinition impl;
    private final List<VariableDefinition> fields;
    private final EndPointDefinition endPoint;

    public EndPointImplDefinition(
            TypeDefinition impl,
            List<VariableDefinition> fields,
            EndPointDefinition endPoint) {
        this.impl = impl;
        this.fields = fields;
        this.endPoint = endPoint;
    }

    public TypeDefinition getImpl() {
        return impl;
    }

    public List<VariableDefinition> getFields() {
        return fields;
    }

    public EndPointDefinition getEndPoint() {
        return endPoint;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(fields)
                .transformAndConcat(new Function<VariableDefinition, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(VariableDefinition variable) {
                        return variable.getImports();
                    }
                })
                .append(impl.getImports())
                .toList();
    }
}
