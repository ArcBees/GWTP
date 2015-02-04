/**
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

package com.gwtplatform.dispatch.rest.rebind.utils;

import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;

public class JPrimitives {
    public static boolean isPrimitiveOrBoxed(JType type) {
        return isPrimitive(type) || isBoxed(type);
    }

    public static boolean isPrimitive(JType type) {
        return type.isPrimitive() != null;
    }

    public static boolean isBoxed(JType type) {
        String name = type.getQualifiedSourceName();

        for (JPrimitiveType primitive : JPrimitiveType.values()) {
            if (name.equals(primitive.getQualifiedBoxedSourceName())) {
                return true;
            }
        }

        return false;
    }
}
