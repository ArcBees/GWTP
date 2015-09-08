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

import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.details.Variable;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.utils.Utils;

import static javax.lang.model.util.ElementFilter.methodsIn;

import static com.google.auto.common.MoreElements.asType;

public class EndPoint implements IsEndPoint {
    private final Type impl;
    private final List<Variable> fields;
    private final EndPointDetails endPointDetails;
    private final Utils utils;

    public EndPoint(
            Utils utils,
            EndPointMethod endPointMethod,
            ExecutableElement element) {
        this.utils = utils;
        this.impl = processName(endPointMethod.getParentResource().getType(), element);
        this.fields = new EndPointUtils().processFields(endPointMethod);
        this.endPointDetails = endPointMethod.getEndPointDetails();
    }

    private Type processName(Type parentType, ExecutableElement method) {
        String parentName = parentType.getSimpleName();
        int methodIndex = indexInParent(method);
        Name methodName = method.getSimpleName();

        String packageName = parentType.getPackageName();
        String className = String.format("%s_%d_%s", parentName, methodIndex, methodName);

        return new Type(packageName, className);
    }

    private int indexInParent(ExecutableElement method) {
        TypeElement type = asType(method.getEnclosingElement());
        List<ExecutableElement> methods = methodsIn(utils.getElements().getAllMembers(type));
        return methods.indexOf(method);
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
