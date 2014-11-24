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

package com.gwtplatform.dispatch.rest.rebind.utils;

import com.google.gwt.core.ext.typeinfo.JMethod;

public class ClassNameGenerator {
    /**
     * Generate a unique class name based on a method. Since methods may define overloads, it's possible we end up with
     * name clashes. This function will prefix the requested name with the parent name and the method index to ensure
     * uniqueness.
     */
    public static String prefixName(JMethod method, String parentName, String name) {
        int methodIndex = Arrays.asList(method.getEnclosingType().getInheritableMethods()).indexOf(method);

        return String.format("%s_%d_%s", parentName, methodIndex, name);
    }
}
