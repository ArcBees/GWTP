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

package com.gwtplatform.dispatch.annotation.processor;

import static javax.lang.model.SourceVersion.RELEASE_6;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.gwtplatform.dispatch.annotation.GenEvent;

/**
 * Processes {@link GenEvent} annotations.
 * <p/>
 * {@link GenEventProcessor} should only ever be called by tool infrastructure.
 * See {@link javax.annotation.processing.Processor} for more details.
 *
 * @author Brendan Doherty
 * @author Florian Sauter
 * @author Stephen Haberman (concept)
 */

@SupportedAnnotationTypes("com.gwtplatform.dispatch.annotation.GenEvent")
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

  protected void generateEvent(Element eventElement) {
    GenerationHelper writer = null;
    try {
      ReflectionHelper reflection = new ReflectionHelper(env, (TypeElement) eventElement);
      String eventElementSimpleName = reflection.getSimpleClassName();
      String eventSimpleName = eventElementSimpleName + "Event";
      String eventClassName = reflection.getClassName() + "Event";
      Writer sourceWriter = this.env.getFiler().createSourceFile(eventClassName, eventElement).openWriter();
      writer = new GenerationHelper(sourceWriter);

      Collection<VariableElement> orderedElementFields = reflection.getOrderedFields();

      writer.generatePackageDeclaration(reflection.getPackageName());

      writer.generateImports(
          "com.google.gwt.event.shared.EventHandler",
          "com.google.gwt.event.shared.GwtEvent",
          "com.google.gwt.event.shared.HandlerRegistration",
          null,
          "com.google.gwt.event.shared.HasHandlers"
      );

      writer.generateClassHeader(eventSimpleName, "GwtEvent<" + eventSimpleName + "." + eventElementSimpleName + "Handler>");

      generateHasHandlerInterface(writer, eventElementSimpleName);

      generateHandlerInterface(writer, eventElementSimpleName);

      generateStaticTypeField(writer, eventElementSimpleName);

      generateFireMethodsUsingFields(writer, eventSimpleName, reflection);

      generateTypeAccessorMethod(writer, eventElementSimpleName);

      writer.generateFieldDeclarations(orderedElementFields);

      Collection<VariableElement> allFields = reflection.getNonConstantFields();
      Collection<VariableElement> optionalFields = reflection.getOptionalFields();
      Collection<VariableElement> requiredFields = reflection.getNonConstantFields();
      requiredFields.removeAll(optionalFields);

      writer.generateConstructorUsingFields(eventSimpleName, allFields);

      if (optionalFields.size() > 0) {
        writer.generateConstructorUsingFields(eventSimpleName, requiredFields);
      }

      if (!allFields.isEmpty() && requiredFields.size() > 0) {
        writer.generateEmptyConstructor(eventSimpleName, Modifier.PROTECTED);
      }

      generateAssociatedTypeMethod(writer, eventElementSimpleName);

      writer.generateFieldAccessors(orderedElementFields);

      generateDispatchMethod(writer, eventElementSimpleName);

      writer.generateEquals(eventSimpleName, orderedElementFields);

      writer.generateHashCode(orderedElementFields);

      writer.generateToString(eventSimpleName, orderedElementFields);

      writer.generateClassFooter();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  protected void generateAssociatedTypeMethod(GenerationHelper writer, String eventSimpleName) {
    writer.println();
    writer.println("  @Override");
    writer.println("  public Type<" + eventSimpleName + "Handler> getAssociatedType() {");
    writer.println("    return TYPE;");
    writer.println("  }");
  }

  protected void generateDispatchMethod(GenerationHelper writer, String eventSimpleName) {
    writer.println();
    writer.println("  @Override");
    writer.println("  protected void dispatch(" + eventSimpleName + "Handler handler) {");
    writer.println("    handler.on" + eventSimpleName + "(this);");
    writer.println("  }");
  }

  protected void generateFireMethodsUsingFields(GenerationHelper writer, String simpleClassName, ReflectionHelper reflection) {
    Collection<VariableElement> fields = reflection.getNonConstantFields();
    // generate fire method with all fields
    generateFireMethodUsingFields(writer, simpleClassName, reflection, fields);

    Collection<VariableElement> optionalFields = reflection.getOptionalFields();
    if (!optionalFields.isEmpty()) {
      // generate fire method without optional fields
      ArrayList<VariableElement> fieldElements = new ArrayList<VariableElement>();
      fieldElements.addAll(fields);
      fieldElements.removeAll(optionalFields);
      generateFireMethodUsingFields(writer, simpleClassName, reflection, fieldElements);
    }
  }

  protected void generateFireMethodUsingFields(GenerationHelper writer, String simpleClassName, ReflectionHelper reflection, Collection<VariableElement> fieldElements) {
    writer.println();
    writer.print("  public static void fire(HasHandlers source");
    writer.generateFieldList(fieldElements, true, true);
    writer.println(") {");
    writer.print("    source.fireEvent(new " + simpleClassName + "(");
    writer.generateFieldList(fieldElements, false, false);
    writer.println("));");
    writer.println("  }");
  }

  protected void generateHasHandlerInterface(GenerationHelper out, String eventSimpleName) {
    out.println();
    out.println("  public interface Has" + eventSimpleName + "Handlers extends HasHandlers {");
    out.println("    HandlerRegistration add" + eventSimpleName + "Handler(" + eventSimpleName + "Handler handler);");
    out.println("  }");
  }

  protected void generateHandlerInterface(GenerationHelper out, String eventSimpleName) {
    out.println();
    out.println("  public interface " + eventSimpleName + "Handler extends EventHandler {");
    out.println("    public void on" + eventSimpleName + "(" + eventSimpleName + "Event event);");
    out.println("  }");
  }

  protected void generateStaticTypeField(GenerationHelper out, String eventSimpleName) {
    out.println();
    out.println("  private static final Type<" + eventSimpleName + "Handler> TYPE = new Type<" + eventSimpleName + "Handler>();");
  }

  protected void generateTypeAccessorMethod(GenerationHelper out, String eventSimpleName) {
    out.println();
    out.println("  public static Type<" + eventSimpleName + "Handler> getType() {");
    out.println("    return TYPE;");
    out.println("  }");
  }
}
