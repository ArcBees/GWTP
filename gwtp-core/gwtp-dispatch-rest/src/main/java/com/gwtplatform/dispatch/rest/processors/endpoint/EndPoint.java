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
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetails;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.domain.Variable;
import com.gwtplatform.processors.tools.utils.Utils;

import static javax.lang.model.util.ElementFilter.methodsIn;
import static com.google.auto.common.MoreElements.asType;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class EndPoint implements IsEndPoint {
    public interface Factory {
        EndPoint create(EndPointMethod endPointMethod, ExecutableElement element);
    }

    private final Utils utils;
    private final EndPointUtils endPointUtils;
    private final EndPointMethod endPointMethod;
    private final ExecutableElement element;

    private Optional<Type> type = absent();
    private Optional<List<Variable>> fields = absent();
    private Optional<EndPointDetails> endPointDetails = absent();

    EndPoint(
            Utils utils,
            EndPointUtils endPointUtils,
            EndPointMethod endPointMethod,
            ExecutableElement element) {
        this.utils = utils;
        this.endPointUtils = endPointUtils;
        this.endPointMethod = endPointMethod;
        this.element = element;
    }

    @Override
    public Type getType() {
        if (!type.isPresent()) {
            createType();
        }

        return type.get();
    }

    private void createType() {
        Type parentType = endPointMethod.getParentResource().getType();
        String parentName = parentType.getSimpleName();
        int methodIndex = indexInParent();
        Name methodName = element.getSimpleName();

        String packageName = parentType.getPackageName();
        String className = String.format("%s_%d_%s", parentName, methodIndex, methodName);

        type = of(new Type(packageName, className));
    }

    private int indexInParent() {
        TypeElement parentTypeElement = asType(element.getEnclosingElement());
        List<ExecutableElement> methods = methodsIn(utils.getElements().getAllMembers(parentTypeElement));
        return methods.indexOf(element);
    }

    @Override
    public List<Variable> getFields() {
        if (!fields.isPresent()) {
            fields = of(endPointUtils.processFields(endPointMethod));
        }

        return fields.get();
    }

    @Override
    public EndPointDetails getEndPointDetails() {
        if (!endPointDetails.isPresent()) {
            endPointDetails = of(endPointMethod.getEndPointDetails());
        }

        return endPointDetails.get();
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(getFields())
                .transformAndConcat(EXTRACT_IMPORTS_FUNCTION)
                .append(getType().getImports())
                .toList();
    }
}
