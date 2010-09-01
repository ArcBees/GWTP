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
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import javax.annotation.processing.AbstractProcessor;

import com.gwtplatform.annotation.GenDispatch;

/**
 * Processes {@link GenDispatch} annotations.
 * <p/>
 * GenDispatchProcessor should only ever be called by tool infrastructure. See
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

  void generateAction(Element dispatchElement, boolean isSecure,
      String serviceName, String extraActionInterfaces) {

    PrintWriter out = null;
    try {
      AnnotationHelper helper = new AnnotationHelper(env);
      
      String sourceFileName = helper.getQName(dispatchElement) + "Action";
      Writer sourceWriter = this.env.getFiler().createSourceFile(sourceFileName, dispatchElement).openWriter();
      BufferedWriter bufferedWriter = new BufferedWriter(sourceWriter);
      out = new PrintWriter(bufferedWriter);
      
      Name dispatchSimpleName = dispatchElement.getSimpleName();
      String dispatchActionClassName = dispatchSimpleName + "Action";
      
      Collection<VariableElement> annotatedInFields = helper.getInFields(dispatchElement);

      helper.generatePackageDeclaration(out, helper.getPackage(dispatchElement));

      helper.generateImports(out, "com.gwtplatform.dispatch.shared.Action");

      helper.generateClassHeader(out, 
          dispatchActionClassName, null, 
          "Action<" + dispatchSimpleName + "Result>", 
          extraActionInterfaces
      );

      helper.generateFields(out, annotatedInFields, false);

      if (annotatedInFields.isEmpty() && !helper.hasOnlyOptionalFields(annotatedInFields)) {
        helper.generateEmptyConstructor(out, "protected", dispatchActionClassName);
      }

      helper.generateConstructorsUsingFields(out, dispatchActionClassName, dispatchElement, annotatedInFields);

      helper.generateFieldAccessors(out, annotatedInFields);
      
      generateServiceNameAccessor(out, dispatchSimpleName, serviceName);

      generateIsSecuredMethod(out, isSecure);

      helper.generateEquals(out, dispatchActionClassName, annotatedInFields);

      helper.generateHashCode(out, annotatedInFields);

      helper.generateToString(out, dispatchActionClassName, annotatedInFields);

      helper.generateClassFooter(out);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

  void generateResult(Element dispatchElement, String extraResultInterfaces) {

    PrintWriter out = null;
    try {
      AnnotationHelper helper = new AnnotationHelper(env);
      
      String sourceFileName = helper.getQName(dispatchElement) + "Result";
      Writer sourceWriter = this.env.getFiler().createSourceFile(sourceFileName, dispatchElement).openWriter();
      BufferedWriter bufferedWriter = new BufferedWriter(sourceWriter);
      out = new PrintWriter(bufferedWriter);
      
      Name dispatchSimpleName = dispatchElement.getSimpleName();
      String dispatchResultClassName = dispatchSimpleName + "Result";

      Collection<VariableElement> annotatedOutFields = helper.getOutFields(dispatchElement);

      helper.generatePackageDeclaration(out, helper.getPackage(dispatchElement));
      
      helper.generateImports(out, "com.gwtplatform.dispatch.shared.Result");
      
      helper.generateClassHeader(out, 
          dispatchResultClassName, null, 
          "Result", 
          extraResultInterfaces
      );

      helper.generateFields(out, annotatedOutFields, false);

      if (!annotatedOutFields.isEmpty() && !helper.hasOnlyOptionalFields(annotatedOutFields)) {
        helper.generateEmptyConstructor(out, "protected", dispatchResultClassName);
      }

      helper.generateConstructorsUsingFields(out, dispatchResultClassName, dispatchElement, annotatedOutFields);

      helper.generateFieldAccessors(out, annotatedOutFields);

      helper.generateEquals(out, dispatchResultClassName, annotatedOutFields);

      helper.generateHashCode(out, annotatedOutFields);

      helper.generateToString(out, dispatchResultClassName, annotatedOutFields);

      helper.generateClassFooter(out);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }
  
  protected void generateIsSecuredMethod(PrintWriter out, boolean isSecure) {
    out.println();
    out.println("  @Override");
    out.println("  public boolean isSecured() {");
    out.println("    return " + isSecure + ";");
    out.println("  }");
  }
  
  protected void generateServiceNameAccessor(PrintWriter out, Name simpleClassName, String serviceName) {
    out.println();
    out.println("  @Override");
    out.println("  public String getServiceName() {");
    if (serviceName.isEmpty()) {
      out.println("    return Action.DEFAULT_SERVICE_NAME + \"" + simpleClassName
          + "\";");
    } else {
      out.println("    return \"" + serviceName + "\";");
    }
    out.println("  }");
  }
}
