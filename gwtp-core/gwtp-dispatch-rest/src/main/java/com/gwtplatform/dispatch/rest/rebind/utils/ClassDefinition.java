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

import com.google.gwt.core.ext.typeinfo.JClassType;

public class ClassDefinition {
    private final String packageName;
    private final String className;
    private final String typeParameters;

    public ClassDefinition(
            JClassType classType) {
        this(classType.getParameterizedQualifiedSourceName());
    }

    public ClassDefinition(
            String parameterizedQualifiedName) {
        String qualifiedClassName;
        int typeParameterStart = parameterizedQualifiedName.indexOf("<");

        if (typeParameterStart != -1) {
            int typeParameterEnd = parameterizedQualifiedName.lastIndexOf(">");
            typeParameters = parameterizedQualifiedName.substring(typeParameterStart + 1, typeParameterEnd);
            qualifiedClassName = parameterizedQualifiedName.substring(0, typeParameterStart);
        } else {
            typeParameters = null;
            qualifiedClassName = parameterizedQualifiedName;
        }

        int lastDot = qualifiedClassName.lastIndexOf('.');
        packageName = qualifiedClassName.substring(0, lastDot);
        className = qualifiedClassName.substring(lastDot + 1);
    }

    public ClassDefinition(
            String packageName,
            String className) {
        this(packageName, className, null);
    }

    public ClassDefinition(
            String packageName,
            String className,
            String typeParameters) {
        this.packageName = packageName;
        this.className = className;
        this.typeParameters = typeParameters;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getTypeParameters() {
        return typeParameters;
    }

    public boolean isParameterized() {
        return typeParameters != null;
    }

    public String getQualifiedName() {
        return packageName + "." + className;
    }

    public String getParameterizedClassName() {
        return maybeAppendTypeParameters(getClassName());
    }

    public String getParameterizedQualifiedName() {
        return maybeAppendTypeParameters(getQualifiedName());
    }

    @Override
    public String toString() {
        return getParameterizedQualifiedName();
    }

    private String maybeAppendTypeParameters(String name) {
        if (isParameterized()) {
            return name + "<" + typeParameters + ">";
        }

        return name;
    }
}
