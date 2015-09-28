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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import static com.google.auto.common.MoreElements.getPackage;
import static com.google.auto.common.MoreTypes.asDeclared;
import static com.google.auto.common.MoreTypes.asPrimitiveType;
import static com.google.auto.common.MoreTypes.isType;

public class Type implements HasImports, Comparable<Type> {
    private static final Function<TypeMirror, Type> TYPE_MIRROR_TO_TYPE = new Function<TypeMirror, Type>() {
        @Override
        public Type apply(TypeMirror typeArgument) {
            return new Type(typeArgument);
        }
    };

    private final String packageName;
    private final String simpleName;
    private final String enclosingNames;
    private final List<Type> typeArguments;

    public Type(TypeElement element) {
        this(element.asType());
    }

    // TODO: I guess it's type to go with factory methods...
    public Type(TypeMirror type) {
        // void is not a primitive
        if (type.getKind() == TypeKind.VOID) {
            packageName = "";
            enclosingNames = "";
            simpleName = "void";
            typeArguments = ImmutableList.of();
        } else if (type.getKind().isPrimitive()) {
            packageName = "";
            enclosingNames = "";
            simpleName = asPrimitiveType(type).toString();
            typeArguments = ImmutableList.of();
        } else if (isType(type)) {
            DeclaredType declaredType = asDeclared(type);
            Element element = declaredType.asElement();

            packageName = getPackage(element).getQualifiedName().toString();
            simpleName = element.getSimpleName().toString();
            typeArguments = FluentIterable.from(declaredType.getTypeArguments())
                    .transform(TYPE_MIRROR_TO_TYPE)
                    .toList();

            StringBuilder enclosingElementNames = new StringBuilder();

            Element enclosingElement = element.getEnclosingElement();
            while (enclosingElement != null && enclosingElement.getKind() != ElementKind.PACKAGE) {
                enclosingElementNames.insert(0, enclosingElement.getSimpleName() + ".");
                enclosingElement = enclosingElement.getEnclosingElement();
            }

            if (enclosingElementNames.length() != 0) {
                enclosingElementNames.deleteCharAt(enclosingElementNames.length() - 1);
            }

            enclosingNames = enclosingElementNames.toString();
        } else {
            throw new IllegalArgumentException("TypeMirror must be a primitive or declared type.");
        }
    }

    public Type(String parameterizedQualifiedName) {
        enclosingNames = "";

        String qualifiedName = parameterizedQualifiedName;
        Builder<Type> argumentsBuilder = ImmutableList.builder();
        int argumentsStart = parameterizedQualifiedName.indexOf("<");

        if (argumentsStart != -1) {
            int argumentsEnd = parameterizedQualifiedName.lastIndexOf(">");
            qualifiedName = parameterizedQualifiedName.substring(0, argumentsStart);

            String[] arguments = parameterizedQualifiedName.substring(argumentsStart + 1, argumentsEnd).split(",");
            for (String argument : arguments) {
                argumentsBuilder.add(new Type(argument.trim()));
            }
        }
        typeArguments = argumentsBuilder.build();

        // primitives don't have packages
        int lastDot = qualifiedName.lastIndexOf('.');
        if (lastDot != -1) {
            packageName = qualifiedName.substring(0, lastDot).trim();
            simpleName = qualifiedName.substring(lastDot + 1).trim();
        } else {
            packageName = "";
            simpleName = qualifiedName.trim();
        }
    }

    public Type(Class<?> clazz) {
        this(clazz.getPackage().getName(), clazz.getSimpleName());
    }

    public Type(Class<?> clazz, List<Type> typeArguments) {
        this(clazz.getPackage().getName(), clazz.getSimpleName(), typeArguments);
    }

    public Type(
            String packageName,
            String simpleName) {
        this(packageName, "", simpleName);
    }

    public Type(
            String packageName,
            String enclosingNames,
            String simpleName) {
        this(packageName, enclosingNames, simpleName, new ArrayList<Type>());
    }

    public Type(
            String packageName,
            String simpleName,
            List<Type> typeArguments) {
        this(packageName, "", simpleName, typeArguments);
    }

    public Type(
            String packageName,
            String enclosingNames,
            String simpleName,
            List<Type> typeArguments) {
        this.packageName = packageName;
        this.enclosingNames = enclosingNames;
        this.simpleName = simpleName;
        this.typeArguments = ImmutableList.copyOf(typeArguments);
    }

    public String getQualifiedName() {
        String qualifiedName = packageName;
        if (!qualifiedName.isEmpty()) {
            qualifiedName += ".";
        }

        if (!enclosingNames.isEmpty()) {
            qualifiedName += enclosingNames + ".";
        }

        return qualifiedName + simpleName;
    }

    public String getParameterizedName() {
        return getSimpleName() + getSimpleTypeParameters();
    }

    public String getQualifiedParameterizedName() {
        return getQualifiedName() + getQualifiedTypeParameters();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getEnclosingNames() {
        return enclosingNames;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public boolean isParameterized() {
        return !typeArguments.isEmpty();
    }

    public List<Type> getTypeArguments() {
        return typeArguments;
    }

    public String getQualifiedTypeParameters() {
        return formatTypeParameters(new Function<Type, String>() {
            @Override
            public String apply(Type type) {
                return type.getQualifiedParameterizedName();
            }
        });
    }

    public String getSimpleTypeParameters() {
        return formatTypeParameters(new Function<Type, String>() {
            @Override
            public String apply(Type type) {
                return type.getParameterizedName();
            }
        });
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(typeArguments)
                .transformAndConcat(EXTRACT_IMPORTS_FUNCTION)
                .append(getQualifiedName())
                .toSet();
    }

    @Override
    public int compareTo(Type o) {
        return getQualifiedParameterizedName().compareTo(o == null ? "" : o.getQualifiedParameterizedName());
    }

    @Override
    public String toString() {
        return getQualifiedParameterizedName();
    }

    @Override
    public int hashCode() {
        return getQualifiedParameterizedName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Type)) {
            return false;
        }

        Type other = (Type) obj;
        return getQualifiedParameterizedName().equals(other.getQualifiedParameterizedName());
    }

    private String formatTypeParameters(Function<Type, String> function) {
        if (typeArguments.isEmpty()) {
            return "";
        }

        String qualifiedTypeParameters = FluentIterable.from(typeArguments)
                .transform(function)
                .join(Joiner.on(", "));

        return "<" + qualifiedTypeParameters + ">";
    }
}
