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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.gwtplatform.common.client.CodeSplitBundleProvider;
import com.gwtplatform.common.client.CodeSplitProvider;
import com.gwtplatform.common.client.StandardProvider;
import com.gwtplatform.mvp.client.DelayedBind;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.RequestTabsHandler;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.TabDataBasic;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.RequestTabs;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.annotations.Title;
import com.gwtplatform.mvp.client.annotations.TitleFunction;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.mvp.client.proxy.GetPlaceTitleEvent;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.PlaceWithGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;
import com.gwtplatform.mvp.client.proxy.ProxyImpl;
import com.gwtplatform.mvp.client.proxy.ProxyPlaceImpl;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.SetPlaceTitleHandler;
import com.gwtplatform.mvp.client.proxy.TabContentProxy;
import com.gwtplatform.mvp.client.proxy.TabContentProxyImpl;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlaceImpl;

/**
 * @author Philippe Beaudoin
 * @author Olivier Monaco
 */
public class ProxyGenerator extends Generator {

  private static class ProxyEventDescription {
    public String eventFullName;
    public String functionName;
    public String handlerFullName;
    public String handlerMethodName;
  }

  private static class TitleFunctionDescription {
    public String functionName;
    public int gingectorParamIndex = -1;
    public boolean isStatic;
    public int placeRequestParamIndex = -1;
    public boolean returnString;
    public int setPlaceTitleHandlerParamIndex = -1;
  }
  
  private static class TabInfoFunctionDescription {
    public TabInfo annotation;
    public String functionName;
    public boolean hasGingectorParam;
    public boolean returnString; // Either returns a String or a TabInfo
  }

  private static final String asyncProviderClassName = AsyncProvider.class.getCanonicalName();
  private static final String baseGinjectorClassName = Ginjector.class.getCanonicalName();
  private static final String basePlaceClassName = Place.class.getCanonicalName();
  private static final String basePresenterClassName = Presenter.class.getCanonicalName();
  private static final String delayedBindClassName = DelayedBind.class.getCanonicalName();
  private static final String eventHandlerClassName = EventHandler.class.getCanonicalName();
  private static final String gatekeeperClassName = Gatekeeper.class.getCanonicalName();
  private static final String gwtEventClassName = GwtEvent.class.getCanonicalName();
  private static final String gwtEventTypeClassName = GwtEvent.Type.class.getCanonicalName();
  private static final String placeImplClassName = PlaceImpl.class.getCanonicalName();
  private static final String placeRequestClassName = PlaceRequest.class.getCanonicalName();
  private static final String placeWithGatekeeperClassName = PlaceWithGatekeeper.class.getCanonicalName();
  private static final String providerClassName = Provider.class.getCanonicalName();
  private static final String proxyImplClassName = ProxyImpl.class.getCanonicalName();
  private static final String proxyPlaceImplClassName = ProxyPlaceImpl.class.getCanonicalName();
  private static final String requestTabsHandlerClassName = RequestTabsHandler.class.getCanonicalName();
  private static final String revealContentHandlerClassName = RevealContentHandler.class.getCanonicalName();
  private static final String setPlaceTitleHandlerClassName = SetPlaceTitleHandler.class.getCanonicalName();
  private static final String tabContentProxyClassName = TabContentProxy.class.getCanonicalName();
  private static final String tabContentProxyImplClassName = TabContentProxyImpl.class.getCanonicalName();
  private static final String tabContentProxyPlaceImplClassName = TabContentProxyPlaceImpl.class.getCanonicalName();
  private static final String typeClassName = Type.class.getCanonicalName();
  private static final String tabDataClassName = TabData.class.getCanonicalName();
  private JGenericType asyncProviderClass;
  private JClassType baseGinjectorClass;
  private JClassType basePlaceClass;
  private JClassType basePresenterClass;
  private JClassType eventHandlerClass;
  private JClassType gatekeeperClass;
  private JGenericType gwtEventClass;
  private JGenericType gwtEventTypeClass;
  private JClassType placeRequestClass;
  private JGenericType providerClass;
  private JClassType requestTabsHandlerClass;
  private JClassType revealContentHandlerClass;
  private JClassType setPlaceTitleHandlerClass;
  private JClassType stringClass;
  private JClassType tabDataClass;

  private JClassType tabContentProxyClass;

  private JClassType typeClass;

  @Override
  public String generate(TreeLogger logger, GeneratorContext ctx,
      String requestedClass) throws UnableToCompleteException {

    findBaseTypes(ctx);

    TypeOracle oracle = ctx.getTypeOracle();

    // Find the requested class
    JClassType proxyInterface = oracle.findType(requestedClass);

    if (proxyInterface == null) {
      logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
          + requestedClass + "'", null);

      throw new UnableToCompleteException();
    }

    // If it's not an interface it's a custom user-made proxy class. Don't use
    // generator.
    if (proxyInterface.isInterface() == null) {
      return null;
    }

    ProxyStandard proxyStandardAnnotation = proxyInterface.getAnnotation(ProxyStandard.class);
    ProxyCodeSplit proxyCodeSplitAnnotation = proxyInterface.getAnnotation(ProxyCodeSplit.class);
    ProxyCodeSplitBundle proxyCodeSplitBundleAnnotation = proxyInterface.getAnnotation(ProxyCodeSplitBundle.class);

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

    // If there is no proxy tag, don't use generator.
    if (nbNonNullTags == 0) {
      return null;
    }

    // Make sure this proxy lies within a presenter
    JClassType presenterClass = proxyInterface.getEnclosingType();
    if (presenterClass == null
        || !presenterClass.isAssignableTo(basePresenterClass)) {
      logger.log(TreeLogger.ERROR,
          "Proxy must be enclosed in a class derived from '"
              + basePresenterClassName + "'", null);

      throw new UnableToCompleteException();
    }
    String presenterClassName = presenterClass.getName();

    // Watch out for more than one proxy tag
    if (nbNonNullTags > 1) {
      logger.log(TreeLogger.ERROR, "Proxy for '" + presenterClassName
          + "' has more than one @Proxy annotation.", null);
      throw new UnableToCompleteException();
    }

    // Find the package, build the generated class name.
    JPackage interfacePackage = proxyInterface.getPackage();
    String packageName = interfacePackage == null ? ""
        : interfacePackage.getName();
    String implClassName = presenterClassName
        + proxyInterface.getSimpleSourceName() + "Impl";
    String generatedClassName = packageName + "." + implClassName;

    // Create the printWriter
    PrintWriter printWriter = ctx.tryCreate(logger, packageName, implClassName);
    if (printWriter == null) {
      // We've already created it, so nothing to do
      return generatedClassName;
    }

    // Find ginjector
    String ginjectorClassName = null;
    try {
      ginjectorClassName = ctx.getPropertyOracle().getConfigurationProperty(
          "gin.ginjector").getValues().get(0);
    } catch (BadPropertyValueException e) {
      logger.log(TreeLogger.ERROR,
          "The required configuration property 'gin.ginjector' was not found.",
          e);
      throw new UnableToCompleteException();
    }
    JClassType ginjectorClass = oracle.findType(ginjectorClassName);
    if (ginjectorClass == null
        || !ginjectorClass.isAssignableTo(baseGinjectorClass)) {
      logger.log(TreeLogger.ERROR,
          "The configuration property 'gin.ginjector' is '"
              + ginjectorClassName + "' "
              + " which doesn't identify a type inheriting from 'Ginjector'.",
          null);
      throw new UnableToCompleteException();
    }

    // Check if this proxy is also a place.
    String nameToken = null;
    String newPlaceCode = null; // TODO Get rid of this when we remove @PlaceInstance
    String getGatekeeperMethod = null;
    String title = null;
    if (proxyInterface.isAssignableTo(basePlaceClass)) {
      NameToken nameTokenAnnotation = proxyInterface.getAnnotation(NameToken.class);
      if (nameTokenAnnotation == null) {
        logger.log(TreeLogger.ERROR,
            "The proxy for '" + presenterClassName
                + "' is a Place, but is not annotated with @' +"
                + NameToken.class.getSimpleName() + ".", null);
        throw new UnableToCompleteException();
      }
      nameToken = nameTokenAnnotation.value();

      UseGatekeeper gatekeeperAnnotation = proxyInterface.getAnnotation(UseGatekeeper.class);
      if (gatekeeperAnnotation != null) {
        String gatekeeperName = gatekeeperAnnotation.value().getCanonicalName();
        JClassType customGatekeeperClass = oracle.findType(gatekeeperName);
        if (customGatekeeperClass == null) {
          logger.log(TreeLogger.ERROR, "The class '" + gatekeeperName
              + "' provided to @" + UseGatekeeper.class.getSimpleName()
              + " can't be found.", null);
          throw new UnableToCompleteException();
        }
        if (!customGatekeeperClass.isAssignableTo(gatekeeperClass)) {
          logger.log(TreeLogger.ERROR, "The class '" + gatekeeperName
              + "' provided to @" + UseGatekeeper.class.getSimpleName()
              + " does not inherit from '" + gatekeeperClassName + "'.", null);
          throw new UnableToCompleteException();
        }
        // Find the appropriate get method in the Ginjector
        for (JMethod method : ginjectorClass.getMethods()) {
          JClassType returnType = method.getReturnType().isClassOrInterface();
          if (method.getParameters().length == 0 && returnType != null
              && returnType.isAssignableTo(customGatekeeperClass)) {
            getGatekeeperMethod = method.getName();
            break;
          }
        }
        if (getGatekeeperMethod == null) {
          logger.log(TreeLogger.ERROR,
              "The Ginjector '" + ginjectorClassName
                  + "' does not have a get() method returning '"
                  + gatekeeperName + "'. This is required when using @"
                  + UseGatekeeper.class.getSimpleName() + ".", null);
          throw new UnableToCompleteException();
        }
      } 
      if (getGatekeeperMethod == null && newPlaceCode == null
          && proxyInterface.getAnnotation(NoGatekeeper.class) == null) {
        // No Gatekeeper specified, see if there is a DefaultGatekeeper defined
        // in the ginjector
        for (JMethod method : ginjectorClass.getMethods()) {
          if (method.getAnnotation(DefaultGatekeeper.class) != null) {
            JClassType returnType = method.getReturnType().isClassOrInterface();
            if (getGatekeeperMethod != null) {
              logger.log(TreeLogger.ERROR, "The Ginjector '"
                  + ginjectorClassName
                  + "' has more than one method annotated with @"
                  + DefaultGatekeeper.class.getSimpleName()
                  + ". This is not allowed.", null);
              throw new UnableToCompleteException();
            }

            if (method.getParameters().length != 0 || returnType == null
                || !returnType.isAssignableTo(gatekeeperClass)) {
              logger.log(
                  TreeLogger.ERROR,
                  "The method '"
                      + method.getName()
                      + "' in the Ginjector '"
                      + ginjectorClassName
                      + "' is annotated with @"
                      + DefaultGatekeeper.class.getSimpleName()
                      + " but has an invalid signature. It must not take any parameter and must return a class derived from '"
                      + gatekeeperClassName + "'.", null);
              throw new UnableToCompleteException();
            }

            getGatekeeperMethod = method.getName();
          }
        }
      }

      Title titleAnnotation = proxyInterface.getAnnotation(Title.class);
      if (titleAnnotation != null) {
        title = titleAnnotation.value();
      }
    }
    TitleFunctionDescription titleFunctionDescription = findTitleFunction(
        logger, presenterClass, presenterClassName, ginjectorClassName,
        ginjectorClass);
    if (titleFunctionDescription != null && title != null) {
      logger.log(TreeLogger.ERROR, "The proxy for '" + presenterClassName
          + "' is annotated with @' +" + Title.class.getSimpleName()
          + " and its presenter has a method annotated with @"
          + TitleFunction.class.getSimpleName() + ". This is not supported.",
          null);
      throw new UnableToCompleteException();
    }

    // Scan the containing class for @ProxyEvent annotated methods
    List<ProxyEventDescription> proxyEvents = findProxyEvents(logger,
        presenterClass, presenterClassName, ginjectorClassName, ginjectorClass);

    // Check if this proxy is also a TabContentProxy.
    JClassType tabContainerClass = null;
    String tabContainerClassName = null;
    Integer tabPriority = null;
    String tabLabel = null;
    TabInfoFunctionDescription tabInfoFunctionDescription = null;
    String tabNameToken = null;
    if (proxyInterface.isAssignableTo(tabContentProxyClass)) {
      TabInfo tabInfoAnnotation = proxyInterface.getAnnotation(TabInfo.class);
      tabInfoFunctionDescription = findTabInfoFunction(
          logger, presenterClass, presenterClassName, ginjectorClassName,
          ginjectorClass);
      
      // Ensure @TabInfo is there exactly once
      if (tabInfoAnnotation != null && tabInfoFunctionDescription != null) {
        logger.log(TreeLogger.ERROR, "Presenter " + presenterClassName
            + " contains both a proxy and a method annotated with @' +"
            + TabInfo.class.getSimpleName() + ". This is illegal.", null);
        throw new UnableToCompleteException();
      }
      if (tabInfoFunctionDescription != null) {
        tabInfoAnnotation = tabInfoFunctionDescription.annotation;
      }      
      if (tabInfoAnnotation == null) {
        logger.log(TreeLogger.ERROR, "The proxy for '" + presenterClassName
            + "' is a TabContentProxy, but is not annotated with @' +"
            + TabInfo.class.getSimpleName() 
            + " and its presenter has no method annotated with it either.", null);
        throw new UnableToCompleteException();
      }
      
      // Extract the label if its in TabInfo
      if (tabInfoAnnotation.label().length() > 0) {
        tabLabel = tabInfoAnnotation.label();
      }
      if (tabLabel != null && tabInfoFunctionDescription != null) {
        logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
            + " in " + presenterClassName + " defines the 'label' parameter and"
            + " annotates a method, this is not permitted.", null);
        throw new UnableToCompleteException();
      }
      if (tabLabel == null && tabInfoFunctionDescription == null) {
        logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
            + " in " + presenterClassName + " does not define the 'label' parameter and"
            + " does not annotate a method, this is not permitted.", null);
        throw new UnableToCompleteException();
      }

      // Extract the label if its in TabInfo (it is a negative integer if not set)
      if (tabInfoAnnotation.priority() >= 0) {
        tabPriority = tabInfoAnnotation.priority();
      }
      if (tabPriority != null &&
          tabInfoFunctionDescription != null && !tabInfoFunctionDescription.returnString) {
        logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
            + " in " + presenterClassName + " defines the 'priority' parameter and"
            + " annotates a method returning TabData, this is not permitted.", null);
        throw new UnableToCompleteException();
      }
      if (tabPriority == null &&
          (tabInfoFunctionDescription == null || tabInfoFunctionDescription.returnString)) {
        logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
            + " in " + presenterClassName + " does not define the 'priority' parameter and"
            + " does not annotate a method returning TabData, this is not permitted.", null);
        throw new UnableToCompleteException();
      }
      
      // Find the container
      tabContainerClass = oracle.findType(tabInfoAnnotation.container().getCanonicalName());
      if (tabContainerClass == null) {
        logger.log(TreeLogger.ERROR, "The container '"
            + tabInfoAnnotation.container().getCanonicalName()
            + "' in the proxy annotation for '" + presenterClassName
            + "' was not found.", null);
        throw new UnableToCompleteException();
      }
      tabContainerClassName = tabContainerClass.getParameterizedQualifiedSourceName();
      
      // Find the name token to use when the tab is clicked
      if (tabInfoAnnotation.nameToken().length() > 0) {
        tabNameToken = tabInfoAnnotation.nameToken();
      }
      if (tabNameToken != null && nameToken != null) {
        logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
            + " in " + presenterClassName + " defines the 'nameToken' parameter but"
            + " its proxy is a place, this is not permitted.", null);
        throw new UnableToCompleteException();
      }
      if (tabNameToken == null && nameToken == null) {
        logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
            + " in " + presenterClassName + " does not define the 'nameToken' parameter and"
            + " its proxy is not a place, this is not permitted.", null);
        throw new UnableToCompleteException();
      }
    }

    // Start composing the class
    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
        packageName, implClassName);

    // Setup required imports
    // TODO cleanup imports
    composerFactory.addImport(GWT.class.getCanonicalName());
    composerFactory.addImport(Inject.class.getCanonicalName()); // Obsolete?
    composerFactory.addImport(Provider.class.getCanonicalName());
    composerFactory.addImport(AsyncProvider.class.getCanonicalName());
    composerFactory.addImport(EventBus.class.getCanonicalName());
    composerFactory.addImport(StandardProvider.class.getCanonicalName());
    composerFactory.addImport(CodeSplitProvider.class.getCanonicalName());
    composerFactory.addImport(CodeSplitBundleProvider.class.getCanonicalName());
    composerFactory.addImport(ProxyFailureHandler.class.getCanonicalName());
    composerFactory.addImport(proxyImplClassName);
    composerFactory.addImport(proxyPlaceImplClassName);
    composerFactory.addImport(RevealContentHandler.class.getCanonicalName());
    composerFactory.addImport(delayedBindClassName);
    composerFactory.addImport(tabContentProxyPlaceImplClassName);
    composerFactory.addImport(DelayedBindRegistry.class.getCanonicalName());
    composerFactory.addImport(Ginjector.class.getCanonicalName());
    composerFactory.addImport(RevealContentEvent.class.getCanonicalName()); // Obsolete?
    composerFactory.addImport(DeferredCommand.class.getCanonicalName());
    composerFactory.addImport(Command.class.getCanonicalName());
    composerFactory.addImport(AsyncCallback.class.getCanonicalName());
    composerFactory.addImport(TabData.class.getCanonicalName());
    composerFactory.addImport(TabDataBasic.class.getCanonicalName());
    if (title != null || titleFunctionDescription != null) {
      composerFactory.addImport(GetPlaceTitleEvent.class.getCanonicalName());
      composerFactory.addImport(AsyncCallback.class.getCanonicalName());
      composerFactory.addImport(Throwable.class.getCanonicalName());
    }

    // Sets interfaces and superclass
    composerFactory.addImplementedInterface(proxyInterface.getParameterizedQualifiedSourceName());
    composerFactory.addImplementedInterface(delayedBindClassName);
    if (nameToken == null) {
      // Not a place
      if (tabContainerClass == null) {
        // Standard proxy (not a Place, not a TabContentProxy)
        composerFactory.setSuperclass(proxyImplClassName + "<"
            + presenterClassName + ">");
      } else {
        if (tabNameToken == null) {
          logger.log(TreeLogger.ERROR, "Class '" + presenterClassName
              + "' is not a Place, its @TabInfo needs to define nameToken.",
              null);
          throw new UnableToCompleteException();
        }
        // TabContentProxy (not a Place, but a TabContentProxy)
        composerFactory.setSuperclass(tabContentProxyImplClassName + "<"
            + presenterClassName + ">");
      }
    } else {
      // A place
      if (tabContainerClass == null) {
        // Place (but not a TabContentProxy)
        composerFactory.setSuperclass(proxyPlaceImplClassName + "<"
            + presenterClassName + ">");
      } else {
        if (tabNameToken != null) {
          logger.log(
              TreeLogger.ERROR,
              "Class '"
                  + presenterClassName
                  + "' has a @NameToken and its @TabInfo has a nameToken, unsupported.",
              null);
          throw new UnableToCompleteException();
        }
        // Place and TabContentProxy
        composerFactory.setSuperclass(tabContentProxyPlaceImplClassName + "<"
            + presenterClassName + ">");
      }
    }

    // Add all implemented handlers
    for (ProxyEventDescription desc : proxyEvents) {
      composerFactory.addImplementedInterface(desc.handlerFullName);
    }

    // Get a source writer
    SourceWriter writer = composerFactory.createSourceWriter(ctx, printWriter);

    // Private variable to store the gingector
    writer.println();
    writer.println("private " + ginjectorClassName + " ginjector;");

    if (nameToken == null) {
      // Not a place proxy
      
      if (tabContainerClass != null) {
        // A TabContentProxy
        writeGetTabDataInternalMethod(ginjectorClassName, presenterClassName,
            tabLabel, tabPriority, tabInfoFunctionDescription, writer);
      }
      
    } else {
      // Place proxy

      // BEGIN Enclosed proxy class
      writer.println();
      writer.println("public static class WrappedProxy");
      if (tabContainerClass == null) {
        writer.println("extends " + proxyImplClassName + "<"
            + presenterClassName + "> {");
      } else {
        writer.println("extends " + tabContentProxyImplClassName + "<"
            + presenterClassName + "> {");
      }
      writer.indent();

      if (tabContainerClass != null) {
        // A TabContentProxy
        writeGetTabDataInternalMethod(ginjectorClassName, presenterClassName,
            tabLabel, tabPriority, tabInfoFunctionDescription, writer);
      }
      
      // Enclosed proxy construcotr
      writer.println();
      writer.println("public WrappedProxy() {}");

      // BEGIN Enclosed proxy Bind method
      writer.println();
      writer.println("private void delayedBind(" + ginjectorClassName
          + " ginjector) {");
      writer.indent();

      if (tabContainerClass != null) {
        writeRequestTabHandler(logger, presenterClassName, nameToken,
            tabContainerClass, tabContainerClassName, tabPriority, tabLabel,
            tabInfoFunctionDescription, writer);
      }
      
      // Call ProxyImpl bind method.
      writer.println("bind(ginjector.getProxyFailureHandler(),ginjector.getEventBus());");

      writePresenterProvider(logger, ctx, writer, proxyCodeSplitAnnotation,
          proxyCodeSplitBundleAnnotation, ginjectorClass, ginjectorClassName,
          presenterClass, presenterClassName);
      writeSlotHandlers(logger, ctx, proxyCodeSplitAnnotation,
          proxyCodeSplitBundleAnnotation, presenterClass, presenterClassName,
          ginjectorClassName, ginjectorClass, writer);
      writer.outdent();
      writer.println("}");

      // END Enclosed proxy class
      writer.outdent();
      writer.println("}");

      // Check if title override if needed

      // Simple string title
      if (title != null) {
        writer.println();
        writer.println("protected void getPlaceTitle(GetPlaceTitleEvent event) {");
        writer.indent();
        writer.println("event.getHandler().onSetPlaceTitle( \"" + title
            + "\" );");
        writer.outdent();
        writer.println("}");
      }

      // Presenter static title method returning string
      if (titleFunctionDescription != null && titleFunctionDescription.isStatic
          && titleFunctionDescription.returnString) {
        writer.println();
        writer.println("protected void getPlaceTitle(GetPlaceTitleEvent event) {");
        writer.indent();
        writer.print("String title = " + presenterClassName + ".");
        writeTitleFunction(titleFunctionDescription, writer);
        writer.println();
        writer.println("event.getHandler().onSetPlaceTitle( title );");
        writer.outdent();
        writer.println("}");
      }

      // Presenter static title method taking a handler (not returning a string)
      if (titleFunctionDescription != null && titleFunctionDescription.isStatic
          && !titleFunctionDescription.returnString) {
        writer.println();
        writer.println("protected void getPlaceTitle(GetPlaceTitleEvent event) {");
        writer.indent();
        writer.print(presenterClassName + ".");
        writeTitleFunction(titleFunctionDescription, writer);
        writer.println();
        writer.println("}");
      }

      // Presenter non-static title method returning a string
      if (titleFunctionDescription != null
          && !titleFunctionDescription.isStatic
          && titleFunctionDescription.returnString) {
        writer.println();
        writer.println("protected void getPlaceTitle(final GetPlaceTitleEvent event) {");
        writer.indent();
        writer.println("getPresenter( new AsyncCallback<" + presenterClassName
            + ">(){");
        writer.indent();
        writer.indent();
        writer.println("public void onSuccess(" + presenterClassName + " p ) {");
        writer.indent();
        writer.print("String title = p.");
        writeTitleFunction(titleFunctionDescription, writer);
        writer.println();
        writer.println("event.getHandler().onSetPlaceTitle( title );");
        writer.outdent();
        writer.println(" }");
        writer.println("public void onFailure(Throwable t) { event.getHandler().onSetPlaceTitle(null); }");
        writer.outdent();
        writer.println("} );");
        writer.outdent();
        writer.println("}");
      }

      // Presenter non-static title method taking a handler (not returning a string)
      if (titleFunctionDescription != null
          && !titleFunctionDescription.isStatic
          && !titleFunctionDescription.returnString) {
        writer.println();
        writer.println("protected void getPlaceTitle(final GetPlaceTitleEvent event) {");
        writer.indent();
        writer.println("getPresenter( new AsyncCallback<" + presenterClassName
            + ">(){");
        writer.indent();
        writer.indent();
        writer.print("public void onSuccess(" + presenterClassName
            + " p ) { p.");
        writeTitleFunction(titleFunctionDescription, writer);
        writer.println(" }");
        writer.println("public void onFailure(Throwable t) { event.getHandler().onSetPlaceTitle(null); }");
        writer.outdent();
        writer.println("} );");
        writer.outdent();
        writer.println("}");
      }
    }

    // Constructor
    writer.println();
    writer.println("public " + implClassName + "() {");
    writer.indent();
    writer.println("DelayedBindRegistry.register(this);");
    writer.outdent();
    writer.println("}");

    // BEGIN Bind method
    writer.println();
    writer.println("@Override");
    writer.println("public void delayedBind(Ginjector baseGinjector) {");
    writer.indent();
    writeGinjector(writer, ginjectorClassName);
    if (nameToken == null) {
      // Standard proxy (not a Place)

      if (tabContainerClass != null) {
        writeRequestTabHandler(logger, presenterClassName, tabNameToken,
            tabContainerClass, tabContainerClassName, tabPriority, tabLabel,
            tabInfoFunctionDescription, writer);
      }

      // Call ProxyImpl bind method.
      writer.println("bind(ginjector.getProxyFailureHandler(),ginjector.getEventBus());");

      writePresenterProvider(logger, ctx, writer, proxyCodeSplitAnnotation,
          proxyCodeSplitBundleAnnotation, ginjectorClass, ginjectorClassName,
          presenterClass, presenterClassName);

      writeSlotHandlers(logger, ctx, proxyCodeSplitAnnotation,
          proxyCodeSplitBundleAnnotation, presenterClass, presenterClassName,
          ginjectorClassName, ginjectorClass, writer);
    } else {

      // Place proxy

      // Call ProxyPlaceAbstract bind method.
      writer.println("bind(ginjector.getProxyFailureHandler(), ");
      writer.println("    ginjector.getPlaceManager(),");
      writer.println("    ginjector.getEventBus());");
      writer.println("WrappedProxy wrappedProxy = GWT.create(WrappedProxy.class);");
      writer.println("wrappedProxy.delayedBind( ginjector ); ");
      writer.println("proxy = wrappedProxy; ");
      writer.println("String nameToken = \"" + nameToken + "\"; ");
      if (newPlaceCode == null) {
        if (getGatekeeperMethod == null) {
          writer.println("place = new " + placeImplClassName + "( nameToken );");
        } else {
          writer.println("place = new " + placeWithGatekeeperClassName
              + "( nameToken, ginjector." + getGatekeeperMethod + "() );");
        }
      } else {
        writer.println("place = " + newPlaceCode + ";");
      }
    }

    // Register all @ProxyEvent
    for (ProxyEventDescription desc : proxyEvents) {
      writer.println("getEventBus().addHandler( " + desc.eventFullName
          + ".getType(), this );");
    }

    // END Bind method
    writer.outdent();
    writer.println("}");

    // Write all handler methods
    for (ProxyEventDescription desc : proxyEvents) {
      writer.println("");
      writeHandlerMethod(presenterClassName, desc, writer);
    }

    writer.commit(logger);

    return generatedClassName;
  }

  private void writeGetTabDataInternalMethod(String ginjectorClassName,
      String presenterClassName, String tabLabel, Integer tabPriority, 
      TabInfoFunctionDescription tabLabelFunctionDescription,
      SourceWriter writer) {

    if (tabLabel != null) {
      // Simple string tab label
      writer.println();
      writer.println("protected TabData getTabDataInternal(" + ginjectorClassName + " ginjector) {");
      writer.indent();
      writer.println("  return new TabDataBasic(\"" + tabLabel + "\", " + tabPriority + ");");
      writer.outdent();
      writer.println("}");
    } else if (tabLabelFunctionDescription.returnString) {
      // Presenter static method returning a string
      writer.println();
      writer.println("protected TabData getTabDataInternal(" + ginjectorClassName + " ginjector) {");
      writer.indent();
      writer.print("  return new TabDataBasic(");
      writer.print(presenterClassName + ".");
      writeTabInfoFunctionCall(tabLabelFunctionDescription, writer);
      writer.println(", " + tabPriority + ");");
      writer.outdent();
      writer.println("}");
    } else {
      // Presenter static method returning tab data
      writer.println();
      writer.println("protected TabData getTabDataInternal(" + ginjectorClassName + " ginjector) {");
      writer.indent();
      writer.print("  return ");
      writer.print(presenterClassName + ".");
      writeTabInfoFunctionCall(tabLabelFunctionDescription, writer);
      writer.println(";");
      writer.outdent();
      writer.println("}");
    }
  }

  /**
   * Make sure all the required base type information is known.
   * 
   * @param ctx The generator context.
   */
  private void findBaseTypes(GeneratorContext ctx) {
    TypeOracle oracle = ctx.getTypeOracle();

    // Find the required base types
    stringClass = oracle.findType("java.lang.String");
    basePresenterClass = oracle.findType(basePresenterClassName);
    baseGinjectorClass = oracle.findType(baseGinjectorClassName);
    typeClass = oracle.findType(typeClassName);
    revealContentHandlerClass = oracle.findType(revealContentHandlerClassName);
    requestTabsHandlerClass = oracle.findType(requestTabsHandlerClassName);
    providerClass = oracle.findType(providerClassName).isGenericType();
    asyncProviderClass = oracle.findType(asyncProviderClassName).isGenericType();
    basePlaceClass = oracle.findType(basePlaceClassName);
    tabContentProxyClass = oracle.findType(tabContentProxyClassName);
    gatekeeperClass = oracle.findType(gatekeeperClassName);
    placeRequestClass = oracle.findType(placeRequestClassName);
    gwtEventClass = oracle.findType(gwtEventClassName).isGenericType();
    gwtEventTypeClass = oracle.findType(gwtEventTypeClassName).isGenericType();
    eventHandlerClass = oracle.findType(eventHandlerClassName);
    setPlaceTitleHandlerClass = oracle.findType(setPlaceTitleHandlerClassName);
    tabDataClass = oracle.findType(tabDataClassName);
  }

  private List<ProxyEventDescription> findProxyEvents(TreeLogger logger,
      JClassType presenterClass, String presenterClassName,
      String ginjectorClassName, JClassType ginjectorClass)
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
        if (eventType == null || !eventType.isAssignableTo(gwtEventClass)) {
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
            || !getTypeReturnType.isAssignableTo(gwtEventTypeClass)) {
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
                && maybeHandlerType.isAssignableTo(eventHandlerClass)) {
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

  private TitleFunctionDescription findTitleFunction(TreeLogger logger,
      JClassType presenterClass, String presenterClassName,
      String ginjectorClassName, JClassType ginjectorClass)
      throws UnableToCompleteException {
    // Look for the title function in the parent presenter
    TitleFunctionDescription result = null;
    for (JMethod method : presenterClass.getMethods()) {
      TitleFunction annotation = method.getAnnotation(TitleFunction.class);
      if (annotation != null) {
        if (result != null) {
          logger.log(TreeLogger.ERROR, "At least two methods in presenter "
              + presenterClassName + "are annotated with @"
              + TitleFunction.class.getSimpleName() + ". This is illegal.");
          throw new UnableToCompleteException();
        }
        result = new TitleFunctionDescription();
        result.functionName = method.getName();
        result.isStatic = method.isStatic();

        JClassType classReturnType = method.getReturnType().isClassOrInterface();
        if (classReturnType != null && classReturnType == stringClass) {
          result.returnString = true;
        } else {
          result.returnString = false;

          JPrimitiveType primitiveReturnType = method.getReturnType().isPrimitive();
          if (primitiveReturnType == null
              || primitiveReturnType != JPrimitiveType.VOID) {
            logger.log(
                TreeLogger.WARN,
                "In presenter "
                    + presenterClassName
                    + ", method "
                    + result.functionName
                    + " annotated with @"
                    + TitleFunction.class.getSimpleName()
                    + " returns something else than void or String. Return value will be ignored and void will be assumed.");
          }
        }

        int i = 0;
        for (JParameter parameter : method.getParameters()) {
          JClassType parameterType = parameter.getType().isClassOrInterface();
          if (parameterType.isAssignableFrom(ginjectorClass)
              && result.gingectorParamIndex == -1) {
            result.gingectorParamIndex = i;
          } else if (parameterType.isAssignableFrom(placeRequestClass)
              && result.placeRequestParamIndex == -1) {
            result.placeRequestParamIndex = i;
          } else if (parameterType.isAssignableFrom(setPlaceTitleHandlerClass)
              && result.setPlaceTitleHandlerParamIndex == -1) {
            result.setPlaceTitleHandlerParamIndex = i;
          } else {
            logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName
                + ", in method " + result.functionName + " annotated with @"
                + TitleFunction.class.getSimpleName() + " parameter " + i
                + " is invalid. Method can have at most one parameter of type "
                + ginjectorClassName + ", " + placeRequestClass.getName()
                + " or " + setPlaceTitleHandlerClass.getName());
            throw new UnableToCompleteException();
          }
          i++;
        }

        if (result.returnString && result.setPlaceTitleHandlerParamIndex != -1) {
          logger.log(
              TreeLogger.ERROR,
              "In presenter "
                  + presenterClassName
                  + ", the method "
                  + result.functionName
                  + " annotated with @"
                  + TitleFunction.class.getSimpleName()
                  + " returns a string and accepts a "
                  + setPlaceTitleHandlerClass.getName()
                  + " parameter. "
                  + "This is not supported, you can have only one or the other.");
          throw new UnableToCompleteException();
        }

        if (!result.returnString && result.setPlaceTitleHandlerParamIndex == -1) {
          logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName
              + ", the method " + result.functionName + " annotated with @"
              + TitleFunction.class.getSimpleName()
              + " doesn't return a string and doesn't accept a "
              + setPlaceTitleHandlerClass.getName() + " parameter. "
              + "You need one or the other.");
          throw new UnableToCompleteException();
        }
      }
    }

    return result;
  }

  private TabInfoFunctionDescription findTabInfoFunction(TreeLogger logger,
      JClassType presenterClass, String presenterClassName,
      String ginjectorClassName, JClassType ginjectorClass)
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
        if (classReturnType == stringClass) {
          result.returnString = true;
        } else if (classReturnType.isAssignableFrom(tabDataClass)) {
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
          if (parameterType.isAssignableFrom(ginjectorClass)) {
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
                    + ginjectorClassName
                    + ". This is illegal.");
            throw new UnableToCompleteException();
          }
        }
      }
    }

    return result;
  }

  /**
   * Writes a local ginjector variable to the source writer.
   */
  private void writeGinjector(SourceWriter writer, String ginjectorClassName) {
    writer.println("ginjector = (" + ginjectorClassName + ")baseGinjector;");
  }

  private void writeHandlerMethod(String presenterClassName,
      ProxyEventDescription desc, SourceWriter writer) {
    writer.println("@Override");
    writer.println("public final void " + desc.handlerMethodName + "( final "
        + desc.eventFullName + " event ) {");
    writer.indent();
    writer.println("getPresenter( new AsyncCallback<" + presenterClassName
        + ">() {");
    writer.indent();
    writer.println("@Override");
    writer.println("public void onFailure(Throwable caught) {");
    writer.indent();
    writer.println("failureHandler.onFailedGetPresenter(caught);");
    writer.outdent();
    writer.println("}");
    writer.println("@Override");
    writer.println("public void onSuccess(final " + presenterClassName
        + " presenter) {");
    writer.indent();
    writer.println("DeferredCommand.addCommand( new Command() {");
    writer.indent();
    writer.println("@Override");
    writer.println("public void execute() {");
    writer.indent();
    writer.println("presenter." + desc.functionName + "( event );");
    writer.outdent();
    writer.println("}");
    writer.outdent();
    writer.println("} );");
    writer.outdent();
    writer.println("}");
    writer.outdent();
    writer.println("} );");
    writer.outdent();
    writer.println("}");
  }

  /**
   * Writes the provider for the presenter to the source writer.
   */
  private void writePresenterProvider(TreeLogger logger, GeneratorContext ctx,
      SourceWriter writer, ProxyCodeSplit proxyCodeSplitAnnotation,
      ProxyCodeSplitBundle proxyCodeSplitBundleAnnotation,
      JClassType ginjectorClass, String ginjectorClassName,
      JClassType presenterClass, String presenterClassName)
      throws UnableToCompleteException {

    TypeOracle oracle = ctx.getTypeOracle();

    // Create presenter provider and sets it in parent class
    if (proxyCodeSplitAnnotation == null
        && proxyCodeSplitBundleAnnotation == null) {
      // StandardProvider

      // Find the appropriate get method in the Ginjector
      String methodName = findGetMethod(providerClass, presenterClass,
          ginjectorClass);

      if (methodName == null) {
        logger.log(TreeLogger.ERROR, "The Ginjector '" + ginjectorClassName
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
      String methodName = findGetMethod(asyncProviderClass, presenterClass,
          ginjectorClass);

      if (methodName == null) {
        logger.log(TreeLogger.ERROR, "The Ginjector '" + ginjectorClassName
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
      String methodName = findGetMethod(asyncProviderClass, bundleClass,
          ginjectorClass);

      if (methodName == null) {
        logger.log(TreeLogger.ERROR, "The Ginjector '" + ginjectorClassName
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

  private String findGetMethod(JGenericType desiredReturnType,
      JClassType desiredReturnTypeParameter, JClassType ginjectorClass) {

    for (JClassType classType : ginjectorClass.getFlattenedSupertypeHierarchy()) {
      for (JMethod method : classType.getMethods()) {
        JParameterizedType returnType = method.getReturnType().isParameterized();
        if (method.getParameters().length == 0
            && returnType != null
            && returnType.isAssignableTo(desiredReturnType)
            && returnType.getTypeArgs()[0].isAssignableTo(desiredReturnTypeParameter)) {
          return method.getName();
        }
      }
    }
    return null;
  }

  private void writeRequestTabHandler(TreeLogger logger,
      String presenterClassName, String nameToken,
      JClassType tabContainerClass, String tabContainerClassName,
      Integer tabPriority, String tabLabel, 
      TabInfoFunctionDescription tabLabelFunctionDescription, 
      SourceWriter writer)
      throws UnableToCompleteException {
    boolean foundRequestTabsEventType = false;
    for (JField field : tabContainerClass.getFields()) {
      RequestTabs annotation = field.getAnnotation(RequestTabs.class);
      JParameterizedType parameterizedType = field.getType().isParameterized();
      if (annotation != null) {
        if (!field.isStatic()
            || parameterizedType == null
            || !parameterizedType.isAssignableTo(typeClass)
            || !parameterizedType.getTypeArgs()[0].isAssignableTo(requestTabsHandlerClass)) {
          logger.log(
              TreeLogger.ERROR,
              "Found the annotation @"
                  + RequestTabs.class.getSimpleName()
                  + " on the invalid field '"
                  + tabContainerClassName
                  + "."
                  + field.getName()
                  + "'. Field must be static and its type must be Type<RequestTabsHandler<?>>.",
              null);
          throw new UnableToCompleteException();
        }
        foundRequestTabsEventType = true;
        writer.println("requestTabsEventType = " + tabContainerClassName + "."
            + field.getName() + ";");
        break;
      }
    }
    if (!foundRequestTabsEventType) {
      logger.log(TreeLogger.ERROR, "Did not find any field annotated with @"
          + RequestTabs.class.getSimpleName() + " on the container '"
          + tabContainerClassName + "' while building proxy for presenter '"
          + presenterClassName + "'.", null);
      throw new UnableToCompleteException();
    }
    writer.println("tabData = getTabDataInternal(ginjector);");
    writer.println("targetHistoryToken = \"" + nameToken + "\";");
  }

  private void writeSlotHandlers(TreeLogger logger, GeneratorContext ctx,
      ProxyCodeSplit proxyCodeSplitAnnotation,
      ProxyCodeSplitBundle proxyCodeSplitBundleAnnotation,
      JClassType presenterClass, String presenterClassName,
      String ginjectorClassName, JClassType ginjectorClass, SourceWriter writer)
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
            || !parameterizedType.isAssignableTo(typeClass)
            || !parameterizedType.getTypeArgs()[0].isAssignableTo(revealContentHandlerClass)) {
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

  private void writeTitleFunction(
      TitleFunctionDescription titleFunctionDescription, SourceWriter writer) {
    writer.print(titleFunctionDescription.functionName + "( ");
    for (int i = 0; i < 3; ++i) {
      if (titleFunctionDescription.gingectorParamIndex == i) {
        if (i > 0) {
          writer.print(", ");
        }
        writer.print("ginjector");
      } else if (titleFunctionDescription.placeRequestParamIndex == i) {
        if (i > 0) {
          writer.print(", ");
        }
        writer.print("event.getRequest()");
      } else if (titleFunctionDescription.setPlaceTitleHandlerParamIndex == i) {
        if (i > 0) {
          writer.print(", ");
        }
        writer.print("event.getHandler()");
      }
    }
    writer.print(");");
  }

  private void writeTabInfoFunctionCall(
      TabInfoFunctionDescription tabLabelFunctionDescription, SourceWriter writer) {
    writer.print(tabLabelFunctionDescription.functionName + "( ");
    if (tabLabelFunctionDescription.hasGingectorParam) {
      writer.print("ginjector");
    }
     writer.print(")");
  }

}
