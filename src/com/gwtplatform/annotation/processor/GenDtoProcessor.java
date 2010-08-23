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

import static javax.lang.model.SourceVersion.RELEASE_6;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.SortedMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.gwtplatform.annotation.GenDto;

/**
 * Processes {@link GenDto} annotations.
 * <p/> 
 * GenDtoProcessor should only ever be called by tool infrastructure. 
 * See {@link javax.annotation.processing.Processor} for more details. 
 * 
 * @author Brendan Doherty 
 * @author Stephen Haberman (concept) 
 */

@SupportedAnnotationTypes("com.gwtplatform.annotation.GenDto")
@SupportedSourceVersion(RELEASE_6)
public class GenDtoProcessor extends AbstractProcessor {

  private ProcessingEnvironment env;

  @Override
  public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    this.env = env;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {

    if (!roundEnv.processingOver()) {

      for (TypeElement currAnnotation : annotations) {

        if (currAnnotation.getQualifiedName().contentEquals(
            GenDto.class.getName())) {

          for (Element dto : roundEnv.getElementsAnnotatedWith(currAnnotation)) {
            this.generateDto(dto);
          }
        }
      }
    }
    return true;
  }

  void generateDto(Element dtoElement) {
    PrintWriter out = null;
    try {
      AnnotationHelper helper = new AnnotationHelper(env);
      out = new PrintWriter(new BufferedWriter(
          this.env.getFiler().createSourceFile(
              helper.getQName(dtoElement) + "Dto", dtoElement).openWriter()));

      Name name = dtoElement.getSimpleName();

      SortedMap<Integer, VariableElement> fieldsMap = helper.getOrderedFields(dtoElement);

      out.println("package " + helper.getPackage(dtoElement) + ";");
      out.println();
      out.print("public class ");
      out.print(name);
      out.print("Dto {");

      helper.generateFields(out, fieldsMap.values(), false);

      if (!fieldsMap.isEmpty()) {
        out.println("  protected " + name + "Dto() { }");
        out.println();
      }

      out.print("  public " + name + "Dto(");
      helper.generateFieldList(out, fieldsMap.values(), true, false);
      out.println(") { ");
      for (VariableElement fieldDecl : fieldsMap.values()) {
        out.println("    this." + fieldDecl.getSimpleName() + " = "
            + fieldDecl.getSimpleName() + ";");
      }
      out.println("  }");
      out.println();

      helper.generateAccessors(out, fieldsMap.values());

      helper.generateEquals(out, name + "Dto", fieldsMap.values());

      helper.generateHashCode(out, fieldsMap.values());

      helper.generateToString(out, name + "Dto", fieldsMap.values());

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
