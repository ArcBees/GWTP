package com.gwtplatform.annotation.processor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.type.ArrayType;
import com.sun.mirror.type.TypeMirror;

class AnnotationHelper {
  private PrintWriter out;

  public AnnotationHelper(PrintWriter out) {
    this.out = out;
  }

  public void generateFields(Collection<FieldDeclaration> fields) {

    for (FieldDeclaration fieldDecl : fields) {

      out.print("  private ");
      out.print(fieldDecl.getType().toString());
      out.print(" ");
      out.print(fieldDecl.getSimpleName());
      out.println(";");
    }
    out.println();
  }

  public void generateAccessors(Collection<FieldDeclaration> fields) {
    for (FieldDeclaration fieldDecl : fields) {
      out.print("  public ");
      out.print(fieldDecl.getType().toString());
      out.print(" ");
      out.print(accessorName(fieldDecl));
      out.println("() {");
      out.print("    return ");
      out.print(fieldDecl.getSimpleName());
      out.println(";");
      out.println("  }");
      out.println();
    }
  }

  public void generateEquals(String className,
      Collection<FieldDeclaration> fields) {
    out.println("  @Override");
    out.println("  public boolean equals(Object other) {");
    out
        .println("    if (other != null && other.getClass().equals(this.getClass())) {");
    out.println("          " + className + " o = (" + className + ") other;");
    out.println("      return true");
    for (FieldDeclaration fieldDecl : fields) {

      TypeMirror type = fieldDecl.getType();
      if (type instanceof ArrayType) {
        // && java.util.Arrays.deepEquals(o.banana, this.banana)
        out.print("          && java.util.Arrays.deepEquals(o.");
        out.print(fieldDecl.getSimpleName());
        out.print(", this.");
        out.print(fieldDecl.getSimpleName());
        out.println(")");

      } else if (isPrimative(type.toString())) {
        // && o.blah == this.blah
        out.print("          && o.");
        out.print(fieldDecl.getSimpleName());
        out.print(" == this.");
        out.println(fieldDecl.getSimpleName());
      } else {
        // && ((o.blah == null && this.blah == null) || (o.blah != null &&
        // o.blah.equals(this.blah)))
        out.print("          && ((o.");
        out.print(fieldDecl.getSimpleName());
        out.print(" == null && this.");
        out.print(fieldDecl.getSimpleName());
        out.print(" == null) || (o.");
        out.print(fieldDecl.getSimpleName());
        out.print(" != null && o.");
        out.print(fieldDecl.getSimpleName());
        out.print(".equals(this.");
        out.print(fieldDecl.getSimpleName());
        out.println(")))");
      }
    }
    out.println("        ;");
    out.println("    }");
    out.println("    return false;");
    out.println("  }");
    out.println();

  }

  public void generateHashCode(Collection<FieldDeclaration> fields) {
    out.println("  @Override");
    out.println("  public int hashCode() {");
    out.println("    int hashCode = 23;");
    out.println("    hashCode = (hashCode * 37) + getClass().hashCode();");
    for (FieldDeclaration fieldDecl : fields) {
      out.print("    hashCode = (hashCode * 37) + ");
      TypeMirror type = fieldDecl.getType();
      if (type instanceof ArrayType) {
        // hashCode = (hashCode * 37) + java.util.Arrays.deepHashCode(banana);
        out.print("java.util.Arrays.deepHashCode(");
        out.print(fieldDecl.getSimpleName());
        out.println(");");
      } else if (isPrimative(fieldDecl.getType().toString())) {
        // hashCode = (hashCode * 37) + new Integer(height).hashCode();
        out.print("new ");
        out.print(getWrapperClass(fieldDecl.getType().toString()));
        out.print("(");
        out.print(fieldDecl.getSimpleName());
        out.println(").hashCode();");

      } else {
        // hashCode = (hashCode * 37) + (blah == null ? 1 : blah.hashCode());
        out.print("(");
        out.print(fieldDecl.getSimpleName());
        out.print(" == null ? 1 : ");
        out.print(fieldDecl.getSimpleName());
        out.println(".hashCode());");
      }

    }
    out.println("    return hashCode;");
    out.println("  }");
    out.println();
  }

  public void generateToString(String className,
      Collection<FieldDeclaration> fields) {
  
    out.println("  @Override");
    out.println("  public String toString() {");
    out.println("    return \"" + className + "[\"");
    int i = 0;
    for(FieldDeclaration fieldDecl : fields) {
      if(i++ > 0) {
        out.println("                 + \",\"");
      }
      out.println("                 + " + fieldDecl.getSimpleName());
    }
    out.println("    + \"]\";");
    out.println("  }");
    out.println("");
    
  }
  
  public void generateFieldList(Collection<FieldDeclaration> fields, boolean withType, boolean leadingComma) {
    int i = 0;
    for(FieldDeclaration fieldDecl : fields) {
      if(leadingComma || i++ > 0) {
        out.print(", ");            
      }
      if(withType) {
        out.print(fieldDecl.getType().toString());
        out.print(" ");
      }
      out.print(fieldDecl.getSimpleName());
    }
  }
  

  
  private String accessorName(FieldDeclaration fieldDecl) {
    String name;
    if (fieldDecl.getType().toString().equals("boolean")) {
      name = "is";
    } else {
      name = "get";
    }
    name += fieldDecl.getSimpleName().substring(0, 1).toUpperCase();
    name += fieldDecl.getSimpleName().substring(1);

    return name;
  }

  // returns the name of the wrapper class for primitive classes
  private boolean isPrimative(String typeString) {
    if (typeString.equals("byte")) {
      return true;
    }
    if (typeString.equals("short")) {
      return true;
    }
    if (typeString.equals("int")) {
      return true;
    }
    if (typeString.equals("long")) {
      return true;
    }
    if (typeString.equals("float")) {
      return true;
    }
    if (typeString.equals("double")) {
      return true;
    }
    if (typeString.equals("char")) {
      return true;
    }
    if (typeString.equals("boolean")) {
      return true;
    } else {
      return false;
    }
  }

  // returns the name of the wrapper class for primitive classes
  private String getWrapperClass(String typeString) {
    if (typeString.equals("byte")) {
      return java.lang.Byte.class.getSimpleName();
    }
    if (typeString.equals("short")) {
      return java.lang.Short.class.getSimpleName();
    }
    if (typeString.equals("int")) {
      return java.lang.Integer.class.getSimpleName();
    }
    if (typeString.equals("long")) {
      return java.lang.Long.class.getSimpleName();
    }
    if (typeString.equals("float")) {
      return java.lang.Float.class.getSimpleName();
    }
    if (typeString.equals("double")) {
      return java.lang.Double.class.getSimpleName();
    }
    if (typeString.equals("char")) {
      return java.lang.Character.class.getSimpleName();
    }
    if (typeString.equals("boolean")) {
      return java.lang.Boolean.class.getSimpleName();
    } else {
      return null;
    }
  }

}
