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

package com.gwtplatform.mvp.rebind;

import java.lang.annotation.Annotation;
import java.util.List;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * A helper class that can be used to inspect a class for methods having specific signatures.
 *
 * @author Philippe Beaudoin
 */
public class ClassInspector {

  private final TreeLogger logger;
  private final JClassType inspectedClass;

  ClassInspector(TreeLogger logger, JClassType inspectedClass) {
    this.logger = logger;
    this.inspectedClass = inspectedClass;
  }

  /**
   * Inspects the methods to find one that return the specified type. The method can be either in
   * the inspected class or any of its parent classes. It can be static or not.
   *
   * @param returnType The type that should be returned by the method.
   * @return The method returning {@code returnType}, or {@code null} if not found.
   * @throws UnableToCompleteException If more than one matching method is found.
   */
  public JMethod findMethodWithoutParamsReturning(JClassType returnType)
      throws UnableToCompleteException {
    JMethod result = null;
    for (JClassType classType : inspectedClass.getFlattenedSupertypeHierarchy()) {
      for (JMethod method : classType.getMethods()) {
        JClassType actualReturnType = method.getReturnType().isClassOrInterface();
        if (method.getParameters().length == 0
            && actualReturnType != null
            && returnType.isAssignableFrom(actualReturnType)) {
          if (result != null) {
            logger.log(TreeLogger.ERROR, "The class '" + inspectedClass.getName()
                + "' has more than one method returning " + returnType.getName()
                + " and taking no parameter. This is not allowed.", null);
            throw new UnableToCompleteException();
          }
          result = method;
        }
      }
    }
    return result;
  }

  /**
   * Inspects the methods to find one that return the specified parameterized type with the
   * specified type as a parameter. The method can be either in the inspected class or any of its
   * parent classes. It can be static or not.
   *
   * @param returnType The parameterized type that should be returned by the method.
   * @param returnTypeParameter The type parameter of {@code returnType}.
   * @return The method returning {@code returnType<returnTypeParameter>}, or {@code null} if not
   *         found.
   * @throws UnableToCompleteException If more than one matching method is found.
   */
  public JMethod findMethodWithoutParamsReturning(JGenericType returnType,
      JClassType returnTypeParameter) throws UnableToCompleteException {
    JMethod result = null;
    for (JClassType classType : inspectedClass.getFlattenedSupertypeHierarchy()) {
      for (JMethod method : classType.getMethods()) {
        JParameterizedType actualReturnType = method.getReturnType().isParameterized();
        if (method.getParameters().length == 0
            && actualReturnType != null
            && returnType.isAssignableFrom(actualReturnType)
            && returnTypeParameter.isAssignableFrom(actualReturnType.getTypeArgs()[0])) {
          if (result != null) {
            logger.log(TreeLogger.ERROR, "The class '" + inspectedClass.getName()
                + "' has more than one method returning " + returnType.getName()
                + "<" + returnTypeParameter.getName() + "> and taking no parameter. "
                + "This is not allowed.", null);
            throw new UnableToCompleteException();
          }
          result = method;
        }
      }
    }
    return result;
  }

  /**
   * Inspects the methods to find one that return the specified type and is annotated with the
   * specified annotation. The method can be either in the inspected class or any of its parent
   * classes. It can be static or not.
   *
   * @param returnType The type that should be returned by the method.
   * @param annotation The annotation that should be present on the method.
   * @param failIfAnnotationIsFoundOnWrongMethod If {@code true}, the call will throw an
   *        {@link UnableToCompleteException} and log an error if the annotation is found on the
   *        wrong method.
   * @return The method returning {@code returnType} and annotated with {@code annotation}, or
   *         {@code null} if not found.
   * @throws UnableToCompleteException If more than one matching method is found, or if the
   *         annotation is found on another method and {@code failIfAnnotationIsFoundOnWrongMethod}
   *         is {@code true}.
   */
  public JMethod findAnnotatedMethodWithoutParamsReturning(JClassType returnType,
      Class<? extends Annotation> annotation,
      boolean failIfAnnotationIsFoundOnWrongMethod) throws UnableToCompleteException {
    JMethod result = null;
    for (JClassType classType : inspectedClass.getFlattenedSupertypeHierarchy()) {
      for (JMethod method : classType.getMethods()) {
        JClassType actualReturnType = method.getReturnType().isClassOrInterface();
        if (method.getAnnotation(annotation) != null) {
          if (method.getParameters().length == 0
              && actualReturnType != null
              && returnType.isAssignableFrom(actualReturnType)) {
            if (result != null) {
              logger.log(TreeLogger.ERROR, "The class '" + inspectedClass.getName()
                  + "' has more than one method returning " + returnType.getName()
                  + " annotated with '" + annotation.getSimpleName() + "' "
                  + " and taking no parameter. This is not allowed.", null);
              throw new UnableToCompleteException();
            }
            result = method;
          } else if (failIfAnnotationIsFoundOnWrongMethod) {
            logger.log(TreeLogger.ERROR, "The class '" + inspectedClass.getName()
                + "' has method '" + method.getName() + "' annotated with '"
                + annotation.getSimpleName() + "', but the method has the wrong "
                + "signature. It must take 0 parameter and return '"
                + returnType.getName() + "'.", null);
            throw new UnableToCompleteException();
          }
        }
      }
    }
    return result;
  }

  /**
   * Inspects the methods to find one annotated with the specified annotation. The method can be
   * either in the inspected class or any of its parent classes. It can be static or not.
   *
   * @param annotation The annotation that should be present on the method.
   * @return The method annotated with {@code annotation}, or {@code null} if not found.
   * @throws UnableToCompleteException If more than one matching method is found.
   */
  public JMethod findAnnotatedMethod(Class<? extends Annotation> annotation)
      throws UnableToCompleteException {
    JMethod result = null;
    for (JClassType classType : inspectedClass.getFlattenedSupertypeHierarchy()) {
      for (JMethod method : classType.getMethods()) {
        if (method.getAnnotation(annotation) != null) {
          if (result != null) {
            logger.log(TreeLogger.ERROR, "The class '" + inspectedClass.getName()
                + "' has more than one method annotated with '"
                + annotation.getSimpleName() + "'. This is not allowed.", null);
            throw new UnableToCompleteException();
          }
          result = method;
        }
      }
    }
    return result;
  }

  /**
   * Inspects the class for a method with the given name and taking exactly one parameter of the
   * specified type, or of a subtype of that type.
   *
   * @param methodName The name of the method to look for.
   * @param parameterType The type of the unique parameter accepted by that method.
   * @return The method, or {@code null} if not found.
   */
  public JMethod findMethod(String methodName, JClassType parameterType) {
    for (JClassType classType : inspectedClass.getFlattenedSupertypeHierarchy()) {
      for (JMethod method : classType.getMethods()) {
        if (methodName.equals(method.getName())) {
          JType[] parameterTypes = method.getParameterTypes();
          if (parameterTypes.length == 1) {
            JClassType actualParameterType = parameterTypes[0].isClassOrInterface();
            if (actualParameterType != null &&
                parameterType.isAssignableFrom(actualParameterType)) {
              return method;
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * Collects the methods to find one annotated with the specified annotation. The method can be
   * either in the inspected class or any of its parent classes. It can be static or not.
   *
   * @param annotation The annotation that should be present on the methods.
   * @param collection The list in which to collect matching methods.
   */
  public void collectAnnotatedMethods(Class<? extends Annotation> annotation,
      List<JMethod> collection) {
    for (JClassType classType : inspectedClass.getFlattenedSupertypeHierarchy()) {
      for (JMethod method : classType.getMethods()) {
        if (method.getAnnotation(annotation) != null) {
          collection.add(method);
        }
      }
    }
  }

  /**
   * Inspects the fields to collect the ones of the specified parameterized type with the specified
   * type as a parameter annotated with the specified annotation. The field can be either in the
   * inspected class or any of its parent classes. Only static fields are collected.
   *
   * @param type The parameterized type of the desired fields.
   * @param typeParameter The type parameter of {@code type}.
   * @param annotation The annotation that should be present on the field.
   * @param collection The list in which to collect matching fields.
   * @throws UnableToCompleteException If a field annotated with {@code annotation} is found that
   *         does not respect the other criteria of the search.
   */
  public void collectStaticAnnotatedFields(JClassType type,
      JClassType typeParameter, Class<? extends Annotation> annotation,
      List<JField> collection) throws UnableToCompleteException {
    for (JClassType classType : inspectedClass.getFlattenedSupertypeHierarchy()) {
      for (JField field : classType.getFields()) {
        if (field.getAnnotation(annotation) != null) {
          JParameterizedType parameterizedType = field.getType().isParameterized();
          if (!field.isStatic()
              || parameterizedType == null
              || !type.isAssignableFrom(parameterizedType)
              || !typeParameter.isAssignableFrom(parameterizedType.getTypeArgs()[0])) {
            logger.log(
                TreeLogger.ERROR, "Found the annotation @" + annotation.getSimpleName()
                    + " on the invalid field '" + classType.getName() + "." + field.getName()
                    + "'. Field must be static and its type must be " + type.getName()
                    + "<" + typeParameter.getName() + ">.", null);
            throw new UnableToCompleteException();
          }
          collection.add(field);
        }
      }
    }
  }
}