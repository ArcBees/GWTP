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
import java.util.ArrayList;
import java.util.Collection;
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
import com.gwtplatform.annotation.Optional;

/**
 * Processes {@link GenEvent} annotations.
 * <p/> 
 * GenEventProcessor should only ever be called by tool infrastructure. 
 * See {@link javax.annotation.processing.Processor} for more details. 
 * 
 * @author Brendan Doherty 
 * @author Florian Sauter
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

  protected void generateEvent(Element eventElement) {
    PrintWriter out = null;
    try {
      AnnotationHelper helper = new AnnotationHelper(env);
     
      String sourceFileName = helper.getQName(eventElement) + "Event";
      Writer sourceWriter = this.env.getFiler().createSourceFile(sourceFileName, eventElement).openWriter();
      BufferedWriter bufferedWriter = new BufferedWriter(sourceWriter);
      out = new PrintWriter(bufferedWriter);
      
      Name eventSimpleName = eventElement.getSimpleName();

      SortedMap<Integer, VariableElement> fieldsMap = helper.getOrderedFields(eventElement);

      generatePackageDeclaration(out, helper.getPackage(eventElement));
      
      generateLineSeparator(out);

      generateImports(out);
      
      generateLineSeparator(out);
      
      generateClassHeader(out, eventSimpleName);
      
      generateLineSeparator(out);

      generateHasHandlerInterface(out, eventSimpleName);
      
      generateLineSeparator(out);

      generateHandlerInterface(out, eventSimpleName);
      
      generateLineSeparator(out);
      
      generateTypeField(out, eventSimpleName);
      
      generateLineSeparator(out);
      
      generateFireMethodsUsingFields(out, helper, eventElement, fieldsMap.values());
      
      generateTypeAccessorMethod(out, eventSimpleName);
      
      generateLineSeparator(out);

      helper.generateFields(out, fieldsMap.values(), true);

      generateConstructorsUsingFields(out, helper, eventElement, fieldsMap.values());
      
      generateAssociatedTypeMethod(out, eventSimpleName);
      
      generateLineSeparator(out);
      
      helper.generateAccessors(out, fieldsMap.values());

      generateDispatchMethod(out, eventSimpleName);
      
      generateLineSeparator(out);   

      helper.generateEquals(out, eventSimpleName + "Event", fieldsMap.values());

      helper.generateHashCode(out, fieldsMap.values());

      helper.generateToString(out, eventSimpleName + "Event", fieldsMap.values());

      generateClassFooter(out);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }
  
  protected void generateLineSeparator(PrintWriter out) {
    out.println();
  }
  
  protected void generatePackageDeclaration(PrintWriter out, String packageName) {
    out.println("package " + packageName + ";");
  }
  
  protected void generateImports(PrintWriter out) {
    out.println("import com.google.gwt.event.shared.EventHandler;");
    out.println("import com.google.gwt.event.shared.GwtEvent;");
    out.println("import com.google.gwt.event.shared.HandlerRegistration;");
    out.println();
    out.println("import com.gwtplatform.mvp.client.HasEventBus;");
    out.println("import com.google.gwt.event.shared.HasHandlers;");
  }
  
  protected void generateClassHeader(PrintWriter out, Name eventSimpleName) {
    out.println("public class " + eventSimpleName + "Event extends GwtEvent<" + eventSimpleName
        + "Event." + eventSimpleName + "Handler> { ");
  }
  
  protected void generateHasHandlerInterface(PrintWriter out, Name eventSimpleName) {
    out.println("  public interface Has" + eventSimpleName
        + "Handlers extends HasHandlers {");
    out.println("    HandlerRegistration add" + eventSimpleName + "Handler(" + eventSimpleName
        + "Handler handler);");
    out.println("  }");
  }
  
  protected void generateHandlerInterface(PrintWriter out, Name eventSimpleName) {
    out.println("  public interface " + eventSimpleName
        + "Handler extends EventHandler {");
    out.println("    public void on" + eventSimpleName + "(" + eventSimpleName + "Event event);");
    out.println("  }");
  }
  
  protected void generateTypeField(PrintWriter out, Name eventSimpleName) {
    out.println("  private static final Type<" + eventSimpleName
        + "Handler> TYPE = new Type<" + eventSimpleName + "Handler>();");
  }
  
  protected void generateFireMethodsUsingFields(PrintWriter out, AnnotationHelper helper, Element eventElement, Collection<VariableElement> fieldElements) {
    Name eventSimpleName = eventElement.getSimpleName();
    
    // generate fire method with all fields
    generateFireMethodUsingFields(out, helper, eventSimpleName, fieldElements);
    
    generateLineSeparator(out);

    Collection<VariableElement> optionalFields = helper.getOptionalFields(eventElement);
    if (!optionalFields.isEmpty()) {
      // generate fire method without optional fields
      ArrayList<VariableElement> fields = new ArrayList<VariableElement>();
      fields.addAll(fieldElements);
      fields.removeAll(optionalFields);
      generateFireMethodUsingFields(out, helper, eventSimpleName, fields);
      generateLineSeparator(out);
    }
  }
  
  protected void generateFireMethodUsingFields(PrintWriter out, AnnotationHelper helper, Name eventSimpleName, Collection<VariableElement> fieldElements) {
    out.print("  public static void fire(HasEventBus source");
    helper.generateFieldList(out, fieldElements, true, true);
    out.println(") {");
    out.print("    source.fireEvent(new " + eventSimpleName + "Event(");
    helper.generateFieldList(out, fieldElements, false, false);
    out.println("));");
    out.println("  }");
  }
  
  protected void generateTypeAccessorMethod(PrintWriter out, Name eventSimpleName) {
    out.println("  public static Type<" + eventSimpleName + "Handler> getType() {");
    out.println("    return TYPE;");
    out.println("  }");
  }
  
  protected void generateConstructorsUsingFields(PrintWriter out, AnnotationHelper helper, Element eventElement, Collection<VariableElement> fieldElements) {
    Name eventSimpleName = eventElement.getSimpleName();
    
    // constructor with all fields
    generateConstructorUsingFields(out, helper, eventSimpleName, fieldElements, null);
    
    generateLineSeparator(out);
    
    Collection<VariableElement> optionalFields = helper.getOptionalFields(eventElement);
    if (!optionalFields.isEmpty()) {
      // constructor without optional fields
      ArrayList<VariableElement> fields = new ArrayList<VariableElement>();
      fields.addAll(fieldElements);
      fields.removeAll(optionalFields);
      generateConstructorUsingFields(out, helper, eventSimpleName, fields, optionalFields);
      generateLineSeparator(out);
    }
  }
  
  /**
   * Optional field elements will not be listed in the constructor field list but initialized with the default value.
   */
  protected void generateConstructorUsingFields(PrintWriter out, AnnotationHelper helper, Name eventSimpleName, Collection<VariableElement> fieldElements, Collection<VariableElement> optionalFieldElements) {
    out.print("  public " + eventSimpleName + "Event(");
    helper.generateFieldList(out, fieldElements, true, false);
    out.println(") {");
    if (optionalFieldElements != null) {
      for (VariableElement optionalField : optionalFieldElements) {
        Optional optionalFieldAnnotation = optionalField.getAnnotation(Optional.class);
        out.println("    this." + optionalField.getSimpleName() + " = " + optionalFieldAnnotation.defaultValue() + ";");
      }
    }
    if (fieldElements != null) {
      for (VariableElement fieldElement : fieldElements) {
        out.println("    this." + fieldElement.getSimpleName() + " = "
            + fieldElement.getSimpleName() + ";");
      }
    }
    out.println("  }");
  }
  
  protected void generateDispatchMethod(PrintWriter out, Name eventSimpleName) {
    out.println("  @Override");
    out.println("  protected void dispatch(" + eventSimpleName + "Handler handler) {");
    out.println("    handler.on" + eventSimpleName + "(this);");
    out.println("  }");
  }

  protected void generateAssociatedTypeMethod(PrintWriter out, Name eventSimpleName) {
    out.println("  @Override");
    out.println("  public Type<" + eventSimpleName + "Handler> getAssociatedType() {");
    out.println("    return TYPE;");
    out.println("  }");
  }
  
  protected void generateClassFooter(PrintWriter out) {
    out.println("}");
  }
  
}
