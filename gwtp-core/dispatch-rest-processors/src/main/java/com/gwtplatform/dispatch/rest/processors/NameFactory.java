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

package com.gwtplatform.dispatch.rest.processors;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor6;

import com.gwtplatform.dispatch.rest.processors.domain.Type;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;

import static javax.lang.model.util.ElementFilter.methodsIn;

import static com.google.auto.common.MoreElements.asType;

public class NameFactory {
    /**
     * Generate a unique class name based on a method. Since methods may define overloads, it's possible we end up with
     * name clashes. This function will prefix the requested name with the parent name and the method index to ensure
     * uniqueness.
     */
    public static Type endPointName(Elements elements, Type parent, ExecutableElement method) {
        String packageName = parent.getPackageName();
        String parentName = parent.getSimpleName();

        Name methodName = method.getSimpleName();
        int methodIndex = indexInParent(elements, method);
        String className = String.format("%s_%d_%s", parentName, methodIndex, methodName);

        return new Type(packageName, className);
    }

    private static int indexInParent(Elements elements, ExecutableElement method) {
        TypeElement type = asType(method.getEnclosingElement());
        List<ExecutableElement> methods = methodsIn(elements.getAllMembers(type));
        return methods.indexOf(method);
    }

    public static String parentName(VariableElement element) {
        return element.getEnclosingElement().accept(new SimpleElementVisitor6<String, Void>("") {
            @Override
            public String visitExecutable(ExecutableElement parent, Void v) {
                return methodName(parent);
            }

            @Override
            public String visitType(TypeElement parent, Void v) {
                return parent.getQualifiedName().toString();
            }
        }, null);
    }

    public static String methodName(ExecutableElement element) {
        Name className = asType(element.getEnclosingElement()).getQualifiedName();
        Name methodName = element.getSimpleName();

        return className + "#" + methodName;
    }

    public static String methodName(Type parent, ExecutableElement element) {
        return parent.getQualifiedName() + "#" + element.getSimpleName();
    }

    public static String methodName(ResourceMethod method) {
        return method.getResource().getImpl() + "#" + method.getMethod().getName();
    }
}
