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

import com.google.gwt.core.ext.typeinfo.JType;

public class ClassDefinition {
    private final String packageName;
    private final String className;
    private final String typeParameters;

    public ClassDefinition(
            JType type) {
        this(type.getParameterizedQualifiedSourceName());
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

        // primitives won't have packages
        int lastDot = qualifiedClassName.lastIndexOf('.');
        if (lastDot != -1) {
            packageName = qualifiedClassName.substring(0, lastDot);
            className = qualifiedClassName.substring(lastDot + 1);
        } else {
            packageName = "";
            className = qualifiedClassName;
        }
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
        String qualifiedName = packageName;
        if (!qualifiedName.isEmpty()) {
            qualifiedName += ".";
        }

        qualifiedName += className;

        return qualifiedName;
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

    @Override
    public int hashCode() {
        return getParameterizedQualifiedName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ClassDefinition)) {
            return false;
        }

        ClassDefinition other = (ClassDefinition) obj;
        return getParameterizedQualifiedName().equals(other.getParameterizedQualifiedName());
    }

    private String maybeAppendTypeParameters(String name) {
        if (isParameterized()) {
            return name + "<" + typeParameters + ">";
        }

        return name;
    }
}
