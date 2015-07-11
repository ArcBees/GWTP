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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gwtplatform.dispatch.rest.processors.domain.CodeSnippet;
import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.Method;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;

public class EndPointResourceMethod implements ResourceMethod {
    private final Method method;
    private final EndPointDetails endPoint;
    private final EndPoint impl;
    private final CodeSnippet codeSnippet;

    EndPointResourceMethod(
            Method method,
            EndPointDetails endPoint,
            EndPoint impl,
            CodeSnippet codeSnippet) {
        this.method = method;
        this.endPoint = endPoint;
        this.impl = impl;
        this.codeSnippet = codeSnippet;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public EndPointDetails getEndPointDetails() {
        return endPoint;
    }

    public EndPoint getImpl() {
        return impl;
    }

    @Deprecated(/* TODO: processors will handle that  */)
    public CodeSnippet getCodeSnippet() {
        return codeSnippet;
    }

    @Override
    public Collection<String> getImports() {
        List<String> imports = new ArrayList<>(method.getImports());
        imports.addAll(impl.getImports());
        imports.addAll(codeSnippet.getImports());

        return imports;
    }
}
