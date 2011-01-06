/**
 * Copyright 2010 ArcBees Inc.
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

package com.gwtplatform.dispatch.annotation.processor;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

/**
 * {@link GenerationHelper} is an internal class that provides common routines
 * only used by the annotation processors.
 * 
 * @author Brendan Doherty 
 * @author Florian Sauter
 * @author Stephen Haberman (concept) 
 */
public class GenerationHelper implements Closeable {
  
  private PrintWriter writer;
  
  public GenerationHelper(Writer sourceWriter) {
    initializeSourceWriter(sourceWriter);
  }
  
  @Override
  public void close() {
    writer.close();
  }

  public void generateClassFooter() {
    println("}");
  }
  
  /**
   * Generates a class header. Pass null to skip the parent class.
   * 
   * <p>
   * <b>Usage:</b>
   * </p>
   * 
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
   * 
   * <pre>
   * <code>public class MyFoo extends Foo implements HasName, MyGenericInterface{@literal <Foo>} {</code></pre>
   * 
   * @param extendedClassName the parent class name
   * @param interfaceNames array of interface names to be implemented
   */
  public void generateClassHeader(String className, String extendedClassName, String... interfaceNames) {
    println();
    print("{0} class ", Modifier.PUBLIC.toString());
    print(className);
    if (extendedClassName != null) {
      print(" extends " + extendedClassName);
    }
    if (interfaceNames != null && interfaceNames.length > 0) {
      print(" implements ");
      String implodedInterfaceString = implode(interfaceNames, ",");
      print(implodedInterfaceString);
    }
    println(" { ");
  }
  
  public void generateConstantFieldDeclaration(VariableElement fieldElement) {
    if (isConstant(fieldElement)) {
      String constantValue = determineFinalConstantValue(fieldElement);
      if (constantValue != null) {
        println("  {0}{1} {2} = {3};", 
            determineFieldModifiers(fieldElement),
            fieldElement.asType().toString(),
            fieldElement.getSimpleName(),
            constantValue
         );
      } else {
        println("  {0}{1} {2};", 
            determineFieldModifiers(fieldElement),
            fieldElement.asType().toString(),
            fieldElement.getSimpleName()
         );
      }
    }
  }
  
  public void generateConstructorUsingFields(String simpleClassName, Collection<VariableElement> fieldsToBePassedAndAssigned) {
    println();
    print("  {0} {1}(", Modifier.PUBLIC.toString(), simpleClassName);
    generateFieldList(fieldsToBePassedAndAssigned, true, false);
    println(") {");
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
      print("  {0} ", Modifier.PUBLIC);
      print(fieldElement.asType().toString());
      print(" ");
      print(determineAccessorName(fieldElement));
      println("() {");
      print("    return ");
      print(fieldElement.getSimpleName());
      println(";");
      println("  }");
    }
  }
  
  public void generateFieldAssignment(VariableElement fieldElement, Object value) {
    println("    this.{0} = {1};", fieldElement.getSimpleName(), String.valueOf(value));
  }

  public void generateFieldDeclaration(VariableElement fieldElement) {
    println("  {0}{1} {2};", 
        determineFieldModifiers(fieldElement),
        fieldElement.asType().toString(),
        fieldElement.getSimpleName()
    );
  }
  
  /**
   * Generates all field declarations which are included in the passed list.
   */
  public void generateFieldDeclarations(Collection<VariableElement> collection) {
    println();
    for (VariableElement fieldElement : collection) {
      if (isConstant(fieldElement)) {
        generateConstantFieldDeclaration(fieldElement);
      } else {
        generateFieldDeclaration(fieldElement);
      }
    }
  }
  
  /**
   * Generates a list of Fields.
   * 
   *  <p>
   * <b>Usage:</b>
   * </p>
   * 
   * <pre>
   * <code>generateFieldList(myList, true, false)</code></pre>
   * 
   * <b>Generated example:</b>
   * 
   * <pre>
   * <code>
   *  String myField1, int myField2, final String myField3
   * </code></pre>
   */
  public void generateFieldList(Collection<VariableElement> fieldElements, boolean withType, boolean leadingComma) {
    if (fieldElements != null) {
      int i = 0;
      for (VariableElement fieldElement : fieldElements) {
        if (leadingComma || i++ > 0) {
          print(", ");
        }
        if (withType) {
          print(fieldElement.asType().toString());
          print(" ");
        }
        print(fieldElement.getSimpleName());
      }
    }
  }

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
            println("    hashCode = (hashCode * 37) + new {0}({1}).hashCode();", determineWrapperClass(type), fieldName);
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
  
  /**
   * Use null as import to separate import groups.
   * 
   * <p>
   * <b>Usage:</b>
   * </p>
   *
   * <pre>
   * <code>
   * generateImports({@link com.google.gwt.event.shared.EventHandler EventHandler}.class, {@link com.google.gwt.event.shared.GwtEvent GwtEvent}.class, null, {@link GenEventProcessor}.class)
   * </code>
   * </pre>
   * 
   * <b>Generated example:</b>
   * 
   * <pre>
   * <code>
   *  import {@link com.google.gwt.event.shared.EventHandler};
   *  import {@link com.google.gwt.event.shared.EventHandler};
   *  
   *  import {@link com.gwtplatform.dispatch.annotation.processor.GenEventProcessor};
   * </code></pre>
   * 
   * TODO: It seems as the compiler can't find GWT classes during generation - why?
   * 
   * @param imports array of classes to be imported
   */
  public void generateImports(Class<?>... imports) {
    println();
    for (Class<?> importClass : imports) {
      if (importClass == null) {
        println();
      } else {
        println("import {0};", importClass.getClass().getName());
      }
    }
  }
  
  public void generateImports(String... imports) {
    println();
    for (String importClass : imports) {
      if (importClass == null) {
        println();
      } else {
        println("import {0};", importClass);
      }
    }
  }
  
  /**
   * Generates a package declaration.
   * 
   * <p>
   * <b>Generated example:</b>
   * </p>
   * <code>package com.gwtplatform.dispatch.annotation.processor;</code>
   */
  public void generatePackageDeclaration(String packageName) {
    println("package {0};", packageName);
  }
  
  private String determineAccessorName(VariableElement fieldElement) {
    String name;
    if (fieldElement.asType().toString().equals(java.lang.Boolean.class.getSimpleName().toLowerCase())) {
      name = "is";
    } else {
      name = "get";
    }
    name += fieldElement.getSimpleName().toString().substring(0, 1).toUpperCase();
    name += fieldElement.getSimpleName().toString().substring(1);

    return name;
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
  
  private String determineFieldModifiers(VariableElement fieldElement) {
    String fieldModifier = "";
    Set<Modifier> modifiers = fieldElement.getModifiers();
    for (Modifier modifier : modifiers) {
        fieldModifier =  fieldModifier + modifier.toString() + " ";
    }
    return fieldModifier;
  }
  
  /**
   * Note that to have a constant value, a field's type must be either a primitive type or String otherwise the value is null.
   */
  private String determineFinalConstantValue(VariableElement fieldElement) {
    Object fieldConstantValue = fieldElement.getConstantValue();
    String determinedConstantValue = null;

    if (fieldConstantValue instanceof java.lang.String) {
      determinedConstantValue = "\"" + String.valueOf(fieldConstantValue) + "\"";
    } else if (fieldConstantValue instanceof java.lang.Character) {
      determinedConstantValue = "'" + String.valueOf(fieldConstantValue) + "'";
    } else if (isPrimitive(fieldElement.asType())) {
      determinedConstantValue = String.valueOf(fieldConstantValue);
      if ("null".equals(determinedConstantValue)) {
        determinedConstantValue = null;
      }
    }
    
    return determinedConstantValue;
  }
  
  /**
   * Returns the name of the wrapper class for a primitive class.
   */
  private String determineWrapperClass(TypeMirror type) {
    String typeName = type.toString();
    if (typeName.equals("byte")) {
      return java.lang.Byte.class.getSimpleName();
    }
    if (typeName.equals("short")) {
      return java.lang.Short.class.getSimpleName();
    }
    if (typeName.equals("int")) {
      return java.lang.Integer.class.getSimpleName();
    }
    if (typeName.equals("long")) {
      return java.lang.Long.class.getSimpleName();
    }
    if (typeName.equals("float")) {
      return java.lang.Float.class.getSimpleName();
    }
    if (typeName.equals("double")) {
      return java.lang.Double.class.getSimpleName();
    }
    if (typeName.equals("char")) {
      return java.lang.Character.class.getSimpleName();
    }
    if (typeName.equals("boolean")) {
      return java.lang.Boolean.class.getSimpleName();
    } else {
      return null;
    }
  }
  
  /**
   * Checks if a type is a primitive type.
   */
  public boolean isPrimitive(TypeMirror type) {
    String typeName = type.toString();
    if (typeName.equals("byte")) {
      return true;
    }
    if (typeName.equals("short")) {
      return true;
    }
    if (typeName.equals("int")) {
      return true;
    }
    if (typeName.equals("long")) {
      return true;
    }
    if (typeName.equals("float")) {
      return true;
    }
    if (typeName.equals("double")) {
      return true;
    }
    if (typeName.equals("char")) {
      return true;
    }
    if (typeName.equals("boolean")) {
      return true;
    } 
    return false;    
  }
  
  /**
   * Checks if a field contains a static or final modifier.
   */
  public boolean isConstant(VariableElement fieldElement) {
    return fieldElement.getModifiers().contains(Modifier.STATIC) || fieldElement.getModifiers().contains(Modifier.FINAL);
  }
  
  /**
   * Checks if a field contains a static or final modifier.
   */
  public boolean isStatic(VariableElement fieldElement) {
    return fieldElement.getModifiers().contains(Modifier.STATIC);
  }
  
  public void print(Object o) {
    writer.print(o);
  }
  
  public void print(String s, Object... parameters) {
    print(replaceParameters(s, parameters));
  }
  
  public void println() {
    writer.println();
  }
  
  public void println(Object o) {
    writer.println(o);
  }
  
  public void println(String s, Object... parameters) {
    println(replaceParameters(s, parameters));
  }
  
  private void initializeSourceWriter(Writer sourceWriter) {
    BufferedWriter bufferedWriter = new BufferedWriter(sourceWriter);
    writer = new PrintWriter(bufferedWriter);
  }
  
  /**
   * Construct a single string from an array of strings, gluing them together
   * with the specified delimiter.
   * 
   * @param segments array of strings
   * @param delimiter character that glues the passed strings together
   * @return imploded and glued list of strings
   */
  public static String implode(String[] segments, String delimiter) {
    String implodedString;
    if (segments.length == 0) {
      implodedString = "";
   } else {
       StringBuffer sb = new StringBuffer();
       sb.append(segments[0]);
       for (int i = 1; i < segments.length; i++) {
         if (segments[i] != null && !segments[i].isEmpty()) {
           sb.append(",");
           sb.append(segments[i]);
         }
      }
      implodedString = sb.toString();
    }
    return implodedString;
  }
  
  /**
   * Replaces each placeholder of this string that matches a parameter index.
   * <p><b>Placeholder format:</b> {int}</p>
   * 
   * <p><b>Usage:</b></p>
   * <pre><code>replaceParameters("{0} int myField = {1};", "private", 20);</code></pre>
   * 
   * @param target the string to be replace.
   * @param parameters the replacement parameters
   * @return the resulting string. 
   *   <p>For example:</p> <code>private int myField = 20;</code>
   */
  public static String replaceParameters(String target , Object... parameters) {
    String result = target;
    if (parameters != null) {
      for (int i = 0; i < parameters.length; i++) {
        result = result.replace("{" + i + "}", String.valueOf(parameters[i]));
      }
    }
    return result;
  }
}
