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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public enum Primitives {
    BOOLEAN("boolean", Boolean.class, false),
    BYTE("byte", Byte.class, 0),
    CHAR("char", Character.class, 0),
    DOUBLE("double", Double.class, 0D),
    FLOAT("float", Float.class, 0F),
    INT("int", Integer.class, 0),
    LONG("long", Long.class, 0L),
    SHORT("short", Short.class, 0),
    VOID("void", Void.class, null);

    public static final Predicate<CharSequence> IS_PRIMITIVE_PREDICATE = new Predicate<CharSequence>() {
        @Override
        public boolean apply(CharSequence name) {
            return findByPrimitive(name).isPresent();
        }
    };

    private final String primitive;
    private final Class<?> boxedClass;
    private final Object defaultValue;

    Primitives(
            String primitive,
            Class<?> boxedClass,
            Object defaultValue) {
        this.primitive = primitive;
        this.boxedClass = boxedClass;
        this.defaultValue = defaultValue;
    }

    public static Optional<Primitives> findByPrimitive(final CharSequence primitiveName) {
        return FluentIterable.of(values())
                .firstMatch(new Predicate<Primitives>() {
                    @Override
                    public boolean apply(Primitives primitives) {
                        return primitiveName.equals(primitives.getPrimitive());
                    }
                });
    }

    public static Optional<Primitives> findByBoxed(final CharSequence boxedName) {
        return FluentIterable.of(values())
                .firstMatch(new Predicate<Primitives>() {
                    @Override
                    public boolean apply(Primitives primitives) {
                        return boxedName.equals(primitives.getBoxedClass().getCanonicalName());
                    }
                });
    }

    public String getPrimitive() {
        return primitive;
    }

    public Class<?> getBoxedClass() {
        return boxedClass;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
