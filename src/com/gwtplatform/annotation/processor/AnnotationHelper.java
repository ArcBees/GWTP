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

package com.gwtplatform.annotation.processor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import com.gwtplatform.annotation.Optional;
import com.gwtplatform.annotation.Order;

/**
 * AnnotationHelper is an internal class that provides common routines only used
 * by the annotation processors.
 * 
 * @author Brendan Doherty
 */

class AnnotationHelper {
  private final ProcessingEnvironment env;

  public AnnotationHelper(ProcessingEnvironment env) {
    this.env = env;
  }

  public String getPackage(Element classElement) {
    return env.getElementUtils().getPackageOf(classElement).getQualifiedName().toString();
  }

  public String getClassName(Element classElement) {
    return classElement.getSimpleName().toString();
  }

  public String getQName(Element classElement) {
    return getPackage(classElement) + '.' + getClassName(classElement);
  }

  public List<VariableElement> getFields(Element classElement) {
    return ElementFilter.fieldsIn(env.getElementUtils().getAllMembers(
        (TypeElement) classElement));
  }

  public List<VariableElement> getOptionalFields(Element classElement) {
    List<VariableElement> optionalFields = new ArrayList<VariableElement>();
    Collection<VariableElement> fields = getOrderedFields(classElement).values();
    for (VariableElement field : fields) {
      Optional elementsOptionalAnnotation = field.getAnnotation(Optional.class);
      if (elementsOptionalAnnotation != null) {
        optionalFields.add(field);
      }
    }
    return optionalFields;
  }

  public void generatePackageDeclaration(PrintWriter out, String packageName) {
    out.println("package " + packageName + ";");
  }

  /**
   * Use null or an empty string as class name to separate package groups.
   */
  public void generateImports(PrintWriter out, String... importClassNames) {
    out.println();
    for (String importClassName : importClassNames) {
      if (importClassName == null || importClassName.isEmpty()) {
        out.println();
      } else {
        out.println("import " + importClassName + ";");
      }
    }
  }
  
  public void generateClassFooter(PrintWriter out) {
    out.println("}");
  }
  
  public void generateFields(PrintWriter out,
      Collection<VariableElement> collection, boolean useFinal) {

    for (VariableElement fieldElement : collection) {

      out.print("  private ");
      if (useFinal && !isPrimitive(fieldElement.asType())) {
        out.print("final ");
      }
      out.print(fieldElement.asType().toString());
      out.print(" ");
      out.print(fieldElement.getSimpleName());
      out.println(";");
    }
    out.println();
  }

  public void generateAccessors(PrintWriter out,
      Collection<VariableElement> fieldElements) {

    for (VariableElement fieldElement : fieldElements) {

      out.print("  public ");
      out.print(fieldElement.asType().toString());
      out.print(" ");
      out.print(accessorName(fieldElement));
      out.println("() {");
      out.print("    return ");
      out.print(fieldElement.getSimpleName());
      out.println(";");
      out.println("  }");
      out.println();
    }
  }

  public void generateEquals(PrintWriter out, String className,
      Collection<VariableElement> fieldElements) {
    out.println("  @Override");
    out.println("  public boolean equals(Object other) {");
    out.println("    if (other != null && other.getClass().equals(this.getClass())) {");
    out.println("          " + className + " o = (" + className + ") other;");
    out.println("      return true");
    for (VariableElement fieldElement : fieldElements) {

      TypeMirror type = fieldElement.asType();
      if (type instanceof ArrayType) {
        // && java.util.Arrays.deepEquals(o.banana, this.banana)
        out.print("          && java.util.Arrays.deepEquals(o.");
        out.print(fieldElement.getSimpleName());
        out.print(", this.");
        out.print(fieldElement.getSimpleName());
        out.println(")");

      } else if (isPrimitive(type)) {
        // && o.blah == this.blah
        out.print("          && o.");
        out.print(fieldElement.getSimpleName());
        out.print(" == this.");
        out.println(fieldElement.getSimpleName());
      } else {
        // && ((o.blah == null && this.blah == null) || (o.blah != null &&
        // o.blah.equals(this.blah)))
        out.print("          && ((o.");
        out.print(fieldElement.getSimpleName());
        out.print(" == null && this.");
        out.print(fieldElement.getSimpleName());
        out.print(" == null) || (o.");
        out.print(fieldElement.getSimpleName());
        out.print(" != null && o.");
        out.print(fieldElement.getSimpleName());
        out.print(".equals(this.");
        out.print(fieldElement.getSimpleName());
        out.println(")))");
      }
    }
    out.println("        ;");
    out.println("    }");
    out.println("    return false;");
    out.println("  }");
    out.println();
  }

  public void generateHashCode(PrintWriter out,
      Collection<VariableElement> fieldElements) {
    out.println("  @Override");
    out.println("  public int hashCode() {");
    out.println("    int hashCode = 23;");
    out.println("    hashCode = (hashCode * 37) + getClass().hashCode();");
    for (VariableElement fieldElement : fieldElements) {
      out.print("    hashCode = (hashCode * 37) + ");
      TypeMirror type = fieldElement.asType();
      if (type instanceof ArrayType) {
        // hashCode = (hashCode * 37) + java.util.Arrays.deepHashCode(banana);
        out.print("java.util.Arrays.deepHashCode(");
        out.print(fieldElement.getSimpleName());
        out.println(");");
      } else if (isPrimitive(fieldElement.asType())) {
        // hashCode = (hashCode * 37) + new Integer(height).hashCode();
        out.print("new ");
        out.print(getWrapperClass(fieldElement.asType()));
        out.print("(");
        out.print(fieldElement.getSimpleName());
        out.println(").hashCode();");

      } else {
        // hashCode = (hashCode * 37) + (blah == null ? 1 : blah.hashCode());
        out.print("(");
        out.print(fieldElement.getSimpleName());
        out.print(" == null ? 1 : ");
        out.print(fieldElement.getSimpleName());
        out.println(".hashCode());");
      }
    }
    out.println("    return hashCode;");
    out.println("  }");
    out.println();
  }
  
  public void generateFieldAssignment(PrintWriter out, VariableElement field, Object value) {
    out.println("    this." + field.getSimpleName() + " = " + String.valueOf(value) + ";");
  }

  public void generateToString(PrintWriter out, String className,
      Collection<VariableElement> fieldElements) {

    out.println("  @Override");
    out.println("  public String toString() {");
    out.println("    return \"" + className + "[\"");
    int i = 0;
    for (VariableElement fieldElement : fieldElements) {
      if (i++ > 0) {
        out.println("                 + \",\"");
      }
      out.println("                 + " + fieldElement.getSimpleName());
    }
    out.println("    + \"]\";");
    out.println("  }");
    out.println("");
  }

  public void generateFieldList(PrintWriter out,
      Collection<VariableElement> fieldElements, boolean withType,
      boolean leadingComma) {
    int i = 0;
    for (VariableElement fieldElement : fieldElements) {
      if (leadingComma || i++ > 0) {
        out.print(", ");
      }
      if (withType) {
        out.print(fieldElement.asType().toString());
        out.print(" ");
      }
      out.print(fieldElement.getSimpleName());
    }
  }

  SortedMap<Integer, VariableElement> getOrderedFields(Element classElement) {
    int maxOrderNum = -1;
    for (VariableElement fieldElement : getFields(classElement)) {
      Order order = fieldElement.getAnnotation(Order.class);
      if (order != null) {
        maxOrderNum = Math.max(maxOrderNum, order.value());
      }
    }

    SortedMap<Integer, VariableElement> fieldsMap = new TreeMap<Integer, VariableElement>();
    for (VariableElement fieldDecl : getFields(classElement)) {
      Order order = fieldDecl.getAnnotation(Order.class);
      if (order != null) {
        maxOrderNum = Math.max(maxOrderNum, order.value());
        fieldsMap.put(order.value(), fieldDecl);
      } else {
        fieldsMap.put(++maxOrderNum, fieldDecl);
      }
    }
    return fieldsMap;
  }

  private String accessorName(VariableElement fieldElement) {
    String name;
    if (fieldElement.asType().toString().equals("boolean")) {
      name = "is";
    } else {
      name = "get";
    }
    name += fieldElement.getSimpleName().toString().substring(0, 1).toUpperCase();
    name += fieldElement.getSimpleName().toString().substring(1);

    return name;
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
   * Returns the name of the wrapper class for a primitive class.
   */
  private String getWrapperClass(TypeMirror type) {
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

}
