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

package com.gwtplatform.dispatch.rest.processors.resolvers;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.definitions.MethodDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.VariableDefinition;

import static com.google.auto.common.MoreTypes.asDeclared;

public class MethodResolver {
    public MethodDefinition resolve(ExecutableElement element) {
        TypeDefinition returnType = processReturnType(element);
        String name = element.getSimpleName().toString();
        List<VariableDefinition> parameters = processParameters(element);

        return new MethodDefinition(returnType, name, parameters);
    }

    private TypeDefinition processReturnType(ExecutableElement element) {
        DeclaredType returnType = asDeclared(element.getReturnType());
        return new TypeDefinition(returnType);
    }

    private List<VariableDefinition> processParameters(ExecutableElement element) {
        return FluentIterable.from(element.getParameters())
                .transform(new Function<VariableElement, VariableDefinition>() {
                    @Override
                    public VariableDefinition apply(VariableElement variableElement) {
                        return processParameter(variableElement);
                    }
                })
                .toList();
    }

    private VariableDefinition processParameter(VariableElement element) {
        TypeDefinition typeDefinition = new TypeDefinition(element.asType());
        String name = element.getSimpleName().toString();

        return new VariableDefinition(typeDefinition, name);
    }
}
