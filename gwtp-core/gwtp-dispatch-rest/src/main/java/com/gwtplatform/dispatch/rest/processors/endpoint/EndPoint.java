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
import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.Variable;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.dispatch.rest.processors.NameFactory.endPointName;

public class EndPoint implements IsEndPoint {
    private final Type impl;
    private final List<Variable> fields;
    private final EndPointDetails endPointDetails;

    public EndPoint(
            Utils utils,
            EndPointMethod endPointMethod,
            ExecutableElement element) {
        this.impl = endPointName(utils.elements, endPointMethod.getParentResource().getType(), element);
        this.fields = new EndPointUtils().processFields(endPointMethod);
        this.endPointDetails = endPointMethod.getEndPointDetails();
    }

    @Override
    public Type getType() {
        return impl;
    }

    @Override
    public List<Variable> getFields() {
        return fields;
    }

    @Override
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
