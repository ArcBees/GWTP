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

package com.gwtplatform.processors.tools.utils;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class Utils {
    private final Types types;
    private final Elements elements;

    public Utils(
            Types types,
            Elements elements) {
        this.types = types;
        this.elements = elements;
    }

    public Types getTypes() {
        return types;
    }

    public Elements getElements() {
        return elements;
    }

    public TypeMirror createWithWildcard(Class<?> clazz) {
        return createWithWildcard(clazz.getCanonicalName());
    }

    public TypeMirror createWithWildcard(String qualifiedName) {
        TypeElement element = elements.getTypeElement(qualifiedName);
        int typeParametersCount = element.getTypeParameters().size();
        TypeMirror[] typeArguments = new TypeMirror[typeParametersCount];

        for (int i = 0; i < typeParametersCount; ++i) {
            typeArguments[i] = types.getWildcardType(null, null);
        }

        return types.getDeclaredType(element, typeArguments);
    }

    public List<Element> getAllMembers(TypeElement type, Class<?> exclusion) {
        return getAllMembers(type, elements.getTypeElement(exclusion.getCanonicalName()));
    }

    public List<Element> getAllMembers(TypeElement type, TypeElement... exclusions) {
        List<? extends Element> allMembers = elements.getAllMembers(type);

        final Set<Element> allExclusions = exclusions == null ? null : FluentIterable.of(exclusions)
                .transformAndConcat(new Function<TypeElement, Iterable<? extends Element>>() {
                    @Override
                    public Iterable<? extends Element> apply(TypeElement exclusion) {
                        return elements.getAllMembers(exclusion);
                    }
                }).toSet();

        return FluentIterable.from(allMembers)
                .transform(new Function<Element, Element>() {
                    @Override
                    public Element apply(Element element) {
                        // Element in Function is Fully-variant, allowing us to transform <? extends Element> to
                        // <Element> without compiler warnings.
                        return element;
                    }
                })
                .filter(new Predicate<Element>() {
                    @Override
                    public boolean apply(Element element) {
                        return allExclusions == null || !allExclusions.contains(element);
                    }
                })
                .toList();
    }
}
