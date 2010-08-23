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

import com.gwtplatform.annotation.GenEvent;

/**
 * Processes {@link GenEvent} annotations.
 * <p/> 
 * GenEventProcessor should only ever be called by tool infrastructure. 
 * See {@link javax.annotation.processing.Processor} for more details. 
 * 
 * @author Brendan Doherty 
 * @author Stephen Haberman (concept) 
 */

@SupportedAnnotationTypes("com.gwtplatform.annotation.GenEvent")
@SupportedSourceVersion(RELEASE_6)
public class GenEventProcessor extends AbstractProcessor {

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
            GenEvent.class.getName())) {

          for (Element eventElement : roundEnv.getElementsAnnotatedWith(currAnnotation)) {
            this.generateEvent(eventElement);
          }
        }
      }
    }
    return true;
  }

  void generateEvent(Element eventElement) {
    PrintWriter out = null;
    try {
      AnnotationHelper helper = new AnnotationHelper(env);
      out = new PrintWriter(
          new BufferedWriter(
              this.env.getFiler().createSourceFile(
                  helper.getQName(eventElement) + "Event", eventElement).openWriter()));

      Name name = eventElement.getSimpleName();

      SortedMap<Integer, VariableElement> fieldsMap = helper.getOrderedFields(eventElement);

      out.println("package " + helper.getPackage(eventElement) + ";");
      out.println();
      out.println("import com.google.gwt.event.shared.EventHandler;");
      out.println("import com.google.gwt.event.shared.GwtEvent;");
      out.println("import com.google.gwt.event.shared.HandlerRegistration;");
      out.println();
      out.println("import com.gwtplatform.mvp.client.HasEventBus;");
      out.println("import com.google.gwt.event.shared.HasHandlers;");
      out.println();
      out.println("public class " + name + "Event extends GwtEvent<" + name
          + "Event." + name + "Handler> { ");
      out.println();
      out.println("  public interface Has" + name
          + "Handlers extends HasHandlers {");
      out.println("    HandlerRegistration add" + name + "Handler(" + name
          + "Handler handler);");
      out.println("  }");
      out.println();
      out.println("  public interface " + name
          + "Handler extends EventHandler {");
      out.println("    public void on" + name + "(" + name + "Event event);");
      out.println("  }");
      out.println();
      out.println("  private static final Type<" + name
          + "Handler> TYPE = new Type<" + name + "Handler>();");
      out.println();
      out.print("  public static void fire(HasEventBus source");
      helper.generateFieldList(out, fieldsMap.values(), true, true);
      out.println(") {");
      out.print("    source.fireEvent(new " + name + "Event(");
      helper.generateFieldList(out, fieldsMap.values(), false, false);
      out.println("));");
      out.println("  }");
      out.println();
      out.println("  public static Type<" + name + "Handler> getType() {");
      out.println("    return TYPE;");
      out.println("  }");
      out.println();

      helper.generateFields(out, fieldsMap.values(), true);

      out.print("  public " + name + "Event(");
      helper.generateFieldList(out, fieldsMap.values(), true, false);
      out.println(") {");
      for (VariableElement fieldElement : fieldsMap.values()) {
        out.println("    this." + fieldElement.getSimpleName() + " = "
            + fieldElement.getSimpleName() + ";");
      }
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Type<" + name + "Handler> getAssociatedType() {");
      out.println("    return TYPE;");
      out.println("  }");
      out.println();
      helper.generateAccessors(out, fieldsMap.values());

      out.println("  @Override");
      out.println("  protected void dispatch(" + name + "Handler handler) {");
      out.println("    handler.on" + name + "(this);");
      out.println("  }");
      out.println("");

      helper.generateEquals(out, name + "Event", fieldsMap.values());

      helper.generateHashCode(out, fieldsMap.values());

      helper.generateToString(out, name + "Event", fieldsMap.values());

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
