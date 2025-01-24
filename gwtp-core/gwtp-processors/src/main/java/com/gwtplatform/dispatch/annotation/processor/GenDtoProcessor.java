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
import com.gwtplatform.dispatch.annotation.helper.ReflectionHelper;

/**
 * Processes {@link GenDto} annotations.
 * <p>
 * {@link GenDtoProcessor} should only ever be called by tool infrastructure.
 * See {@link javax.annotation.processing.Processor} for more details.
 */
@SupportedAnnotationTypes("com.gwtplatform.dispatch.annotation.GenDto")
public class GenDtoProcessor extends GenProcessor {

    @Override
    public void process(Element dtoElement) {
        BuilderGenerationHelper writer = null;
        try {
            ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) dtoElement);
            String dtoElementSimpleName = reflection.getSimpleClassName();
            String dtoSimpleName = dtoElementSimpleName + "Dto";
            String dtoClassName = reflection.getClassName() + "Dto";

            printMessage("Generating '" + dtoClassName + "' from '" + dtoElementSimpleName + "'.");

            @SuppressWarnings("resource")
            Writer sourceWriter = getEnvironment().getFiler().createSourceFile(dtoClassName, dtoElement).openWriter();
            writer = new BuilderGenerationHelper(sourceWriter);

            Collection<VariableElement> orderedElementFields = reflection.getOrderedFields();
            Collection<VariableElement> allFields = reflection.getNonConstantFields();
            Collection<VariableElement> optionalFields = reflection.getOptionalFields();
            Collection<VariableElement> requiredFields = reflection.getNonConstantFields();
            requiredFields.removeAll(optionalFields);

            writer.generatePackageDeclaration(reflection.getPackageName());
            writer.generateImports("com.google.gwt.user.client.rpc.IsSerializable");
            writer.generateClassHeader(dtoSimpleName, null, reflection.getClassRepresenter().getModifiers(),
                    "IsSerializable");
            writer.generateFieldDeclarations(orderedElementFields);

            if (!optionalFields.isEmpty()) { // has optional fields.
                writer.setWhitespaces(2);
                writer.generateBuilderClass(dtoSimpleName, requiredFields, optionalFields);
                writer.resetWhitespaces();
                writer.generateEmptyConstructor(dtoSimpleName, Modifier.PROTECTED);
                if (!requiredFields.isEmpty()) { // and required fields
                    writer.generateConstructorUsingFields(dtoSimpleName, requiredFields, Modifier.PUBLIC);
                }
                writer.generateCustomBuilderConstructor(dtoSimpleName, allFields);
            } else if (!requiredFields.isEmpty()) { // has only required fields
                writer.generateEmptyConstructor(dtoSimpleName, Modifier.PROTECTED);
                writer.generateConstructorUsingFields(dtoSimpleName, requiredFields, Modifier.PUBLIC);
            } else { // has no non-static fields
                writer.generateEmptyConstructor(dtoSimpleName, Modifier.PUBLIC);
            }

            writer.generateFieldAccessors(orderedElementFields);
            writer.generateEquals(dtoSimpleName, orderedElementFields);
            writer.generateHashCode(orderedElementFields);
            writer.generateToString(dtoSimpleName, orderedElementFields);

            writer.generateFooter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
