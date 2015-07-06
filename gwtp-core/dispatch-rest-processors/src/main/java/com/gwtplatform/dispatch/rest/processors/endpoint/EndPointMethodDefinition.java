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

import com.gwtplatform.dispatch.rest.processors.definitions.CodeSnippet;
import com.gwtplatform.dispatch.rest.processors.definitions.EndPointDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.HasImports;
import com.gwtplatform.dispatch.rest.processors.definitions.MethodDefinition;

public class EndPointMethodDefinition implements HasImports {
    private final MethodDefinition method;
    private final EndPointDefinition endPoint;
    private final EndPointImplDefinition impl;
    private final CodeSnippet codeSnippet;

    EndPointMethodDefinition(
            MethodDefinition method,
            EndPointDefinition endPoint,
            EndPointImplDefinition impl,
            CodeSnippet codeSnippet) {
        this.method = method;
        this.endPoint = endPoint;
        this.impl = impl;
        this.codeSnippet = codeSnippet;
    }

    public MethodDefinition getMethod() {
        return method;
    }

    public EndPointImplDefinition getImpl() {
        return impl;
    }

    public EndPointDefinition getEndPoint() {
        return endPoint;
    }

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
