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

import javax.lang.model.element.ExecutableElement;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.dispatch.rest.processors.domain.Variable;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.dispatch.rest.processors.NameFactory.endPointName;

public class EndPoint implements HasImports {
    private final EndPointMethod endPointMethod;

    private final Type impl;
    private final List<Variable> fields;
    private final EndPointDetails endPointDetails;

    public EndPoint(
            Logger logger,
            Utils utils,
            EndPointMethod endPointMethod,
            ExecutableElement element) {
        this.endPointMethod = endPointMethod;

        this.impl = endPointName(utils.elements, endPointMethod.getParentImpl(), element);
        this.fields = ImmutableList.copyOf(endPointMethod.getMethod().getParameters());
        this.endPointDetails = endPointMethod.getEndPointDetails();
    }

    public EndPointMethod getEndPointMethod() {
        return endPointMethod;
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
                .transformAndConcat(EXTRACT_IMPORTS_FUNCTION)
                .append(impl.getImports())
                .toList();
    }
}
