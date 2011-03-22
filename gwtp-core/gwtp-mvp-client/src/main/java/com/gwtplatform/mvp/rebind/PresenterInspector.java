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
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.TabData;
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

  private JClassType presenterClass;
  private String presenterClassName;
  private ClassInspector classInspector;
  private ProxyStandard proxyStandardAnnotation;
  private ProxyCodeSplit proxyCodeSplitAnnotation;
  private ProxyCodeSplitBundle proxyCodeSplitBundleAnnotation;

  public PresenterInspector(TypeOracle oracle, TreeLogger logger,
      ClassCollection classCollection, GinjectorInspector ginjectorInspector) {
    this.oracle = oracle;
    this.logger = logger;
    this.classCollection = classCollection;
    this.ginjectorInspector = ginjectorInspector;
  }

  /**
   * Initializes the presenter inspector given the annotation present on a proxy interface. The possible
   * annotations are {@link ProxyStandard}, {@link ProxyCodeSplit} or {@link ProxyCodeSplitBundle}.
   * If none are present the method returns {@code false} and no code should be generated.
   *
   * @param proxyInterface The annotated interface inheriting from proxy and that should be annotated.
   * @return {@code true} if the presenter provider was built, {@code false} if the interface wasn't
   *         annotated indicating that no proxy should be generated.
   * @throws UnableToCompleteException When more than one annotation is present on the proxy interface.
   */
  public boolean init(JClassType proxyInterface) throws UnableToCompleteException {
    findPresenterClass(logger, proxyInterface);
    presenterClassName = presenterClass.getName();
    classInspector = new ClassInspector(logger, presenterClass);

    proxyStandardAnnotation = proxyInterface.getAnnotation(ProxyStandard.class);
    proxyCodeSplitAnnotation = proxyInterface.getAnnotation(ProxyCodeSplit.class);
    proxyCodeSplitBundleAnnotation = proxyInterface.getAnnotation(ProxyCodeSplitBundle.class);

    return shouldGenerate();
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
   * TODO Document and refactor.
   */
  public void writeProvider(SourceWriter writer)
      throws UnableToCompleteException {

    // Create presenter provider and sets it in parent class
    if (proxyCodeSplitAnnotation == null
        && proxyCodeSplitBundleAnnotation == null) {
      // StandardProvider

      // Find the appropriate get method in the Ginjector
      String methodName = ginjectorInspector.findGetMethod(classCollection.providerClass, presenterClass);

      if (methodName == null) {
        logger.log(TreeLogger.ERROR, "The Ginjector '" + ginjectorInspector.getGinjectorClassName()
            + "' does not have a get() method returning 'Provider<"
            + presenterClassName + ">'. This is required when using @"
            + ProxyStandard.class.getSimpleName() + ".", null);
        throw new UnableToCompleteException();
      }

      writer.println("presenter = new StandardProvider<" + presenterClassName
          + ">( ginjector." + methodName + "() );");
    } else if (proxyCodeSplitAnnotation != null) {
      // CodeSplitProvider

      // Find the appropriate get method in the Ginjector
      String methodName = ginjectorInspector.findGetMethod(classCollection.asyncProviderClass, presenterClass);

      if (methodName == null) {
        logger.log(TreeLogger.ERROR, "The Ginjector '" + ginjectorInspector.getGinjectorClassName()
            + "' does not have a get() method returning 'AsyncProvider<"
            + presenterClassName + ">'. This is required when using @"
            + ProxyCodeSplit.class.getSimpleName() + ".", null);
        throw new UnableToCompleteException();
      }

      writer.println("presenter = new CodeSplitProvider<" + presenterClassName
          + ">( ginjector." + methodName + "() );");
    } else {
      // CodeSplitBundleProvider

      String bundleClassName = proxyCodeSplitBundleAnnotation.bundleClass().getCanonicalName();
      JClassType bundleClass = oracle.findType(bundleClassName);

      if (bundleClass == null) {
        logger.log(TreeLogger.ERROR,
            "Cannot find the bundle class '" + bundleClassName
                + ", used with @" + ProxyCodeSplitBundle.class.getSimpleName()
                + " on presenter '" + presenterClassName + "'.", null);
        throw new UnableToCompleteException();
      }

      // Find the appropriate get method in the Ginjector
      String methodName = ginjectorInspector.findGetMethod(classCollection.asyncProviderClass, bundleClass);

      if (methodName == null) {
        logger.log(TreeLogger.ERROR, "The Ginjector '" + ginjectorInspector.getGinjectorClassName()
            + "' does not have a get() method returning 'AsyncProvider<"
            + bundleClassName + ">'. This is required when using @"
            + ProxyCodeSplitBundle.class.getSimpleName() + " on presenter '"
            + presenterClassName + "'.", null);
        throw new UnableToCompleteException();
      }

      writer.println("presenter = new CodeSplitBundleProvider<"
          + presenterClassName + ", " + bundleClassName + ">( ginjector."
          + methodName + "(), " + proxyCodeSplitBundleAnnotation.id() + ");");
    }
  }

  /**
   * TODO Document and refactor.
   */
  public void writeSlotHandlers(SourceWriter writer)
      throws UnableToCompleteException {
    // Register all RevealContentHandler for the @ContentSlot defined in the
    // presenter
    writer.println();
    boolean noContentSlotFound = true;
    for (JField field : presenterClass.getFields()) {
      ContentSlot annotation = field.getAnnotation(ContentSlot.class);
      JParameterizedType parameterizedType = field.getType().isParameterized();
      if (annotation != null) {
        if (!field.isStatic()
            || parameterizedType == null
            || !parameterizedType.isAssignableTo(classCollection.typeClass)
            || !parameterizedType.getTypeArgs()[0].isAssignableTo(classCollection.revealContentHandlerClass)) {
          logger.log(
              TreeLogger.WARN,
              "Found the annotation @"
                  + ContentSlot.class.getSimpleName()
                  + " on the invalid field '"
                  + presenterClassName
                  + "."
                  + field.getName()
                  + "'. Field must be static and its type must be Type<RevealContentHandler<?>>. Skipping this field.",
              null);
        } else {
          if (noContentSlotFound) {
            // First content slot, fill in the required information.
            noContentSlotFound = false;

            // Create RevealContentHandler
            writer.println();
            writer.println("RevealContentHandler<" + presenterClassName
                + "> revealContentHandler = new RevealContentHandler<"
                + presenterClassName + ">( failureHandler, this );");
          }

          writer.println("getEventBus().addHandler( " + presenterClassName + "."
              + field.getName() + ", revealContentHandler );");
        }
      }
    }
  }

  /**
   * TODO Document and refactor. Should probably look in the entire class hierarchy.
   */
  public PresenterTitleMethod findPresenterTitleMethod()
      throws UnableToCompleteException {
    // Look for the title function in the parent presenter
    JMethod method = classInspector.findAnnotatedMethod(TitleFunction.class);
    if (method == null) {
      return null;
    }

    PresenterTitleMethod result = new PresenterTitleMethod(logger, classCollection, ginjectorInspector, this);
    result.init(method);
    return result;
  }

  /**
   * TODO Document and refactor. Should probably look in the entire class hierarchy.
   */
  public List<ProxyEventDescription> findProxyEvents()
      throws UnableToCompleteException {

    // Look for @ProxyEvent methods in the parent presenter
    List<ProxyEventDescription> result = new ArrayList<ProxyEventDescription>();
    for (JMethod method : presenterClass.getMethods()) {
      ProxyEvent annotation = method.getAnnotation(ProxyEvent.class);
      if (annotation != null) {
        ProxyEventDescription desc = new ProxyEventDescription();
        desc.functionName = method.getName();

        JClassType methodReturnType = method.getReturnType().isClassOrInterface();
        if (methodReturnType != null
            || method.getReturnType().isPrimitive() != JPrimitiveType.VOID) {
          logger.log(
              TreeLogger.WARN,
              "In presenter "
                  + presenterClassName
                  + ", method "
                  + desc.functionName
                  + " annotated with @"
                  + ProxyEvent.class.getSimpleName()
                  + " returns something else than void. Return value will be ignored.");
        }

        if (method.getParameters().length != 1) {
          logger.log(
              TreeLogger.ERROR,
              "In presenter "
                  + presenterClassName
                  + ", method "
                  + desc.functionName
                  + " annotated with @"
                  + ProxyEvent.class.getSimpleName()
                  + " needs to have exactly 1 parameter of a type derived from GwtEvent.");
          throw new UnableToCompleteException();
        }

        JClassType eventType = method.getParameters()[0].getType().isClassOrInterface();
        if (eventType == null || !eventType.isAssignableTo(classCollection.gwtEventClass)) {
          logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName
              + ", method " + desc.functionName + " annotated with @"
              + ProxyEvent.class.getSimpleName()
              + ", the parameter must be derived from GwtEvent.");
          throw new UnableToCompleteException();
        }
        desc.eventFullName = eventType.getQualifiedSourceName();

        // Make sure the eventType has a static getType method
        JMethod getTypeMethod = eventType.findMethod("getType", new JType[0]);
        if (getTypeMethod == null || !getTypeMethod.isStatic()
            || getTypeMethod.getParameters().length != 0) {
          logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName
              + ", method " + desc.functionName + " annotated with @"
              + ProxyEvent.class.getSimpleName() + ". The event class "
              + eventType.getName()
              + " does not have a static getType method with no parameters.");
          throw new UnableToCompleteException();
        }
        JClassType getTypeReturnType = getTypeMethod.getReturnType().isClassOrInterface();
        if (getTypeReturnType == null
            || !getTypeReturnType.isAssignableTo(classCollection.gwtEventTypeClass)) {
          logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName
              + ", method " + desc.functionName + " annotated with @"
              + ProxyEvent.class.getSimpleName() + ". The event class "
              + eventType.getName()
              + " getType() method  not return a GwtEvent.Type object.");
          throw new UnableToCompleteException();
        }

        // Find the handler class via the dispatch method
        JClassType handlerType = null;
        for (JMethod eventMethod : eventType.getMethods()) {
          if (eventMethod.getName().equals("dispatch")
              && eventMethod.getParameters().length == 1) {
            JClassType maybeHandlerType = eventMethod.getParameters()[0].getType().isClassOrInterface();
            if (maybeHandlerType != null
                && maybeHandlerType.isAssignableTo(classCollection.eventHandlerClass)) {
              if (handlerType != null) {
                logger.log(TreeLogger.ERROR, "In presenter "
                    + presenterClassName + ", method " + desc.functionName
                    + " annotated with @" + ProxyEvent.class.getSimpleName()
                    + ". The event class " + eventType.getName()
                    + " has more than one potential 'dispatch' method.");
                throw new UnableToCompleteException();
              }
              handlerType = maybeHandlerType;
            }
          }
        }

        if (handlerType == null) {
          logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName
              + ", method " + desc.functionName + " annotated with @"
              + ProxyEvent.class.getSimpleName() + ". The event class "
              + eventType.getName() + " has no valid 'dispatch' method.");
          throw new UnableToCompleteException();
        }
        desc.handlerFullName = handlerType.getQualifiedSourceName();

        // Find the handler method
        if (handlerType.getMethods().length != 1) {
          logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName
              + ", method " + desc.functionName + " annotated with @"
              + ProxyEvent.class.getSimpleName() + ". The handler class "
              + handlerType.getName() + " has more than one method.");
          throw new UnableToCompleteException();
        }

        JMethod handlerMethod = handlerType.getMethods()[0];

        JClassType handlerMethodReturnType = handlerMethod.getReturnType().isClassOrInterface();
        if (handlerMethodReturnType != null
            || handlerMethod.getReturnType().isPrimitive() != JPrimitiveType.VOID) {
          logger.log(
              TreeLogger.WARN,
              "In presenter "
                  + presenterClassName
                  + ", method "
                  + desc.functionName
                  + " annotated with @"
                  + ProxyEvent.class.getSimpleName()
                  + ". The handler class "
                  + handlerType.getName()
                  + " method "
                  + handlerMethod.getName()
                  + " returns something else than void. Return value will be ignored.");
        }

        desc.handlerMethodName = handlerMethod.getName();

        // Warn if handlerMethodName is different
        if (!desc.handlerMethodName.equals(desc.functionName)) {
          logger.log(
              TreeLogger.WARN,
              "In presenter "
                  + presenterClassName
                  + ", method "
                  + desc.functionName
                  + " annotated with @"
                  + ProxyEvent.class.getSimpleName()
                  + ". The handler class "
                  + handlerType.getName()
                  + " method "
                  + handlerMethod.getName()
                  + " differs from the annotated method. You should use the same method name for easier reference.");
        }

        // Make sure that handler method name is not already used
        for (ProxyEventDescription prevDesc : result) {
          if (prevDesc.handlerMethodName.equals(desc.handlerMethodName)
              && prevDesc.eventFullName.equals(desc.eventFullName)) {
            logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName
                + ", method " + desc.functionName + " annotated with @"
                + ProxyEvent.class.getSimpleName() + ". The handler method "
                + handlerMethod.getName() + " is already used by method "
                + prevDesc.functionName + ".");
            throw new UnableToCompleteException();
          }
        }

        result.add(desc);
      }
    }

    return result;
  }

  /**
   * TODO Document and refactor. Should probably look in the entire class hierarchy.
   */
  public TabInfoFunctionDescription findTabInfoFunction()
      throws UnableToCompleteException {
    // Look for the title function in the parent presenter
    TabInfoFunctionDescription result = null;
    for (JMethod method : presenterClass.getMethods()) {
      TabInfo annotation = method.getAnnotation(TabInfo.class);
      if (annotation != null) {
        if (result != null) {
          logger.log(TreeLogger.ERROR, "At least two methods in presenter "
              + presenterClassName + "are annotated with @"
              + TabInfo.class.getSimpleName() + ". This is illegal.");
          throw new UnableToCompleteException();
        }
        result = new TabInfoFunctionDescription();
        result.annotation = annotation;
        result.functionName = method.getName();
        if (!method.isStatic()) {
          logger.log(
              TreeLogger.ERROR,
              "In presenter "
                  + presenterClassName
                  + ", method "
                  + result.functionName
                  + " annotated with @"
                  + TabInfo.class.getSimpleName()
                  + " is not static. This is illegal.");
          throw new UnableToCompleteException();
        }

        JClassType classReturnType = method.getReturnType().isClassOrInterface();
        if (classReturnType == classCollection.stringClass) {
          result.returnString = true;
        } else if (classReturnType.isAssignableFrom(classCollection.tabDataClass)) {
          result.returnString = false;
        } else {
          logger.log(
              TreeLogger.ERROR,
              "In presenter "
                  + presenterClassName
                  + ", method "
                  + result.functionName
                  + " annotated with @"
                  + TabInfo.class.getSimpleName()
                  + " must return either a String or a "
                  + TabData.class.getSimpleName());
          throw new UnableToCompleteException();
        }

        JParameter[] parameters = method.getParameters();
        if (parameters.length > 1) {
          logger.log(
              TreeLogger.ERROR,
              "In presenter "
                  + presenterClassName
                  + ", method "
                  + result.functionName
                  + " annotated with @"
                  + TabInfo.class.getSimpleName()
                  + " accepts more than one parameter. This is illegal.");
          throw new UnableToCompleteException();
        }

        if (parameters.length == 1) {
          JClassType parameterType = parameters[0].getType().isClassOrInterface();
          if (parameterType.isAssignableFrom(ginjectorInspector.getGinjectorClass())) {
              result.hasGingectorParam = true;
          } else {
            logger.log(
                TreeLogger.ERROR,
                "In presenter "
                    + presenterClassName
                    + ", method "
                    + result.functionName
                    + " annotated with @"
                    + TabInfo.class.getSimpleName()
                    + " has a parameter that is not of type "
                    + ginjectorInspector.getGinjectorClassName()
                    + ". This is illegal.");
            throw new UnableToCompleteException();
          }
        }
      }
    }

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
