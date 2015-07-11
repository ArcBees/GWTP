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
import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.HasImports;
import com.gwtplatform.dispatch.rest.processors.domain.Type;
import com.gwtplatform.dispatch.rest.processors.domain.Variable;

public class EndPoint implements HasImports {
    private final Type impl;
    private final List<Variable> fields;
    private final EndPointDetails endPointDetails;

    public EndPoint(
            Type impl,
            List<Variable> fields,
            EndPointDetails endPointDetails) {
        this.impl = impl;
        this.fields = fields;
        this.endPointDetails = endPointDetails;
    }

    public Type getImpl() {
        return impl;
    }

    public List<Variable> getFields() {
        return fields;
    }

    public EndPointDetails getEndPointDetails() {
        return endPointDetails;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(fields)
                .transformAndConcat(new Function<Variable, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(Variable variable) {
                        return variable.getImports();
                    }
                })
                .append(impl.getImports())
                .toList();
    }
}
