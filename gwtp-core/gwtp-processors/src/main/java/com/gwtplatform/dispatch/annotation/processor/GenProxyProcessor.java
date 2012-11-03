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

package com.gwtplatform.dispatch.annotation.processor;

import static javax.lang.model.SourceVersion.RELEASE_6;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import com.google.web.bindery.requestfactory.shared.Locator;
import com.gwtplatform.dispatch.annotation.GenProxy;
import com.gwtplatform.dispatch.annotation.UseProxy;
import com.gwtplatform.dispatch.annotation.UseProxyName;
import com.gwtplatform.dispatch.annotation.helper.InterfaceGenerationHelper;
import com.gwtplatform.dispatch.annotation.helper.ReflectionHelper;

/**
 * Processes {@link GenProxy} annotations.
 * <p/>
 * {@link GenProxyProcessor} should only ever be called by tool infrastructure.
 * See {@link javax.annotation.processing.Processor} for more details.
 *
 * @author Florian Sauter
 */
@SupportedSourceVersion(RELEASE_6)
@SupportedAnnotationTypes("com.gwtplatform.dispatch.annotation.GenProxy")
public class GenProxyProcessor extends GenProcessor {

  @Override
  public void process(final Element proxyElement) {
    GenProxy genProxy = proxyElement.getAnnotation(GenProxy.class);
    generateProxy(
        proxyElement,
        genProxy.targetPackage(),
        genProxy.filterSetter(),
        genProxy.filterGetter(),
        genProxy.isEmbeddedType(),
        getLocatorTypeMirror(genProxy)
    );
  }

  protected void generateProxy(Element proxyElement, String targetPackage, String[] filterSetter, String[] filterGetter, boolean isEmbeddedType, TypeMirror locatorType) {
    InterfaceGenerationHelper writer = null;
    try {
      ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) proxyElement);
      String proxyElementSimpleName = reflection.getSimpleClassName();
      String proxyElementClassName = reflection.getClassName();
      String proxyElementPackage = reflection.getPackageName();

      String preparedProxyElementClassName = proxyElementClassName;
      String preparedProxyElementPackage = proxyElementPackage;

      if (targetPackage != null && !targetPackage.isEmpty()) {
        // Prepare user defined proxy target package.
        preparedProxyElementClassName = targetPackage + "." + proxyElementSimpleName;
        preparedProxyElementPackage = targetPackage;
      } else {
        // Magic: By default proxies should be available for client and server side.
        preparedProxyElementClassName = proxyElementClassName.replace(".server", ".shared");
        preparedProxyElementPackage = proxyElementPackage.replace(".server", ".shared");
      }

      String proxySimpleName = proxyElementSimpleName + "Proxy";
      String proxyClassName = preparedProxyElementClassName + "Proxy";

      Writer sourceWriter = getEnvironment().getFiler().createSourceFile(proxyClassName, proxyElement).openWriter();
      writer = new InterfaceGenerationHelper(sourceWriter);

      Collection<VariableElement> allFields = reflection.getNonConstantFields();
      Collection<VariableElement> setterFields = reflection.filterFields(allFields, filterSetter);
      Collection<VariableElement> getterFields = reflection.filterFields(allFields, filterGetter);

      writer.generatePackageDeclaration(preparedProxyElementPackage);

      String proxyForClassSimpleName = "ProxyFor";

      if (isEmbeddedType) {
        // Process ValueProxy.
        writer.generateImports(
            "com.google.web.bindery.requestfactory.shared." + proxyForClassSimpleName,
            "com.google.web.bindery.requestfactory.shared.ValueProxy"
        );
        writer.println();
        writer.generateAnnotation(proxyForClassSimpleName, proxyElementClassName + ".class");
        writer.generateInterfaceHeader(proxySimpleName, reflection.getClassRepresenter().getModifiers(),
            "ValueProxy"
        );
      } else {
          // Process EntityProxy.
          writer.generateImports(
              "com.google.web.bindery.requestfactory.shared.ProxyFor",
              "com.google.web.bindery.requestfactory.shared.EntityProxy",
              "com.google.web.bindery.requestfactory.shared.EntityProxyId"
          );
          writer.println();
          // Check for locator.
          if (locatorType == null || Locator.class.getName().equals(locatorType.toString())) {
            writer.generateAnnotation(proxyForClassSimpleName, proxyElementClassName + ".class");
          } else {
            writer.println("@{0}(value = {1}.class, locator = {2}.class)", proxyForClassSimpleName, proxyElementClassName, locatorType.toString());
          }
          writer.generateInterfaceHeader(proxySimpleName, reflection.getClassRepresenter().getModifiers(),
              "EntityProxy"
          );
          writer.println();
          writer.generateEmptyMethodBody("stableId", "EntityProxyId<" + proxySimpleName + ">");
      }

      writer.println();

      // Generate getters.
      for (VariableElement getterField : getterFields) {
        // Check for embedded types.
        UseProxy useProxyAnnotation = getterField.getAnnotation(UseProxy.class);
        UseProxyName useProxyNameAnnotation = getterField.getAnnotation(UseProxyName.class);
        if (useProxyAnnotation != null) {
          writer.generateGetter(getterField.getSimpleName().toString(), getProxyTypeMirror(useProxyAnnotation).toString());
        } else if (useProxyAnnotation == null && useProxyNameAnnotation != null) {
          writer.generateGetter(getterField.getSimpleName().toString(), useProxyNameAnnotation.value());
        } else {
          writer.generateGetter(getterField);
        }
      }

      // Generate setters.
      for (VariableElement setterField : setterFields) {
        // Check for embedded types.
        UseProxy useProxyAnnotation = setterField.getAnnotation(UseProxy.class);
        UseProxyName useProxyNameAnnotation = setterField.getAnnotation(UseProxyName.class);
        if (useProxyAnnotation != null) {
          writer.generateSetter(setterField.getSimpleName().toString(), getProxyTypeMirror(useProxyAnnotation).toString());
        } else if (useProxyAnnotation == null && useProxyNameAnnotation != null) {
          writer.generateSetter(setterField.getSimpleName().toString(), useProxyNameAnnotation.value());
        } else {
          writer.generateSetter(setterField);
        }
      }

      writer.println();
      writer.generateFooter();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  /**
   * Workaround for MirroredTypeException (Attempt to access Class object for
   * TypeMirror).
   *
   * @see http://goo.gl/7ee2R
   */
  protected final TypeMirror getProxyTypeMirror(UseProxy useProxyAnnotation) {
    TypeMirror mirror = null;
    try {
      useProxyAnnotation.value();
    } catch (MirroredTypeException e) {
      mirror = e.getTypeMirror();
    }
    return mirror;
  }

  /**
   * @see #getTypeMirror(UseProxy)
   */
  protected final TypeMirror getLocatorTypeMirror(GenProxy genProxyAnnotation) {
    TypeMirror mirror = null;
    try {
      genProxyAnnotation.locator();
    } catch (MirroredTypeException e) {
      mirror = e.getTypeMirror();
    }
    return mirror;
  }
}
