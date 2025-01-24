/*
 * Copyright 2016 ArcBees Inc.
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

import jakarta.inject.Inject;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.util.ElementFilter.constructorsIn;

import static com.google.auto.common.MoreElements.asExecutable;
import static com.google.auto.common.MoreElements.hasModifiers;
import static com.google.auto.common.MoreElements.isAnnotationPresent;

public class ElementPredicates {
    public static Predicate<Element> IS_ACCESSIBLE_CLASS = element -> element.getKind() == CLASS
            && hasModifiers(PUBLIC).apply(element)
            && !hasModifiers(ABSTRACT).apply(element);

    public static Predicate<Element> IS_DEFAULT_CONSTRUCTOR = element -> element.getKind() == CONSTRUCTOR
            && hasModifiers(PUBLIC).apply(element)
            && asExecutable(element).getParameters().isEmpty();

    public static Predicate<Element> IS_INJECTABLE_CONSTRUCTOR = element -> element.getKind() == CONSTRUCTOR
            && isAnnotationPresent(element, Inject.class);

    public static Predicate<Element> hasMatchingConstructorOrNone(final Predicate<Element> predicate) {
        return element -> {
            List<ExecutableElement> constructors = constructorsIn(element.getEnclosedElements());
            return constructors.isEmpty() || FluentIterable.from(constructors).anyMatch(predicate);
        };
    }
}
