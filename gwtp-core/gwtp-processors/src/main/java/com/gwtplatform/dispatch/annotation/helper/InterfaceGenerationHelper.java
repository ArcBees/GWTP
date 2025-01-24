/*
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.dispatch.annotation.helper;

import java.io.Writer;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * {@link InterfaceGenerationHelper} is an internal class that provides common routines
 * only used by the annotation processors.
 */
public class InterfaceGenerationHelper extends GenerationHelper {
    public InterfaceGenerationHelper(Writer sourceWriter) {
        super(sourceWriter);
    }

    /**
     * Generates an empty method body.
     * <p>
     * If you type:
     * <p>
     * <pre>
     * <code>
     *    writer.generateEmptyMethodBody("MyEntity", "stableId");
     * </code>
     * </pre>
     * <p>
     * The following method body will be generated:
     * <p>
     * <pre>
     * <code>
     *     MyEntity stableId();
     * </code>
     * </pre>
     *
     * @param methodName
     * @param returnType
     */
    public void generateEmptyMethodBody(String methodName, String returnType) {
        println();
        print("  {0} {1}();", returnType, methodName);
    }

    public void generateGetter(String fieldName, String getterType) {
        println();
        print("  {0} get{1}();", getterType, firstCharToUpperCase(fieldName));
        println();
    }

    public void generateGetter(VariableElement fieldElement) {
        println();
        print("  {0} {1}();", fieldElement.asType().toString(), manufactureAccessorName(fieldElement));
        println();
    }

    public void generateInterfaceHeader(String inferfaceName, Set<Modifier> modifiers, String... extendedInterfaces) {
        print("{0}interface {1}", generateModifierList(modifiers.toArray(new Modifier[]{})), inferfaceName);
        if (extendedInterfaces != null && extendedInterfaces.length > 0) {
            printWithoutSpaces(" extends {0}", implode(extendedInterfaces, ","));
        }
        printWithoutSpaces(" { ");
    }

    public void generateSetter(String fieldName, String fieldType) {
        println();
        print("  void {0}({1} {2});",
                manufactureSetterName(fieldName),
                fieldType,
                fieldName
        );
        println();
    }

    public void generateSetter(VariableElement fieldElement) {
        generateSetter(
                fieldElement.getSimpleName().toString(),
                fieldElement.asType().toString()
        );
    }
}
