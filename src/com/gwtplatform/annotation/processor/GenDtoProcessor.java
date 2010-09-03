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

import com.gwtplatform.annotation.GenDto;
import com.gwtplatform.annotation.processor.GenerationHelper.Visibility;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

import static javax.lang.model.SourceVersion.RELEASE_6;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Processes {@link GenDto} annotations.
 * <p/> 
 * {@link GenDtoProcessor} should only ever be called by tool infrastructure. 
 * See {@link javax.annotation.processing.Processor} for more details. 
 * 
 * @author Brendan Doherty 
 * @author Florian Sauter
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
    GenerationHelper writer = null;
    try {
      ReflectionHelper reflection = new ReflectionHelper(env, (TypeElement) dtoElement);
      String dtoElementSimpleName = reflection.getSimpleClassName();
      String dtoSimpleName = dtoElementSimpleName + "Dto";
      String dtoClassName = reflection.getClassName() + "Dto";
      Writer sourceWriter = this.env.getFiler().createSourceFile(dtoClassName, dtoElement).openWriter();
      writer = new GenerationHelper(sourceWriter);

      Collection<VariableElement> orderedElementFields = reflection.getOrderedFields();

      writer.generatePackageDeclaration(reflection.getPackageName());

      writer.generateImports("com.google.gwt.user.client.rpc.IsSerializable");
      
      writer.generateClassHeader(dtoSimpleName, null, "IsSerializable");
      
      writer.generateFields(orderedElementFields, false);

      if (!orderedElementFields.isEmpty() && !reflection.hasOnlyOptionalFields()) {
        writer.generateEmptyConstructor(dtoSimpleName, Visibility.PROTECTED);
      }

      writer.generateConstructorsUsingFields(dtoSimpleName, 
          orderedElementFields,
          reflection.getOptionalFields()
      );

      writer.generateFieldAccessors(orderedElementFields);

      writer.generateEquals(dtoSimpleName, orderedElementFields);

      writer.generateHashCode(orderedElementFields);

      writer.generateToString(dtoSimpleName, orderedElementFields);

      writer.generateClassFooter();

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

}
