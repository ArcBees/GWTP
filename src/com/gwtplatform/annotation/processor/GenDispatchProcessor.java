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
import java.util.TreeMap;

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
import com.gwtplatform.annotation.In;
import com.gwtplatform.annotation.Out;

/**
 * Processes {@link GenDispatch} annotations.
 * <p/>
 * GenDispatchProcessor should only ever be called by tool infrastructure. See
 * {@link javax.annotation.processing.Processor} for more details.
 * 
 * @author Brendan Doherty
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
      out = new PrintWriter(
          new BufferedWriter(
              this.env.getFiler().createSourceFile(
                  helper.getQName(dispatchElement) + "Action", dispatchElement).openWriter()));

      Name name = dispatchElement.getSimpleName();

      SortedMap<Integer, VariableElement> fieldsMap = new TreeMap<Integer, VariableElement>();
      for (VariableElement fieldElement : helper.getFields(dispatchElement)) {
        In order = fieldElement.getAnnotation(In.class);
        if (order != null) {
          fieldsMap.put(order.value(), fieldElement);
        }
      }

      out.println("package " + helper.getPackage(dispatchElement) + ";");
      out.println();
      out.println("import com.gwtplatform.dispatch.shared.Action;");
      out.println();
      out.print("public class ");
      out.print(name);
      out.print("Action implements Action<");
      out.print(name);
      out.print("Result>");
      if (!extraActionInterfaces.isEmpty()) {
        out.print(", ");
        out.print(extraActionInterfaces);
      }

      out.println(" { ");

      helper.generateFields(out, fieldsMap.values(), false);

      if (fieldsMap.size() > 0) {
        out.println("  protected " + name + "Action() { }");
        out.println();
      }

      out.print("  public " + name + "Action(");
      helper.generateFieldList(out, fieldsMap.values(), true, false);
      out.println(") { ");
      for (VariableElement fieldElement : fieldsMap.values()) {
        out.println("    this." + fieldElement.getSimpleName() + " = "
            + fieldElement.getSimpleName() + ";");
      }
      out.println("  }");
      out.println();

      helper.generateAccessors(out, fieldsMap.values());

      out.println("  @Override");
      out.println("  public String getServiceName() {");
      if (serviceName.isEmpty()) {
        out.println("    return Action.DEFAULT_SERVICE_NAME + \"" + name
            + "\";");
      } else {
        out.println("    return \"" + serviceName + "\";");
      }

      out.println("  }");
      out.println();

      out.println("  @Override");
      out.println("  public boolean isSecured() {");
      out.println("    return " + isSecure + ";");
      out.println("  }");
      out.println();

      helper.generateEquals(out, name + "Action", fieldsMap.values());

      helper.generateHashCode(out, fieldsMap.values());

      helper.generateToString(out, name + "Action", fieldsMap.values());

      out.println("}");

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
      out = new PrintWriter(
          new BufferedWriter(
              this.env.getFiler().createSourceFile(
                  helper.getQName(dispatchElement) + "Result", dispatchElement).openWriter()));

      Name name = dispatchElement.getSimpleName();

      SortedMap<Integer, VariableElement> fieldsMap = new TreeMap<Integer, VariableElement>();
      for (VariableElement fieldElement : helper.getFields(dispatchElement)) {
        Out order = fieldElement.getAnnotation(Out.class);
        if (order != null) {
          fieldsMap.put(order.value(), fieldElement);
        }
      }

      out.println("package " + helper.getPackage(dispatchElement) + ";");
      out.println();
      out.println("import com.gwtplatform.dispatch.shared.Result;");
      out.println();
      out.print("public class ");
      out.print(name);
      out.print("Result implements Result");
      if (!extraResultInterfaces.isEmpty()) {
        out.print(",");
        out.print(extraResultInterfaces);
      }

      out.print(" { ");
      out.println();

      helper.generateFields(out, fieldsMap.values(), false);

      if (fieldsMap.size() > 0) {
        out.println("  protected " + name + "Result() { }");
        out.println();
      }

      out.print("  public " + name + "Result(");
      helper.generateFieldList(out, fieldsMap.values(), true, false);
      out.println(") { ");
      for (VariableElement fieldElement : fieldsMap.values()) {
        out.println("    this." + fieldElement.getSimpleName() + " = "
            + fieldElement.getSimpleName() + ";");
      }
      out.println("  }");
      out.println();

      helper.generateAccessors(out, fieldsMap.values());

      helper.generateEquals(out, name + "Result", fieldsMap.values());

      helper.generateHashCode(out, fieldsMap.values());

      helper.generateToString(out, name + "Result", fieldsMap.values());

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
