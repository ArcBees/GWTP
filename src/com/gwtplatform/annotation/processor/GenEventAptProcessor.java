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

import com.gwtplatform.annotation.GenEvent;
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
 * {@literal}@GenEvent
 * public class FooChanged {
 *   Foo foo;
 *   boolean originator;
 * }
 * </code>
 * </pre>
 * 
 * gwt-platform will generate two classes, FooChangedEvent and
 * FooChangedHandler.
 * <p/>
 * FooChangedEvent will have fields, getters, and a constructor for foo and
 * originator, plus static getType(), instance dispatch, etc., for it to
 * function correctly as a GwtEvent.
 * <p/>
 * FooChangedHandler will be an interface with a onFooChanged method that takes
 * a FooChangedEvent parameter.
 * 
 * Notes:
 * 
 * There is no naming requirement for your class name. It will be appended with
 * Event and Handler.
 * 
 * @author Brendan Doherty (All original code, concept attributed to Stephen
 *         Haberman)
 */

class GenEventAptProcessor implements AnnotationProcessor {

  private AnnotationProcessorEnvironment env;
  private final AnnotationTypeDeclaration genEventDecl;

  public GenEventAptProcessor(AnnotationProcessorEnvironment env) {
    this.env = env;
    this.genEventDecl = (AnnotationTypeDeclaration) env
        .getTypeDeclaration(GenEvent.class.getName());
  }

  @Override
  public void process() {
    PrintWriter out = null;
    try {

      for (Declaration decl : env.getDeclarationsAnnotatedWith(genEventDecl)) {

        ClassDeclaration classDecl = (ClassDeclaration) decl;

        out = env.getFiler().createSourceFile(
            classDecl.getQualifiedName() + "Event");
        AnnotationHelper helper = new AnnotationHelper(out);
        String name = classDecl.getSimpleName();

        SortedMap<Integer, FieldDeclaration> fieldsMap = helper
            .getOrderedFields(classDecl);

        out.println("package " + classDecl.getPackage() + ";");
        out.println();
        out.println("import com.google.gwt.event.shared.EventHandler;");
        out.println("import com.google.gwt.event.shared.GwtEvent;");
        out.println("import com.google.gwt.event.shared.HandlerRegistration;");
        out.println();
        out.println("import javax.annotation.Generated;");
        out.println("import com.gwtplatform.mvp.client.EventBus;");
        out.println("import com.google.gwt.event.shared.HasHandlers;");
        out.println();
        /*
         * out.println(
         * "@Generated(value = \"com.gwtplatform.annotation.processor.GenEventAptProcessor\", date = \""
         * + (new Date()).toString() + "\")");
         */
        out.println("public class " + name + "Event extends GwtEvent<" + name
            + "Event." + name + "Handler> { ");
        out.println();
        out.println("  public static final Type<" + name
            + "Handler> TYPE = new Type<" + name + "Handler>();");
        out.println();

        helper.generateFields(fieldsMap.values());

        out.print("  public " + name + "Event(");
        helper.generateFieldList(fieldsMap.values(), true, false);
        out.println(") {");
        out.println("  }");
        out.println();
        out.println("  public static Type<" + name + "Handler> getType() {");
        out.println("    return TYPE;");
        out.println("  }");
        out.println();

        out.print("  public static void fire(EventBus eventBus");
        helper.generateFieldList(fieldsMap.values(), true, true);
        out.println(") {");
        out.print("    eventBus.fireEvent(new " + name + "Event(");
        helper.generateFieldList(fieldsMap.values(), false, false);
        out.println("));");
        out.println("  }");
        out.println();

        out.println("  @Override");
        out.println("  public Type<" + name + "Handler> getAssociatedType() {");
        out.println("    return TYPE;");
        out.println("  }");
        out.println();
        out.println("  @Override");
        out.println("  protected void dispatch(" + name + "Handler handler) {");
        out.println("    handler.on" + name + "(this);");
        out.println("  }");
        out.println("");

        helper.generateAccessors(fieldsMap.values());

        helper.generateEquals(name + "Event", fieldsMap.values());

        helper.generateHashCode(fieldsMap.values());

        helper.generateToString(name + "Event", fieldsMap.values());

        out.println("  public static interface " + name
            + "Handler extends EventHandler {");
        out.println("    public void on" + name + "(" + name + "Event event);");
        out.println("  }");
        out.println();
        out.println("  public interface Has" + name
            + "Handlers extends HasHandlers {");
        out.println("    HandlerRegistration add" + name + "Handler(" + name
            + "Handler handler);");
        out.println("  }");

        out.println("}");

      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        out.close();
      }
    }

  }

}
