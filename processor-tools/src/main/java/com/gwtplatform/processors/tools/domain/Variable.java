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

import javax.lang.model.element.VariableElement;

public class Variable implements HasImports {
    private final VariableElement element;
    private final Type type;
    private final String name;

    public Variable(
            VariableElement element,
            Collection<String> existingVariableNames) {
        this.element = element;
        this.type = new Type(element.asType());
        this.name = processName(element, existingVariableNames);
    }

    private String processName(VariableElement element, Collection<String> existingVariableNames) {
        String simpleName = element.getSimpleName().toString();
        String uniqueName = simpleName;

        int i = 2;
        while (existingVariableNames.contains(uniqueName)) {
            uniqueName = simpleName + i++;
        }

        return uniqueName;
    }

    public VariableElement getElement() {
        return element;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<String> getImports() {
        return type.getImports();
    }
}
