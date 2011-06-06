/**
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

package com.gwtplatform.mvp.rebind;

import java.lang.annotation.Annotation;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

/**
 * This class is used to inspect the user-provided ginjector. This is typically useful to identify
 * 'get' methods that generate specific types.
 * You must call {@link #init} before any other method can be called.
 *
 * @author Philippe Beaudoin
 */
public class GinjectorInspector {
  private final ClassCollection classCollection;
  private final GeneratorContext generatorContext;
  private final TreeLogger logger;

  private String ginjectorClassName;
  private JClassType ginjectorClass;
  private ClassInspector classInspector;

  GinjectorInspector(ClassCollection classCollection, GeneratorContext generatorContext,
      TreeLogger logger) {
    this.classCollection = classCollection;
    this.generatorContext = generatorContext;
    this.logger = logger;
  }

  /**
   * Access the ginjector class name.
   *
   * @return The ginjector class name.
   */
  public String getGinjectorClassName() {
    return ginjectorClassName;
  }

  /**
   * Access the ginjector class.
   *
   * @return The ginjector class.
   */
  public JClassType getGinjectorClass() {
    return ginjectorClass;
  }

  /**
   * Initializes the ginjector inspector. Finds the ginjector class given the value of the GWT configuration
   * property {@code gin.ginjector}.
   *
   * @throws UnableToCompleteException If the ginjector property or class cannot be found, an error is logged.
   */
  public void init()
      throws UnableToCompleteException {
    findGinjectorClassName(logger, generatorContext.getPropertyOracle());
    findGinjectorClass(logger, generatorContext.getTypeOracle());
    classInspector = new ClassInspector(logger, ginjectorClass);
  }

  /**
   * Looks at the ginjector methods to find one that return the specified
   * type. The get method can be either in the ginjector or any of its parent class.
   *
   * @param returnType The type that should be returned by the get method.
   * @return The get method returning {@code returnType<returnTypeParameter>}, or {@code null} if not found.
   * @throws UnableToCompleteException If more than one matching method is found.
   */
  public String findGetMethod(JClassType returnType)
      throws UnableToCompleteException {
    return methodNameOrNull(classInspector.findMethodWithoutParamsReturning(returnType));
  }

  /**
   * Looks at the ginjector methods to find one that return the specified
   * parameterized type with the specified type as a parameter. The get method can
   * be either in the ginjector or any of its parent class.
   *
   * @param returnType The parameterized type that should be returned by the get method.
   * @param returnTypeParameter The type parameter of {@code returnType}.
   * @return The get method returning {@code returnType<returnTypeParameter>}, or {@code null} if not found.
   * @throws UnableToCompleteException If more than one matching method is found.
   */
  public String findGetMethod(JGenericType returnType,
      JClassType returnTypeParameter) throws UnableToCompleteException {
    return methodNameOrNull(classInspector.findMethodWithoutParamsReturning(
        returnType, returnTypeParameter));
  }

  /**
   * Looks at the ginjector methods to find one that return the specified
   * type and that is annotated with the specified annotation. The get method can
   * be either in the ginjector or any of its parent class.
   *
   * @param returnType The type that should be returned by the get method.
   * @param annotation The annotation that should be present on the get method.
   * @param failIfAnnotationIsFoundOnWrongMethod If {@code true}, the call will throw an {@link UnableToCompleteException}
   *           and log an error if the annotation is found on the wrong method.
   * @return The get method returning {@code returnType} and annotated with {@code annotation}, or {@code null} if not found.
   * @throws UnableToCompleteException If more than one matching method is found.
   */
  public String findAnnotatedGetMethod(JClassType returnType,
      Class<? extends Annotation> annotation,
      boolean failIfAnnotationIsFoundOnWrongMethod) throws UnableToCompleteException {
    return methodNameOrNull(classInspector.findAnnotatedMethodWithoutParamsReturning(
        returnType, annotation, failIfAnnotationIsFoundOnWrongMethod));
  }

  private void findGinjectorClassName(TreeLogger logger, PropertyOracle oracle)
      throws UnableToCompleteException {
    ginjectorClassName = null;
    try {
      ginjectorClassName = oracle.getConfigurationProperty(
          "gin.ginjector").getValues().get(0);
    } catch (BadPropertyValueException e) {
      logger.log(TreeLogger.ERROR,
          "The required configuration property 'gin.ginjector' was not found.",
          e);
      throw new UnableToCompleteException();
    }
  }

  private void findGinjectorClass(TreeLogger logger, TypeOracle oracle)
      throws UnableToCompleteException {
    ginjectorClass = oracle.findType(ginjectorClassName);
    if (ginjectorClass == null
        || !ginjectorClass.isAssignableTo(classCollection.baseGinjectorClass)) {
      logger.log(TreeLogger.ERROR,
          "The configuration property 'gin.ginjector' is '"
              + ginjectorClassName + "' "
              + " which doesn't identify a type inheriting from 'Ginjector'.",
          null);
      throw new UnableToCompleteException();
    }
  }

  private String methodNameOrNull(JMethod method) {
    if (method == null) {
      return null;
    }
    return method.getName();
  }
}
