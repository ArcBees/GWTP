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

package com.gwtplatform.dispatch.rest.processors.utils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class UnconstrainedTypes {
    private final Types types;
    private final Elements elements;

    public UnconstrainedTypes(
            Types types,
            Elements elements) {
        this.types = types;
        this.elements = elements;
    }

    public TypeMirror create(Class<?> clazz) {
        return create(clazz.getCanonicalName());
    }

    public TypeMirror create(String qualifiedName) {
        TypeElement element = elements.getTypeElement(qualifiedName);
        int typeParametersCount = element.getTypeParameters().size();
        TypeMirror[] typeArguments = new TypeMirror[typeParametersCount];

        for (int i = 0; i < typeParametersCount; ++i) {
            typeArguments[i] = types.getWildcardType(null, null);
        }

        return types.getDeclaredType(element, typeArguments);
    }
}
