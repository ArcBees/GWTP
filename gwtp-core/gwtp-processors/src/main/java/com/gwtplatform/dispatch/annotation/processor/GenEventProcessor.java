/*
 * Copyright 2011 ArcBees Inc.
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

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.gwtplatform.dispatch.annotation.helper.BuilderGenerationHelper;
import com.gwtplatform.dispatch.annotation.helper.GenerationHelper;
import com.gwtplatform.dispatch.annotation.helper.ReflectionHelper;

/**
 * Processes {@link GenEvent} annotations.
 * <p>
 * {@link GenEventProcessor} should only ever be called by tool infrastructure.
 * See {@link javax.annotation.processing.Processor} for more details.
 */
@SupportedAnnotationTypes("com.gwtplatform.dispatch.annotation.GenEvent")
public class GenEventProcessor extends GenProcessor {

    @Override
    public void process(Element eventElement) {
        BuilderGenerationHelper writer = null;
        try {
            ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) eventElement);
            String eventElementSimpleName = reflection.getSimpleClassName();
            String eventSimpleName = eventElementSimpleName + "Event";
            String eventClassName = reflection.getClassName() + "Event";

            @SuppressWarnings("resource")
            Writer sourceWriter = getEnvironment().getFiler().createSourceFile(eventClassName,
                    eventElement).openWriter();
            writer = new BuilderGenerationHelper(sourceWriter);

            Collection<VariableElement> orderedElementFields = reflection.getOrderedFields();
            Collection<VariableElement> allFields = reflection.getNonConstantFields();
            Collection<VariableElement> optionalFields = reflection.getOptionalFields();
            Collection<VariableElement> requiredFields = reflection.getNonConstantFields();
            requiredFields.removeAll(optionalFields);

            writer.generatePackageDeclaration(reflection.getPackageName());
            writer.generateImports(
                    "com.google.gwt.event.shared.EventHandler",
                    "com.google.gwt.event.shared.GwtEvent",
                    "com.google.web.bindery.event.shared.HandlerRegistration",
                    null,
                    "com.google.gwt.event.shared.HasHandlers"
            );

            writer.generateClassHeader(eventSimpleName,
                    "GwtEvent<" + eventSimpleName + "." + eventElementSimpleName + "Handler>",
                    reflection.getClassRepresenter().getModifiers()
            );
            writer.generateFieldDeclarations(orderedElementFields);

            if (!optionalFields.isEmpty()) { // has optional fields.
                writer.setWhitespaces(2);
                writer.generateBuilderClass(eventSimpleName, requiredFields, optionalFields);
                writer.resetWhitespaces();
                if (!requiredFields.isEmpty()) { // and required fields
                    writer.generateConstructorUsingFields(eventSimpleName, requiredFields, Modifier.PUBLIC);
                }
                writer.generateCustomBuilderConstructor(eventSimpleName, allFields);
                generateFireSelfMethod(writer);
            } else if (!requiredFields.isEmpty()) {  // has only required fields
                writer.generateEmptyConstructor(eventSimpleName, Modifier.PROTECTED);
                writer.generateConstructorUsingFields(eventSimpleName, requiredFields, Modifier.PUBLIC);
                generateFireFieldsStaticMethod(writer, requiredFields, eventSimpleName);
            } else { // has no non-static fields
                writer.generateEmptyConstructor(eventSimpleName, Modifier.PUBLIC);
                generateFireFieldsStaticMethod(writer, requiredFields, eventSimpleName);
            }

            generateFireInstanceStaticMethod(writer, eventSimpleName);
            generateHasHandlerInterface(writer, eventElementSimpleName);
            generateHandlerInterface(writer, eventElementSimpleName);
            generateStaticTypeField(writer, eventElementSimpleName);
            generateTypeAccessorMethod(writer, eventElementSimpleName);
            generateAssociatedTypeMethod(writer, eventElementSimpleName);
            generateDispatchMethod(writer, eventElementSimpleName);

            writer.generateFieldAccessors(orderedElementFields);
            writer.generateEquals(eventSimpleName, orderedElementFields);
            writer.generateHashCode(orderedElementFields);
            writer.generateToString(eventSimpleName, orderedElementFields);

            writer.generateFooter();
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
        writer.println("  public Type<{0}Handler> getAssociatedType() {", eventSimpleName);
        writer.println("    return TYPE;");
        writer.println("  }");
    }

    protected void generateDispatchMethod(GenerationHelper writer, String eventSimpleName) {
        writer.println();
        writer.println("  @Override");
        writer.println("  protected void dispatch({0}Handler handler) {", eventSimpleName);
        writer.println("    handler.on{0}(this);", eventSimpleName);
        writer.println("  }");
    }

    protected void generateHasHandlerInterface(GenerationHelper writer, String eventSimpleName) {
        writer.println();
        writer.println("  public interface Has{0}Handlers extends HasHandlers {", eventSimpleName);
        writer.println("    HandlerRegistration add{0}Handler({0}Handler handler);", eventSimpleName);
        writer.println("  }");
    }

    protected void generateHandlerInterface(GenerationHelper writer, String eventSimpleName) {
        writer.println();
        writer.println("  public interface {0}Handler extends EventHandler {", eventSimpleName);
        writer.println("    public void on{0}({0}Event event);", eventSimpleName);
        writer.println("  }");
    }

    protected void generateFireInstanceStaticMethod(GenerationHelper writer, String simpleClassName) {
        writer.println();
        writer.println("  public static void fire(HasHandlers source, {0} eventInstance) {", simpleClassName);
        writer.println("    source.fireEvent(eventInstance);");
        writer.println("  }");
    }

    protected void generateFireFieldsStaticMethod(GenerationHelper writer,
            Collection<VariableElement> requiredFields, String simpleClassName) {
        String fields = writer.generateFieldList(requiredFields, false, false);
        String fieldsWithTypes = writer.generateFieldList(requiredFields, true, requiredFields.size() > 0);
        writer.println();
        writer.println("  public static void fire(HasHandlers source{0}) {", fieldsWithTypes);
        writer.println("    {0} eventInstance = new {0}({1});", simpleClassName, fields);
        writer.println("    source.fireEvent(eventInstance);");
        writer.println("  }");
    }

    protected void generateFireSelfMethod(GenerationHelper writer) {
        writer.println();
        writer.println("  public void fire(HasHandlers source) {");
        writer.println("    source.fireEvent(this);");
        writer.println("  }");
    }

    protected void generateStaticTypeField(GenerationHelper out, String eventSimpleName) {
        out.println();
        out.println("  private static final Type<{0}Handler> TYPE = new Type<{0}Handler>();", eventSimpleName);
    }

    protected void generateTypeAccessorMethod(GenerationHelper out, String eventSimpleName) {
        out.println();
        out.println("  public static Type<{0}Handler> getType() {", eventSimpleName);
        out.println("    return TYPE;");
        out.println("  }");
    }
}
