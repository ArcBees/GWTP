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

package com.gwtplatform.processors.tools.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class Method implements HasImports {
    private static class ElementToVariableFunction implements Function<VariableElement, Variable> {
        private final Collection<String> existingVariableNames;

        ElementToVariableFunction(Collection<String> existingVariableNames) {
            this.existingVariableNames = existingVariableNames;
        }

        @Override
        public Variable apply(VariableElement variableElement) {
            return new Variable(variableElement, existingVariableNames);
        }
    }

    private final Type returnType;
    private final String name;
    private final List<Variable> parameters;
    private final ExecutableElement element;

    public Method(ExecutableElement element) {
        this(element, Collections.<String>emptyList());
    }

    public Method(
            ExecutableElement element,
            Collection<String> existingVariableNames) {
        this.element = element;
        this.returnType = new Type(element.getReturnType());
        this.name = element.getSimpleName().toString();
        this.parameters = processParameters(element, existingVariableNames);
    }

    private List<Variable> processParameters(ExecutableElement element, Collection<String> existingVariableNames) {
        return FluentIterable.from(element.getParameters())
                .transform(new ElementToVariableFunction(existingVariableNames))
                .toList();
    }

    public ExecutableElement getElement() {
        return element;
    }

    public Type getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<Variable> getParameters() {
        return parameters;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(parameters)
                .transformAndConcat(EXTRACT_IMPORTS_FUNCTION)
                .append(returnType.getImports())
                .toSet();
    }
}
