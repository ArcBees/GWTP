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

package com.gwtplatform.dispatch.annotation.helper;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
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
  
  /**
   * Construct a single string from an array of strings, gluing them together
   * with the specified delimiter.
   * 
   * @param segments array of strings
   * @param delimiter character that glues the passed strings together
   * @return imploded and glued list of strings
   */
  public static String implode(Object[] segments, String delimiter) {
    String implodedString;
    if (segments.length == 0) {
      implodedString = "";
   } else {
       StringBuffer sb = new StringBuffer();
       sb.append(segments[0]);
       for (int i = 1; i < segments.length; i++) {
         if (segments[i] != null && !segments[i].toString().isEmpty()) {
           sb.append(delimiter);
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
  
  private int whitespaces;
  
  private PrintWriter writer;
  
  public GenerationHelper(Writer sourceWriter) {
    initializeSourceWriter(sourceWriter);
  }
  
  @Override
  public void close() {
    writer.close();
  }

  public String firstCharToUpperCase(String charSequence) {
    String upperCased = "";
    upperCased += charSequence.substring(0, 1).toUpperCase();
    upperCased += charSequence.substring(1);
    return upperCased;
  }
  
  public void generateAnnotation(String className, String value) {
    if (value == null) {
      println("@{0}", className);
    } else {
      println("@{0}({1})", className, value);
    }
  }
  
  public void generateConstantFieldDeclaration(VariableElement fieldElement) {
    if (isConstant(fieldElement)) {
      String constantValue = determineFinalConstantValue(fieldElement);
      if (constantValue != null) {
        println("  {0}{1} {2} = {3};", 
            generateModifierList(fieldElement.getModifiers().toArray(new Modifier[]{})),
            fieldElement.asType().toString(),
            fieldElement.getSimpleName(),
            constantValue
         );
      } else {
        println("  {0}{1} {2};", 
            generateModifierList(fieldElement.getModifiers().toArray(new Modifier[]{})),
            fieldElement.asType().toString(),
            fieldElement.getSimpleName()
         );
      }
    }
  }
  
  public void generateFieldDeclaration(VariableElement fieldElement) {
    println("  {0}{1} {2};", 
        generateModifierList(fieldElement.getModifiers().toArray(new Modifier[]{})),
        fieldElement.asType().toString(),
        fieldElement.getSimpleName()
    );
  } 
  public void generateFieldDeclaration(VariableElement fieldElement, Modifier... modifiers) {
    println("  {0}{1} {2};", 
        generateModifierList(modifiers),
        fieldElement.asType().toString(),
        fieldElement.getSimpleName()
    );
  }

  /**
   * Generates all field declarations which are included in the passed list.
   */
  public void generateFieldDeclarations(Collection<VariableElement> collection) {
    println();
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
  public String generateFieldList(Collection<VariableElement> fieldElements, boolean withType, boolean leadingComma) {
    String fieldList = "";
    if (fieldElements != null && fieldElements.size() > 0) {
      int i = 0;
      for (VariableElement fieldElement : fieldElements) {
        if (leadingComma || i++ > 0) {
          fieldList += ", ";
        }
        if (withType) {
          fieldList += fieldElement.asType().toString();
          fieldList += " ";
        }
        fieldList += fieldElement.getSimpleName();
      }
    }
    return fieldList;
  }
  
  public void generateFooter() {
    println("}");
  }
  
  /**
   * Use null as import to separate import groups.
   * 
   * <p>
   * <b>Usage:</b>
   * </p>
   * 
   * <pre>
   * <code>generateImports({@link EventHandler}.class, {@link GwtEvent}.class, null, {@link GenEventProcessor}.class)</code></pre>
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
   * TODO: It seems as the compiler can't find GWTP classes during generation - why?
   * 
   * @param imports array of classes to be imported
   */
  public void generateImports(Class<?>... imports) {
    println();
    for (Class<?> importClass : imports) {
      if (importClass == null) {
        println();
      } else {
        println("import {0};", importClass.getName());
      }
    }
  }
  
  /**
   * @see GenerationHelper#generateImports(Class...)
   */
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
  
  public String generateModifierList(Modifier... modifiers) {
    String fieldModifier = "";
    if (modifiers != null && modifiers.length > 0) { 
      fieldModifier = implode(modifiers, " ");
    }
    return fieldModifier.isEmpty() ? fieldModifier : fieldModifier + " ";
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
  
  /**
   * Checks if a field contains a static or final modifier.
   */
  public boolean isConstant(VariableElement fieldElement) {
    return fieldElement.getModifiers().contains(Modifier.STATIC) || fieldElement.getModifiers().contains(Modifier.FINAL);
  }
  
  /**
   * Checks if a field contains a final modifier.
   */
  public boolean isFinal(VariableElement fieldElement) {
    return fieldElement.getModifiers().contains(Modifier.FINAL);
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
   * Checks if a field contains a static modifier.
   */
  public boolean isStatic(VariableElement fieldElement) {
    return fieldElement.getModifiers().contains(Modifier.STATIC);
  }
  
  /**
   * Returns the field's type together with the field's simple name.
   */
  public String manufactureField(VariableElement fieldElement) {
    return fieldElement.asType().toString() + " " +  fieldElement.getSimpleName();
  }
  
  public void print(Object o) {
    writer.print(manufactureIndentation() + o);
  }
  
  public void print(String s, Object... parameters) {
    print(replaceParameters(s, parameters));
  }
  
  public void println() {
    writer.println();
  }
  
  public void println(Object o) {
    writer.println(manufactureIndentation() + o);
  }
  
  public void println(String s, Object... parameters) {
    println(replaceParameters(s, parameters));
  }
  
  public void printWithoutSpaces(String s, Object... parameters) {
    writer.print(replaceParameters(s, parameters));
  }
  
  public void resetWhitespaces() {
    this.whitespaces = 0;
  }

  public void setWhitespaces(int whitespace) {
    this.whitespaces = whitespace;
  }
  
  /**
   * Note that to have a constant value, a field's type must be either a primitive type or String otherwise the value is null.
   */
  protected String determineFinalConstantValue(VariableElement fieldElement) {
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
  protected String determineWrapperClass(TypeMirror type) {
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
  
  protected String manufactureAccessorName(VariableElement fieldElement) {
    String name;
    if (fieldElement.asType().toString().equals(java.lang.Boolean.class.getSimpleName().toLowerCase())) {
      name = "is";
    } else {
      name = "get";
    }
    name += firstCharToUpperCase(fieldElement.getSimpleName().toString());
    return name;
  }
  
  protected String manufactureSetterName(String fieldName) {
    String name = "set";
    name += firstCharToUpperCase(fieldName);
    return name;
  }
  
  protected String manufactureSetterName(VariableElement fieldElement) {
    return manufactureSetterName(fieldElement.getSimpleName().toString());
  }
  
  protected String manufactureIndentation() {
    String space = "";
    for (int i = 0; i < whitespaces; i++) {
      space += " "; 
    }
    return space;
  }

  private void initializeSourceWriter(Writer sourceWriter) {
    BufferedWriter bufferedWriter = new BufferedWriter(sourceWriter);
    writer = new PrintWriter(bufferedWriter);
  }
}
