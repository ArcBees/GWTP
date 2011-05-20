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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.annotations.TitleFunction;

/**
 * A class used to inspect the presenter, the methods and inner interfaces it contains.
 * You must call {@link #init(JClassType)} before any other method can be called.
 *
 * @author Philippe Beaudoin
 */
public class PresenterInspector {

  private final TypeOracle oracle;
  private final TreeLogger logger;
  private final ClassCollection classCollection;
  private final GinjectorInspector ginjectorInspector;
  private final List<JField> contentSlots = new ArrayList<JField>();

  private JClassType presenterClass;
  private String presenterClassName;
  private ClassInspector classInspector;
  private ProxyStandard proxyStandardAnnotation;
  private ProxyCodeSplit proxyCodeSplitAnnotation;
  private ProxyCodeSplitBundle proxyCodeSplitBundleAnnotation;
  private String getPresenterMethodName;
  private String bundleClassName;

  public PresenterInspector(TypeOracle oracle, TreeLogger logger,
      ClassCollection classCollection, GinjectorInspector ginjectorInspector) {
    this.oracle = oracle;
    this.logger = logger;
    this.classCollection = classCollection;
    this.ginjectorInspector = ginjectorInspector;
  }

  /**
   * Initializes the presenter inspector given the annotation present on a proxy interface. The
   * possible annotations are {@link ProxyStandard}, {@link ProxyCodeSplit} or
   * {@link ProxyCodeSplitBundle}. If none are present the method returns {@code false} and no code
   * should be generated.
   *
   * @param proxyInterface The annotated interface inheriting from proxy and that should be
   *        annotated.
   * @return {@code true} if the presenter provider was built, {@code false} if the interface wasn't
   *         annotated indicating that no proxy should be generated.
   * @throws UnableToCompleteException When more than one annotation is present on the proxy
   *         interface.
   */
  public boolean init(JClassType proxyInterface) throws UnableToCompleteException {
    findPresenterClass(logger, proxyInterface);
    presenterClassName = presenterClass.getName();
    classInspector = new ClassInspector(logger, presenterClass);

    proxyStandardAnnotation = proxyInterface.getAnnotation(ProxyStandard.class);
    proxyCodeSplitAnnotation = proxyInterface.getAnnotation(ProxyCodeSplit.class);
    proxyCodeSplitBundleAnnotation = proxyInterface.getAnnotation(ProxyCodeSplitBundle.class);

    if (!shouldGenerate()) {
      return false;
    }

    findGetPresenterMethodName();

    classInspector.collectStaticAnnotatedFields(classCollection.typeClass,
        classCollection.revealContentHandlerClass, ContentSlot.class, contentSlots);

    return true;
  }

  private void findGetPresenterMethodName() throws UnableToCompleteException {
    if (proxyStandardAnnotation != null) {
      getPresenterMethodName = ginjectorInspector.findGetMethod(
          classCollection.providerClass, presenterClass);

      failIfNoProviderError(getPresenterMethodName, "Provider",
          ProxyStandard.class.getSimpleName());
    } else if (proxyCodeSplitAnnotation != null) {
      getPresenterMethodName = ginjectorInspector.findGetMethod(classCollection.asyncProviderClass,
          presenterClass);

      failIfNoProviderError(getPresenterMethodName, "AsyncProvider",
          ProxyCodeSplit.class.getSimpleName());
    } else {
      JClassType bundleClass = findBundleClass();
      getPresenterMethodName = ginjectorInspector.findGetMethod(classCollection.asyncProviderClass,
          bundleClass);

      failIfNoProviderError(getPresenterMethodName, "AsyncProvider", bundleClassName,
          ProxyCodeSplit.class.getSimpleName());
    }
  }

  private JClassType findBundleClass() throws UnableToCompleteException {
    assert proxyCodeSplitBundleAnnotation != null;
    bundleClassName = proxyCodeSplitBundleAnnotation.bundleClass().getCanonicalName();
    JClassType bundleClass = oracle.findType(bundleClassName);

    if (bundleClass == null) {
      logger.log(TreeLogger.ERROR,
          "Cannot find the bundle class '" + bundleClassName
              + ", used with @" + ProxyCodeSplitBundle.class.getSimpleName()
              + " on presenter '" + presenterClassName + "'.", null);
      throw new UnableToCompleteException();
    }
    return bundleClass;
  }

  private void failIfNoProviderError(String providerMethodName,
      String providerClassName, String providedClassName,
      String annotationClassName) throws UnableToCompleteException {
    if (providerClassName != null) {
      return;
    }

    String actualProvidedClassName = providedClassName;
    String extraString = " See presenter '" + presenterClassName + "'.";
    if (providedClassName == null) {
      actualProvidedClassName = presenterClassName;
      extraString = "";
    }

    logger.log(TreeLogger.ERROR, "The Ginjector '" + ginjectorInspector.getGinjectorClassName()
        + "' does not have a get() method returning '" + providerClassName + "<"
        + actualProvidedClassName + ">'. This is required when using @"
        + annotationClassName + "." + extraString, null);
    throw new UnableToCompleteException();
  }

  private void failIfNoProviderError(String providerMethodName,
      String providerClassName, String annotationClassName) throws UnableToCompleteException {
    failIfNoProviderError(providerMethodName, providerClassName, null, annotationClassName);
  }

  /**
   * @return The class of the presenter that this {@link PresenterInspector} provides.
   */
  public JClassType getPresenterClass() {
    return presenterClass;
  }

  /**
   * @return The name of the class of the presenter that this {@link PresenterInspector} provides.
   */
  public String getPresenterClassName() {
    return presenterClassName;
  }

  /**
   * Writes the assignation into the {@code provider} field of
   * {@link com.gwtplatform.mvp.client.proxy.ProxyImpl ProxyImpl}.
   */
  public void writeProviderAssignation(SourceWriter writer) {

    if (proxyStandardAnnotation != null) {
      writer.println("presenter = new StandardProvider<" + presenterClassName
          + ">( ginjector." + getPresenterMethodName + "() );");
    } else if (proxyCodeSplitAnnotation != null) {
      writer.println("presenter = new CodeSplitProvider<" + presenterClassName
          + ">( ginjector." + getPresenterMethodName + "() );");
    } else {
      assert proxyCodeSplitBundleAnnotation != null;
      writer.println("presenter = new CodeSplitBundleProvider<"
          + presenterClassName + ", " + bundleClassName + ">( ginjector."
          + getPresenterMethodName + "(), " + proxyCodeSplitBundleAnnotation.id() + ");");
    }
  }

  /**
   * Register a {@link com.gwtplatform.mvp.client.proxy.RevealContentHandler RevealContentHandler}
   * for each {@code @ContentSlot} defined in the presenter.
   */
  public void writeContentSlotHandlerRegistration(SourceWriter writer) {
    if (contentSlots.size() == 0) {
      return;
    }

    writer.println();
    writer.println("RevealContentHandler<" + presenterClassName
        + "> revealContentHandler = new RevealContentHandler<"
        + presenterClassName + ">( eventBus, this );");

    for (JField field : contentSlots) {
      writer.println("getEventBus().addHandler( " + presenterClassName + "."
          + field.getName() + ", revealContentHandler );");
    }
  }

  /**
   * Look in the presenter and any superclass for a method annotated with {@link TitleFunction}.
   */
  public PresenterTitleMethod findPresenterTitleMethod()
      throws UnableToCompleteException {
    // Look for the title function in the parent presenter
    JMethod method = classInspector.findAnnotatedMethod(TitleFunction.class);
    if (method == null) {
      return null;
    }

    PresenterTitleMethod result = new PresenterTitleMethod(logger, classCollection,
        ginjectorInspector, this);
    result.init(method);
    return result;
  }

  /**
   * Collect all the {@link ProxyEventMethod} of methods annotated with
   * {@literal @}{@link ProxyEvent} and contained in the presenter or its super classes.
   *
   * @param proxyEventMethods The list into which to collect the proxy events.
   * @throws UnableToCompleteException If something goes wrong. An error will be logged.
   */
  public void collectProxyEvents(List<ProxyEventMethod> proxyEventMethods)
      throws UnableToCompleteException {

    // Look for @ProxyEvent methods in the parent presenter
    List<JMethod> collectedMethods = new ArrayList<JMethod>();
    classInspector.collectAnnotatedMethods(ProxyEvent.class, collectedMethods);

    for (JMethod method : collectedMethods) {
      ProxyEventMethod proxyEventMethod = new ProxyEventMethod(logger, classCollection, this);
      proxyEventMethod.init(method);
      // Make sure that handler method name is not already used
      for (ProxyEventMethod previousMethod : proxyEventMethods) {
        proxyEventMethod.ensureNoClashWith(previousMethod);
      }
      proxyEventMethods.add(proxyEventMethod);
    }
  }

  /**
   * Retrieves the static {@link TabInfoMethod} defined in the presenter.
   *
   * @return The {@link TabInfoMethod}, or {@code null} if none is found.
   * @throws UnableToCompleteException If something goes wrong. An error will be logged.
   */
  public TabInfoMethod findTabInfoMethod()
      throws UnableToCompleteException {

    JMethod method = classInspector.findAnnotatedMethod(TabInfo.class);
    if (method == null) {
      return null;
    }
    TabInfoMethod result = new TabInfoMethod(logger, classCollection,
        ginjectorInspector, this);
    result.init(method);

    return result;
  }

  /**
   * Identify the presenter class containing the proxy as an inner interface.
   */
  private void findPresenterClass(TreeLogger logger,
      JClassType proxyInterface) throws UnableToCompleteException {
    presenterClass = proxyInterface.getEnclosingType();
    if (presenterClass == null
        || !presenterClass.isAssignableTo(classCollection.basePresenterClass)) {
      presenterClass = null;
      logger.log(TreeLogger.ERROR,
          "Proxy must be enclosed in a class derived from '"
              + ClassCollection.basePresenterClassName + "'", null);

      throw new UnableToCompleteException();
    }
  }

  private boolean shouldGenerate() throws UnableToCompleteException {
    int nbNonNullTags = 0;
    if (proxyStandardAnnotation != null) {
      nbNonNullTags++;
    }
    if (proxyCodeSplitAnnotation != null) {
      nbNonNullTags++;
    }
    if (proxyCodeSplitBundleAnnotation != null) {
      nbNonNullTags++;
    }

    // If there is no annotations, don't use generator.
    if (nbNonNullTags == 0) {
      return false;
    }

    // Fail if there is more than one annotation.
    if (nbNonNullTags > 1) {
      logger.log(TreeLogger.ERROR, "Proxy for '" + presenterClassName
          + "' has more than one @Proxy annotation.", null);
      throw new UnableToCompleteException();
    }
    return true;
  }
}