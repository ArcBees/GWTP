/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gwtplatform.annotation.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.SortedMap;
import java.util.TreeMap;

import com.gwtplatform.annotation.GenDto;
import com.gwtplatform.annotation.In;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.FieldDeclaration;

/**
 * If you type:
 * 
 * <pre>
 * <code> 
 * {@literal}@GenDto
 * public class PersonName {
 *   String firstName;
 *   String lastName; 
 * }
 * </code>
 * </pre>
 * 
 * gwt-platform will generate a class call PersonNameDto.
 * <p/>
 * PersonNameDto will have fields, getters, and a constructor that takes
 * firstName and lastName plus equals, hashCode, toString etc,
 * <p/>
 * Notes:
 * <p/>
 * There is no naming requirement for your class name. It will be appended with
 * Dto
 * 
 * <p/>
 * 
 * @author Brendan Doherty (All original code, concept attributed to Stephen
 *         Haberman)
 */

class GenDtoAptProcessor implements AnnotationProcessor {

  private AnnotationProcessorEnvironment env;
  private final AnnotationTypeDeclaration genDtoDecl;

  public GenDtoAptProcessor(AnnotationProcessorEnvironment env) {
    this.env = env;
    this.genDtoDecl = (AnnotationTypeDeclaration) env
        .getTypeDeclaration(GenDto.class.getName());
  }

  @Override
  public void process() {

    for (Declaration decl : env.getDeclarationsAnnotatedWith(genDtoDecl)) {

      ClassDeclaration classDecl = (ClassDeclaration) decl;
      generateDto(classDecl);

    }
  }

  void generateDto(ClassDeclaration classDecl) {
    PrintWriter out = null;
    try {
      out = env.getFiler().createSourceFile(
          classDecl.getQualifiedName() + "Dto");
      AnnotationHelper helper = new AnnotationHelper(out);
      String name = classDecl.getSimpleName();

      SortedMap<Integer, FieldDeclaration> fieldsMap = helper
          .getOrderedFields(classDecl);

      out.println("package " + classDecl.getPackage() + ";");
      out.println();
      /*
       * out.println("import javax.annotation.Generated;"); out.println(); out
       * .println(
       * "@Generated(value = \"com.gwtplatform.annotation.processor.GenDtoAptProcessor\", date = \""
       * + (new Date()).toString() + "\")");
       */
      out.print("public class ");
      out.print(name);
      out.print("Dto {");

      helper.generateFields(fieldsMap.values());

      if (!fieldsMap.isEmpty()) {
        out.println("  protected " + name + "Dto() { }");
        out.println();
      }

      out.print("  public " + name + "Dto(");
      helper.generateFieldList(fieldsMap.values(), true, false);
      out.println(") { ");
      for (FieldDeclaration fieldDecl : fieldsMap.values()) {
        out.println("    this." + fieldDecl.getSimpleName() + " = "
            + fieldDecl.getSimpleName() + ";");
      }
      out.println("  }");
      out.println();

      helper.generateAccessors(fieldsMap.values());

      helper.generateEquals(name + "Dto", fieldsMap.values());

      helper.generateHashCode(fieldsMap.values());

      helper.generateToString(name + "Dto", fieldsMap.values());

      out.println("}");

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        out.close();
      }
    }

  }

}
