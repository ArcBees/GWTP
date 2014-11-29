/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.resource;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.dispatch.rest.rebind.Parameter;

public class MethodDefinition {
    private final JMethod method;
    private final List<Parameter> parameters;
    private final List<Parameter> inheritedParameters;
    private final Set<String> imports;

    private String output;

    public MethodDefinition(
            MethodDefinition definition) {
        this(definition.getMethod(), definition.getParameters(), definition.getInheritedParameters());

        setOutput(definition.getOutput());
        imports.addAll(definition.getImports());
    }

    public MethodDefinition(
            JMethod method,
            List<Parameter> parameters,
            List<Parameter> inheritedParameters) {
        this.method = method;
        this.parameters = parameters;
        this.inheritedParameters = inheritedParameters;
        this.imports = Sets.newTreeSet();
    }

    public JMethod getMethod() {
        return method;
    }

    public List<Parameter> getParameters() {
        return Lists.newArrayList(parameters);
    }

    public List<Parameter> getInheritedParameters() {
        return Lists.newArrayList(inheritedParameters);
    }

    public void addImport(String qualifiedClassName) {
        imports.add(qualifiedClassName);
    }

    public Set<String> getImports() {
        return Sets.newTreeSet(imports);
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOutput() {
        return output;
    }
}
