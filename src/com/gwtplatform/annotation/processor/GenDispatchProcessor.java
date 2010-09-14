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

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import javax.annotation.processing.AbstractProcessor;

import com.gwtplatform.annotation.GenDispatch;
import com.gwtplatform.annotation.In;
import com.gwtplatform.annotation.Optional;
import com.gwtplatform.annotation.Out;
import com.gwtplatform.annotation.processor.GenerationHelper.Visibility;

/**
 * Processes {@link GenDispatch} annotations.
 * <p/>
 * {@link GenDispatchProcessor} should only ever be called by tool infrastructure. See
 * {@link javax.annotation.processing.Processor} for more details.
 * 
 * @author Brendan Doherty
 * @author Florian Sauter
 * @author Stephen Haberman (concept)
 */

@SupportedAnnotationTypes("com.gwtplatform.annotation.GenDispatch")
@SupportedSourceVersion(RELEASE_6)
public class GenDispatchProcessor extends AbstractProcessor {

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
            GenDispatch.class.getName())) {

          for (Element dispatch : roundEnv.getElementsAnnotatedWith(currAnnotation)) {
            this.generateGenDispatch(dispatch);
          }
        }
      }
    }
    return true;
  }

  public void generateGenDispatch(Element dispatchElement) {

    GenDispatch genDispatch = dispatchElement.getAnnotation(GenDispatch.class);

    generateAction(dispatchElement, genDispatch.isSecure(),
        genDispatch.serviceName(), genDispatch.extraActionInterfaces());
    generateResult(dispatchElement, genDispatch.extraResultInterfaces());
  }

  @SuppressWarnings("unchecked")
  protected void generateAction(Element dispatchElement, boolean isSecure, String serviceName, String extraActionInterfaces) {
    GenerationHelper writer = null;
    try {
      ReflectionHelper reflection = new ReflectionHelper(env, (TypeElement) dispatchElement);
      String dispatchElementSimpleName = reflection.getSimpleClassName();
      String dispatchActionSimpleName = dispatchElementSimpleName + "Action";
      String dispatchActionClassName = reflection.getClassName() + "Action";
      Writer sourceWriter = this.env.getFiler().createSourceFile(dispatchActionClassName, dispatchElement).openWriter();
      writer = new GenerationHelper(sourceWriter);

      Collection<VariableElement> annotatedInFields = reflection.getInFields();

      writer.generatePackageDeclaration(reflection.getPackageName());

      writer.generateImports(
          "com.gwtplatform.dispatch.shared.Action"
      );

      writer.generateClassHeader(dispatchActionSimpleName, null, 
          "Action<" + dispatchElementSimpleName + "Result>", 
          extraActionInterfaces
      );

      writer.generateFields(annotatedInFields, false);

      if (!annotatedInFields.isEmpty() && !reflection.hasOnlyOptionalFields(In.class)) {
        writer.generateEmptyConstructor(dispatchActionSimpleName, Visibility.PROTECTED);
      }

      writer.generateConstructorsUsingFields(dispatchActionSimpleName, 
          annotatedInFields, 
          reflection.getAnnotatedFields(In.class, Optional.class)
      );

      writer.generateFieldAccessors(annotatedInFields);
      
      generateServiceNameAccessor(writer, dispatchElementSimpleName, serviceName);

      generateIsSecuredMethod(writer, isSecure);

      writer.generateEquals(dispatchActionSimpleName, annotatedInFields);

      writer.generateHashCode(annotatedInFields);

      writer.generateToString(dispatchActionSimpleName, annotatedInFields);

      writer.generateClassFooter();

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected void generateResult(Element dispatchElement, String extraResultInterfaces) {
    GenerationHelper writer = null;
    try {
      ReflectionHelper reflection = new ReflectionHelper(env, (TypeElement) dispatchElement);
      String dispatchElementSimpleName = reflection.getSimpleClassName();
      String dispatchResultSimpleName = dispatchElementSimpleName + "Result";
      String dispatchResultClassName = reflection.getClassName() + "Result";
      Writer sourceWriter = this.env.getFiler().createSourceFile(dispatchResultClassName, dispatchElement).openWriter();
      writer = new GenerationHelper(sourceWriter);

      Collection<VariableElement> annotatedOutFields = reflection.getOutFields();

      writer.generatePackageDeclaration(reflection.getPackageName());
      
      writer.generateImports("com.gwtplatform.dispatch.shared.Result");
      
      writer.generateClassHeader(dispatchResultSimpleName, null, 
          "Result", 
          extraResultInterfaces
      );

      writer.generateFields(annotatedOutFields, false);

      if (!annotatedOutFields.isEmpty() && !reflection.hasOnlyOptionalFields(Out.class)) {
        writer.generateEmptyConstructor(dispatchResultSimpleName, Visibility.PROTECTED);
      }

      writer.generateConstructorsUsingFields(dispatchResultSimpleName, 
          annotatedOutFields, 
          reflection.getAnnotatedFields(Out.class, Optional.class)
      );

      writer.generateFieldAccessors(annotatedOutFields);

      writer.generateEquals(dispatchResultSimpleName, annotatedOutFields);

      writer.generateHashCode(annotatedOutFields);

      writer.generateToString(dispatchResultSimpleName, annotatedOutFields);

      writer.generateClassFooter();

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }
  
  protected void generateIsSecuredMethod(GenerationHelper writer, boolean isSecure) {
    writer.println();
    writer.println("  @Override");
    writer.println("  public boolean isSecured() {");
    writer.println("    return " + isSecure + ";");
    writer.println("  }");
  }
  
  protected void generateServiceNameAccessor(GenerationHelper writer, String simpleClassName, String serviceName) {
    writer.println();
    writer.println("  @Override");
    writer.println("  public String getServiceName() {");
    if (serviceName.isEmpty()) {
      writer.println("    return Action.DEFAULT_SERVICE_NAME + \"" + simpleClassName
          + "\";");
    } else {
      writer.println("    return \"" + serviceName + "\";");
    }
    writer.println("  }");
  }
}
