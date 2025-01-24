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
import java.util.Collection;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

/**
 * {@link ClassGenerationHelper} is an internal class that provides common routines
 * only used by the annotation processors.
 */
public class ClassGenerationHelper extends GenerationHelper {

    public ClassGenerationHelper(Writer sourceWriter) {
        super(sourceWriter);
    }

    /**
     * Generates a class header. Pass null to skip the parent class.
     * <p>
     * <p>
     * <b>Usage:</b>
     * </p>
     * <p>
     * <pre>
     * <code>
     *  generateClassHeader(Foo.class.getSimpleName(),
     *        HasName.class.getSimpleName,
     *        "MyGenericInterface{@literal <Foo>}"
     *  )
     *  </code>
     * </pre>
     * <p>
     * <b>Generated example:</b>
     * </p>
     * <p>
     * <pre>
     * <code>public class MyFoo extends Foo implements HasName, MyGenericInterface{@literal <Foo>} {</code></pre>
     *
     * @param className         the simple class name
     * @param modifiers         the modifiers for the class
     * @param extendedClassName the parent class name
     * @param extraInterfaces   array of interface names to be implemented
     */
    public void generateClassHeader(String className, String extendedClassName, Set<Modifier> modifiers,
            String... extraInterfaces) {
        println();
        print("{0}class {1}", generateModifierList(modifiers.toArray(new Modifier[]{})), className);
        if (extendedClassName != null && !extendedClassName.isEmpty()) {
            String trimedExtendedClassName = extendedClassName.trim();
            printWithoutSpaces(" extends {0}", trimedExtendedClassName);
        }
        if (extraInterfaces != null && extraInterfaces.length > 0) {
            printWithoutSpaces(" implements {0}", implode(extraInterfaces, ","));
        }
        printWithoutSpaces(" { ");
    }

    public void generateConstructorUsingFields(String simpleClassName,
            Collection<VariableElement> fieldsToBePassedAndAssigned, Modifier... constructorModifiers) {
        println();
        println("  {0}{1}({2}) {",
                generateModifierList(constructorModifiers),
                simpleClassName,
                generateFieldList(fieldsToBePassedAndAssigned, true, false)
        );
        if (fieldsToBePassedAndAssigned != null) {
            for (VariableElement fieldToBeAssigned : fieldsToBePassedAndAssigned) {
                generateFieldAssignment(fieldToBeAssigned, fieldToBeAssigned.getSimpleName().toString());
            }
        }
        println("  }");
    }

    public void generateEmptyConstructor(String simpleClassName, Modifier modifier) {
        println();
        if (modifier != null) {
            println("  {0} {1}() {", modifier, simpleClassName);
        } else {
            println("  {0}() {", simpleClassName);
        }
        println("    // Possibly for serialization.");
        println("  }");
    }

    /**
     * Creates an equals method using Java standards.
     *
     * @param simpleClassName the class name
     * @param fieldElements   the field elements
     */
    public void generateEquals(String simpleClassName, Collection<VariableElement> fieldElements) {
        println();
        println("  @Override");
        println("  public boolean equals(Object obj) {");
        if (fieldElements.size() > 0) {
            println("    if (this == obj)");
            println("        return true;");
            println("    if (obj == null)");
            println("        return false;");
            println("    if (getClass() != obj.getClass())");
            println("        return false;");
            println("    {0} other = ({0}) obj;", simpleClassName);

            for (VariableElement fieldElement : fieldElements) {
                TypeMirror type = fieldElement.asType();
                String fieldName = fieldElement.getSimpleName().toString();
                if (!isStatic(fieldElement)) {
                    if (isPrimitive(type)) {
                        println("    if ({0} != other.{0})", fieldName);
                        println("        return false;");
                    } else {
                        println("    if ({0} == null) {", fieldName);
                        println("      if (other.{0} != null)", fieldName);
                        println("        return false;");
                        println("    } else if (!{0}.equals(other.{0}))", fieldName);
                        println("      return false;");
                    }
                }
            }
            println("    return true;");
        } else {
            println("    return super.equals(obj);");
        }
        println("  }");
    }

    public void generateFieldAccessors(Collection<VariableElement> fieldElements) {
        for (VariableElement fieldElement : fieldElements) {
            println();
            println("  {0} {1} {2}(){", Modifier.PUBLIC, fieldElement.asType().toString(),
                    manufactureAccessorName(fieldElement));
            println("    return {0};", fieldElement.getSimpleName());
            println("  }");
        }
    }

    /**
     * Creates a default field assignment.
     * <p>
     * <p>
     * <code>generateFieldAssignment(myNameIsTest, null)</code>
     * </p>
     * <p>
     * Generates:
     * <p>
     * <p>
     * <code>    this.test = null;</code>
     * </p>
     *
     * @param fieldElement
     * @param value
     */
    public void generateFieldAssignment(VariableElement fieldElement, Object value) {
        println("    this.{0} = {1};", fieldElement.getSimpleName(), String.valueOf(value));
    }

    /**
     * Creates an hashCode method using Java standards.
     *
     * @param fieldElements the field elements
     */
    public void generateHashCode(Collection<VariableElement> fieldElements) {
        println();
        println("  @Override");
        println("  public int hashCode() {");
        if (fieldElements.size() > 0) {
            println("    int hashCode = 23;");
            for (VariableElement fieldElement : fieldElements) {
                TypeMirror type = fieldElement.asType();
                String fieldName = fieldElement.getSimpleName().toString();
                if (!isStatic(fieldElement)) {
                    if (type instanceof ArrayType) {
                        // hashCode = (hashCode * 37) + java.util.Arrays.deepHashCode(banana);
                        println("    hashCode = (hashCode * 37) + java.util.Arrays.deepHashCode({0});", fieldName);
                    } else if (isPrimitive(type)) {
                        // hashCode = (hashCode * 37) + new Integer(height).hashCode();
                        println("    hashCode = (hashCode * 37) + new {0}({1}).hashCode();",
                                determineWrapperClass(type), fieldName);
                    } else {
                        // hashCode = (hashCode * 37) + (blah == null ? 1 : blah.hashCode());
                        println("    hashCode = (hashCode * 37) + ({0} == null ? 1 : {0}.hashCode());", fieldName);
                    }
                }
            }
            println("    return hashCode;");
        } else {
            println("    return super.hashCode();");
        }
        println("  }");
    }

    public void generateToString(String simpleClassName, Collection<VariableElement> fieldElements) {
        println();

        println("  @Override");
        println("  public String toString() {");
        println("    return \"" + simpleClassName + "[\"");
        int i = 0;
        for (VariableElement fieldElement : fieldElements) {
            if (i++ > 0) {
                println("                 + \",\"");
            }
            println("                 + " + fieldElement.getSimpleName());
        }
        println("    + \"]\";");
        println("  }");
    }

}
