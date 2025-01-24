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

package com.gwtplatform.dispatch.annotation.helper;

import java.io.Writer;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * <p>You should consider to use a builder when you are faced with many constructor
 * parameters. Specially mandatory and optional parameters. Here comes the
 * {@link BuilderGenerationHelper} into play.</p>
 * <p>
 * Instead of making the desired object directly, the client calls a constructor
 * (or static factory) with all of the required parameters and gets a builder
 * object. Then the client calls setter-like methods on the builder object to
 * set each optional parameter of interest. Finally, the client calls a
 * parameterless build method to generate the object, which is immutable. The
 * builder is a static member class of the class it builds. This class offers a
 * method to generate a builder like this.
 * <p>
 * Rules:
 * No optional fields - no Builder.
 * Has required fields and optional fields - Constructor for required fields + Builder.
 * Has optional fields - only Builder.
 *
 * @see http://my.safaribooksonline.com/9780137150021/ch02lev1sec2
 */
public class BuilderGenerationHelper extends ClassGenerationHelper {

    private static final String SIMPLE_CLASS_NAME = "Builder";

    public BuilderGenerationHelper(Writer sourceWriter) {
        super(sourceWriter);
    }

    public void generateBuilderClass(String builderObjectSimpleClassName,
            Collection<VariableElement> requiredFields,
            Collection<VariableElement> optionalFields,
            String... interfaces) {
        Set<Modifier> builderModifiers = new TreeSet<Modifier>();
        builderModifiers.add(Modifier.PUBLIC);
        builderModifiers.add(Modifier.STATIC);
        generateClassHeader(SIMPLE_CLASS_NAME, null, builderModifiers, interfaces);
        println();
        println("  // Required parameters");
        for (VariableElement requiredField : requiredFields) {
            generateFieldDeclaration(requiredField, Modifier.PRIVATE, Modifier.FINAL);
        }
        println();
        println("  // Optional parameters - initialized to default values");
        for (VariableElement optionalField : optionalFields) {
            generateFieldDeclaration(optionalField, Modifier.PRIVATE);
        }
        generateConstructorUsingFields(SIMPLE_CLASS_NAME, requiredFields, Modifier.PUBLIC);
        for (VariableElement optionalField : optionalFields) {
            generateBuilderInvocationMethod(optionalField);
        }
        generateBuilderBuildMethod(builderObjectSimpleClassName);
        generateFooter();
    }

    /**
     * Creates a private object constructor which should only ever be called by the intern Builder class.
     *
     * @param customClassName             the object class name
     * @param fieldsToBePassedAndAssigned the fields
     */
    public void generateCustomBuilderConstructor(String customClassName,
            Collection<VariableElement> fieldsToBePassedAndAssigned) {
        println();
        println("  private {0}({1} builder) {", customClassName, SIMPLE_CLASS_NAME);
        if (fieldsToBePassedAndAssigned != null) {
            for (VariableElement fieldToBeAssigned : fieldsToBePassedAndAssigned) {
                generateBuilderFieldAssignment(fieldToBeAssigned, fieldToBeAssigned.getSimpleName().toString());
            }
        }
        println("  }");
    }

    /**
     * Helper method to create the Builder#build() method which returns a concrete object instance.
     *
     * @param builderObjectSimpleClassName the object class name
     */
    protected void generateBuilderBuildMethod(String builderObjectSimpleClassName) {
        println();
        println("  public {0} build() {", builderObjectSimpleClassName);
        println("    return new {0}(this);", builderObjectSimpleClassName);
        println("  }");
    }

    /**
     * @param fieldElement
     * @param value
     */
    protected void generateBuilderFieldAssignment(VariableElement fieldElement, Object value) {
        println("    this.{0} = builder.{0};", fieldElement.getSimpleName());
    }

    protected void generateBuilderHeader() {
        println("public static class {0} {", SIMPLE_CLASS_NAME);
    }

    protected void generateBuilderInvocationMethod(VariableElement fieldElement) {
        println();
        println("  public {0} {1}({2}) {",
                SIMPLE_CLASS_NAME,
                fieldElement.getSimpleName(),
                manufactureField(fieldElement)
        );
        generateFieldAssignment(fieldElement, fieldElement.getSimpleName());
        println("    return this;");
        println("  }");
    }

}
