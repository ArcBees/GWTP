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

import com.google.gwt.user.client.rpc.IsSerializable;
import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.In;
import com.gwtplatform.dispatch.annotation.Out;
import com.gwtplatform.dispatch.annotation.helper.BuilderGenerationHelper;
import com.gwtplatform.dispatch.annotation.helper.GenerationHelper;
import com.gwtplatform.dispatch.annotation.helper.ReflectionHelper;

/**
 * Processes {@link GenDispatch} annotations.
 * <p>
 * {@link GenDispatchProcessor} should only ever be called by tool infrastructure. See
 * {@link javax.annotation.processing.Processor} for more details.
 */
@SupportedAnnotationTypes("com.gwtplatform.dispatch.annotation.GenDispatch")
public class GenDispatchProcessor extends GenProcessor {
    private static final String RPC_DISPATCH_PACKAGE = "com.gwtplatform.dispatch.rpc.shared";

    @Override
    public void process(Element dispatchElement) {
        GenDispatch genDispatch = dispatchElement.getAnnotation(GenDispatch.class);
        generateAction(dispatchElement,
                genDispatch.isSecure(),
                genDispatch.serviceName(),
                genDispatch.extraActionInterfaces()
        );
        generateResult(dispatchElement, genDispatch.extraResultInterfaces());
    }

    protected void generateAction(Element dispatchElement, boolean isSecure, String serviceName,
            String extraActionInterfaces) {
        BuilderGenerationHelper writer = null;
        try {
            ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) dispatchElement);
            String dispatchElementSimpleName = reflection.getSimpleClassName();
            String dispatchActionSimpleName = dispatchElementSimpleName + "Action";
            String dispatchActionClassName = reflection.getClassName() + "Action";

            printMessage("Generating '" + dispatchActionClassName + "' from '" + dispatchElementSimpleName + "'.");

            @SuppressWarnings("resource")
            Writer sourceWriter = getEnvironment().getFiler().createSourceFile(dispatchActionClassName,
                    dispatchElement).openWriter();
            writer = new BuilderGenerationHelper(sourceWriter);

            Collection<VariableElement> annotatedInFields = reflection.getInFields();
            Collection<VariableElement> allFields = reflection.filterConstantFields(reflection.getInFields());
            Collection<VariableElement> optionalFields = reflection.sortFields(In.class,
                    reflection.getOptionalFields(In.class));
            Collection<VariableElement> requiredFields = reflection.filterConstantFields(reflection.getInFields());
            requiredFields.removeAll(optionalFields);

            writer.generatePackageDeclaration(reflection.getPackageName());
            writer.generateImports(RPC_DISPATCH_PACKAGE + ".Action");

            String actionInterface = "Action<" + dispatchElementSimpleName + "Result>";
            writer.generateClassHeader(dispatchActionSimpleName, null,
                    reflection.getClassRepresenter().getModifiers(),
                    actionInterface, extraActionInterfaces
            );
            writer.generateFieldDeclarations(annotatedInFields);

            if (!optionalFields.isEmpty()) { // has optional fields.
                writer.setWhitespaces(2);
                writer.generateBuilderClass(dispatchActionSimpleName, requiredFields, optionalFields);
                writer.resetWhitespaces();
                writer.generateEmptyConstructor(dispatchActionSimpleName, Modifier.PROTECTED);
                if (!requiredFields.isEmpty()) { // and required fields
                    writer.generateConstructorUsingFields(dispatchActionSimpleName, requiredFields, Modifier.PUBLIC);
                }
                writer.generateCustomBuilderConstructor(dispatchActionSimpleName, allFields);
            } else if (!requiredFields.isEmpty()) { // has only required fields
                writer.generateEmptyConstructor(dispatchActionSimpleName, Modifier.PROTECTED);
                writer.generateConstructorUsingFields(dispatchActionSimpleName, requiredFields, Modifier.PUBLIC);
            } else { // has no non-static fields
                writer.generateEmptyConstructor(dispatchActionSimpleName, Modifier.PUBLIC);
            }

            generateServiceNameAccessor(writer, dispatchElementSimpleName, serviceName);
            generateIsSecuredMethod(writer, isSecure);

            writer.generateFieldAccessors(annotatedInFields);
            writer.generateEquals(dispatchActionSimpleName, annotatedInFields);
            writer.generateHashCode(annotatedInFields);
            writer.generateToString(dispatchActionSimpleName, annotatedInFields);

            writer.generateFooter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    protected void generateResult(Element dispatchElement, String extraResultInterfaces) {
        BuilderGenerationHelper writer = null;
        try {
            ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) dispatchElement);
            String dispatchElementSimpleName = reflection.getSimpleClassName();
            String dispatchResultSimpleName = dispatchElementSimpleName + "Result";
            String dispatchResultClassName = reflection.getClassName() + "Result";

            printMessage("Generating '" + dispatchResultClassName + "' from '" + dispatchElementSimpleName + "'.");

            @SuppressWarnings("resource")
            Writer sourceWriter = getEnvironment().getFiler().createSourceFile(dispatchResultClassName,
                    dispatchElement).openWriter();
            writer = new BuilderGenerationHelper(sourceWriter);

            Collection<VariableElement> annotatedOutFields = reflection.getOutFields();
            Collection<VariableElement> allFields = reflection.filterConstantFields(reflection.getOutFields());
            Collection<VariableElement> optionalFields = reflection.sortFields(Out.class,
                    reflection.getOptionalFields(Out.class));
            Collection<VariableElement> requiredFields = reflection.filterConstantFields(reflection.getOutFields());
            requiredFields.removeAll(optionalFields);

            writer.generatePackageDeclaration(reflection.getPackageName());
            writer.generateImports(
                    reflection.hasOptionalFields() ? IsSerializable.class.getName() : null,
                    null,
                    RPC_DISPATCH_PACKAGE + ".Result"
            );

            String resultInterface = "Result";
            writer.generateClassHeader(dispatchResultSimpleName, null,
                    reflection.getClassRepresenter().getModifiers(),
                    resultInterface, extraResultInterfaces
            );
            writer.generateFieldDeclarations(annotatedOutFields);

            if (!optionalFields.isEmpty()) {
                writer.setWhitespaces(2);
                writer.generateBuilderClass(dispatchResultSimpleName, requiredFields, optionalFields, "IsSerializable");
                writer.resetWhitespaces();
                writer.generateEmptyConstructor(dispatchResultSimpleName, Modifier.PROTECTED);
                if (!requiredFields.isEmpty()) {
                    writer.generateConstructorUsingFields(dispatchResultSimpleName, requiredFields, Modifier.PUBLIC);
                }
                writer.generateCustomBuilderConstructor(dispatchResultSimpleName, allFields);
            } else if (!requiredFields.isEmpty()) {
                writer.generateEmptyConstructor(dispatchResultSimpleName, Modifier.PROTECTED);
                writer.generateConstructorUsingFields(dispatchResultSimpleName, requiredFields, Modifier.PUBLIC);
            } else {
                writer.generateEmptyConstructor(dispatchResultSimpleName, Modifier.PUBLIC);
            }

            writer.generateFieldAccessors(annotatedOutFields);
            writer.generateEquals(dispatchResultSimpleName, annotatedOutFields);
            writer.generateHashCode(annotatedOutFields);
            writer.generateToString(dispatchResultSimpleName, annotatedOutFields);

            writer.generateFooter();
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
            writer.println("    return Action.DEFAULT_SERVICE_NAME + \"{0}\";", simpleClassName);
        } else {
            writer.println("    return \"{0}\";", serviceName);
        }
        writer.println("  }");
    }
}
