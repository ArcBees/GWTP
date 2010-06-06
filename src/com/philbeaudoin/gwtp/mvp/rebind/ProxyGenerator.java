/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.mvp.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.philbeaudoin.gwtp.mvp.client.CodeSplitBundleProvider;
import com.philbeaudoin.gwtp.mvp.client.CodeSplitProvider;
import com.philbeaudoin.gwtp.mvp.client.DelayedBind;
import com.philbeaudoin.gwtp.mvp.client.DelayedBindRegistry;
import com.philbeaudoin.gwtp.mvp.client.EventBus;
import com.philbeaudoin.gwtp.mvp.client.Presenter;
import com.philbeaudoin.gwtp.mvp.client.RequestTabsHandler;
import com.philbeaudoin.gwtp.mvp.client.StandardProvider;
import com.philbeaudoin.gwtp.mvp.client.annotations.ContentSlot;
import com.philbeaudoin.gwtp.mvp.client.annotations.NameToken;
import com.philbeaudoin.gwtp.mvp.client.annotations.PlaceInstance;
import com.philbeaudoin.gwtp.mvp.client.annotations.ProxyCodeSplit;
import com.philbeaudoin.gwtp.mvp.client.annotations.ProxyCodeSplitBundle;
import com.philbeaudoin.gwtp.mvp.client.annotations.ProxyStandard;
import com.philbeaudoin.gwtp.mvp.client.annotations.RequestTabs;
import com.philbeaudoin.gwtp.mvp.client.annotations.TabInfo;
import com.philbeaudoin.gwtp.mvp.client.annotations.Title;
import com.philbeaudoin.gwtp.mvp.client.annotations.TitleFunction;
import com.philbeaudoin.gwtp.mvp.client.proxy.GetPlaceTitleEvent;
import com.philbeaudoin.gwtp.mvp.client.proxy.Place;
import com.philbeaudoin.gwtp.mvp.client.proxy.PlaceImpl;
import com.philbeaudoin.gwtp.mvp.client.proxy.PlaceRequest;
import com.philbeaudoin.gwtp.mvp.client.proxy.ProxyFailureHandler;
import com.philbeaudoin.gwtp.mvp.client.proxy.ProxyImpl;
import com.philbeaudoin.gwtp.mvp.client.proxy.ProxyPlaceImpl;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealContentEvent;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealContentHandler;
import com.philbeaudoin.gwtp.mvp.client.proxy.SetPlaceTitleHandler;
import com.philbeaudoin.gwtp.mvp.client.proxy.TabContentProxy;
import com.philbeaudoin.gwtp.mvp.client.proxy.TabContentProxyImpl;
import com.philbeaudoin.gwtp.mvp.client.proxy.TabContentProxyPlaceImpl;

public class ProxyGenerator extends Generator {

  private JClassType stringClass = null;

  private static final String basePresenterClassName = Presenter.class.getCanonicalName();
  private JClassType basePresenterClass = null;
  private static final String baseGinjectorClassName = Ginjector.class.getCanonicalName();
  private JClassType baseGinjectorClass = null;
  private static final String typeClassName = Type.class.getCanonicalName();
  private JClassType typeClass = null;
  private static final String revealContentHandlerClassName = RevealContentHandler.class.getCanonicalName();
  private JClassType revealContentHandlerClass = null;
  private static final String requestTabsHandlerClassName = RequestTabsHandler.class.getCanonicalName();
  private JClassType requestTabsHandlerClass = null;  
  private static final String providerClassName = Provider.class.getCanonicalName();
  private JClassType providerClass = null;
  private static final String asyncProviderClassName = AsyncProvider.class.getCanonicalName();
  private JClassType asyncProviderClass = null;
  private static final String basePlaceClassName = Place.class.getCanonicalName();
  private JClassType basePlaceClass = null;
  private static final String tabContentProxyClassName = TabContentProxy.class.getCanonicalName();
  private JClassType tabContentProxyClass = null;
  private static final String placeRequestClassName = PlaceRequest.class.getCanonicalName();
  private JClassType placeRequestClass = null;
  private static final String setPlaceTitleHandlerClassName = SetPlaceTitleHandler.class.getCanonicalName();
  private JClassType setPlaceTitleHandlerClass = null;
  private static final String placeImplClassName = PlaceImpl.class.getCanonicalName();
  private static final String delayedBindClassName = DelayedBind.class.getCanonicalName();
  private static final String proxyImplClassName = ProxyImpl.class.getCanonicalName();
  private static final String proxyPlaceImplClassName = ProxyPlaceImpl.class.getCanonicalName();
  private static final String tabContentProxyImplClassName = TabContentProxyImpl.class.getCanonicalName();
  private static final String tabContentProxyPlaceImplClassName = TabContentProxyPlaceImpl.class.getCanonicalName();

  @Override
  public String generate(TreeLogger logger, GeneratorContext ctx, String requestedClass)
  throws UnableToCompleteException {

    findBaseTypes(ctx);

    TypeOracle oracle = ctx.getTypeOracle();

    // Find the requested class
    JClassType proxyInterface = oracle.findType(requestedClass);

    if (proxyInterface == null) {
      logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
          + requestedClass + "'", null);

      throw new UnableToCompleteException();
    }

    // If it's not an interface it's a custom user-made proxy class. Don't use generator.
    if( proxyInterface.isInterface() == null )
      return null;

    ProxyStandard proxyStandardAnnotation = proxyInterface.getAnnotation( ProxyStandard.class );
    ProxyCodeSplit proxyCodeSplitAnnotation = proxyInterface.getAnnotation( ProxyCodeSplit.class );
    ProxyCodeSplitBundle proxyCodeSplitBundleAnnotation = proxyInterface.getAnnotation( ProxyCodeSplitBundle.class );

    int nbNonNullTags = 0;
    if( proxyStandardAnnotation != null ) nbNonNullTags++;
    if( proxyCodeSplitAnnotation != null ) nbNonNullTags++;
    if( proxyCodeSplitBundleAnnotation != null ) nbNonNullTags++;


    // If there is no proxy tag, don't use generator.
    if( nbNonNullTags == 0 )
      return null;


    // Make sure this proxy lies within a presenter
    JClassType presenterClass = proxyInterface.getEnclosingType();
    if( presenterClass == null || 
        !presenterClass.isAssignableTo( basePresenterClass ) ) {
      logger.log(TreeLogger.ERROR, "Proxy must be enclosed in a class derived from '"
          + basePresenterClassName + "'", null);

      throw new UnableToCompleteException();
    }    
    String presenterClassName = presenterClass.getName();

    // Watch out for more than one proxy tag
    if( nbNonNullTags > 1 ) {
      logger.log(TreeLogger.ERROR, "Proxy for '" + presenterClassName + "' has more than one @Proxy annotation.", null);
      throw new UnableToCompleteException();
    }


    // Find the package, build the generated class name.
    JPackage interfacePackage = proxyInterface.getPackage();
    String packageName = interfacePackage == null ? "" : interfacePackage.getName();
    String implClassName = presenterClassName + proxyInterface.getSimpleSourceName() + "Impl";
    String generatedClassName = packageName + "." + implClassName;


    // Create the printWriter
    PrintWriter printWriter = ctx.tryCreate(logger, packageName, implClassName);
    if (printWriter == null) 
      // We've already created it, so nothing to do
      return generatedClassName;   


    // Find ginjector
    String ginjectorClassName = null;
    try {
      ginjectorClassName = ctx.getPropertyOracle().getConfigurationProperty("gin.ginjector").getValues().get(0);
    } catch (BadPropertyValueException e) {
      logger.log(TreeLogger.ERROR, "The required configuration property 'gin.ginjector' was not found.", e);
      throw new UnableToCompleteException();
    }
    JClassType ginjectorClass = oracle.findType(ginjectorClassName);
    if( ginjectorClass == null || !ginjectorClass.isAssignableTo(baseGinjectorClass) ) {
      logger.log(TreeLogger.ERROR, "The configuration property 'gin.ginjector' is '"+ginjectorClassName+"' " +
          " which doesn't identify a type inheriting from 'Ginjector'.", null);      
      throw new UnableToCompleteException();
    }


    // Check if this proxy is also a place.
    String nameToken = null;
    String newPlaceCode = null;
    String title = null;
    if( proxyInterface.isAssignableTo( basePlaceClass ) ) {
      NameToken nameTokenAnnotation = proxyInterface.getAnnotation( NameToken.class );
      if( nameTokenAnnotation == null ) {
        logger.log(TreeLogger.ERROR, "The proxy for '" + presenterClassName + "' is a Place, but is not annotated with @' +" +
            NameToken.class.getSimpleName() + ".", null);      
        throw new UnableToCompleteException();
      }
      nameToken = nameTokenAnnotation.value();
      PlaceInstance newPlaceCodeAnnotation =  proxyInterface.getAnnotation( PlaceInstance.class );
      if( newPlaceCodeAnnotation != null )
        newPlaceCode = newPlaceCodeAnnotation.value();

      Title titleAnnotation = proxyInterface.getAnnotation( Title.class );
      if( titleAnnotation != null ) {
        title = titleAnnotation.value();
      }
    }
    TitleFunctionDescription titleFunctionDescription = findTitleFunction(logger, 
        presenterClass, presenterClassName, ginjectorClassName, ginjectorClass);
    if( titleFunctionDescription != null && title != null ) {
      logger.log(TreeLogger.ERROR, "The proxy for '" + presenterClassName + "' is annotated with @' +" +
          Title.class.getSimpleName() + " and its presenter has a method annotated with @" + 
          TitleFunction.class.getSimpleName() + ". This is not supported.", null);      
      throw new UnableToCompleteException();
    }


    // Check if this proxy is also a TabContentProxy.
    JClassType tabContainerClass = null;
    String tabContainerClassName = null;
    int tabPriority = 0;
    String tabLabel = null;
    String tabGetLabel = null;
    String tabNameToken = null;
    if( proxyInterface.isAssignableTo( tabContentProxyClass ) ) {
      TabInfo tabInfoAnnotation = proxyInterface.getAnnotation( TabInfo.class );
      if( tabInfoAnnotation == null ) {
        logger.log(TreeLogger.ERROR, "The proxy for '" + presenterClassName + "' is a TabContentProxy, but is not annotated with @' +" +
            TabInfo.class.getSimpleName() + ".", null);
        throw new UnableToCompleteException();
      }
      tabContainerClass = oracle.findType( tabInfoAnnotation.container().getCanonicalName() );
      if( tabContainerClass == null ) {
        logger.log(TreeLogger.ERROR, "The container '" + tabInfoAnnotation.container().getCanonicalName() + 
            "' in the proxy annotation for '" + presenterClassName + "' was not found.", null);
        throw new UnableToCompleteException();
      }
      tabContainerClassName = tabContainerClass.getParameterizedQualifiedSourceName();
      tabPriority = tabInfoAnnotation.priority();
      if( tabInfoAnnotation.label().length() > 0 )    // label is a string that contains the label
        tabLabel = tabInfoAnnotation.label();
      if( tabInfoAnnotation.getLabel().length() > 0 ) // getLabel is a method to call to get the label
        tabGetLabel = tabInfoAnnotation.getLabel();
      if( tabLabel == null && tabGetLabel == null ) {
        logger.log(TreeLogger.ERROR, "The @' +" +
            TabInfo.class.getSimpleName() + " annotation of the proxy for '" + presenterClassName + 
            "' must define either 'label' or 'getLabel'.", null);
        throw new UnableToCompleteException();
      }
      if( tabLabel != null && tabGetLabel != null ) {
        logger.log(TreeLogger.WARN, "The @' +" +
            TabInfo.class.getSimpleName() + " annotation of the proxy for '" + presenterClassName + 
            "' defines both 'label' and 'getLabel'. Ignoring 'getLabel'.", null);
        tabGetLabel = null;
      }      
      if( tabInfoAnnotation.nameToken().length() > 0 ) // nameToken is the name token to use when the tab is clicked
        tabNameToken = tabInfoAnnotation.nameToken();
    }

    // Start composing the class
    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
        packageName, implClassName);

    // Setup required imports
    // TODO cleanup imports
    composerFactory.addImport(GWT.class.getCanonicalName());
    composerFactory.addImport(Inject.class.getCanonicalName());  // Obsolete?
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
    composerFactory.addImport(RevealContentEvent.class.getCanonicalName());  // Obsolete?
    if( title != null || titleFunctionDescription != null ) {
      composerFactory.addImport(GetPlaceTitleEvent.class.getCanonicalName());
      composerFactory.addImport(AsyncCallback.class.getCanonicalName());
      composerFactory.addImport(Throwable.class.getCanonicalName());
    }

    // Sets interfaces and superclass
    composerFactory.addImplementedInterface(
        proxyInterface.getParameterizedQualifiedSourceName());
    composerFactory.addImplementedInterface(delayedBindClassName);
    if( nameToken == null ) {
      // Not a place
      if( tabContainerClass == null ) {
        // Standard proxy (not a Place, not a TabContentProxy)
        composerFactory.setSuperclass(proxyImplClassName+"<"+presenterClassName+">" );
      } else {
        if( tabNameToken == null ) {
          logger.log(TreeLogger.ERROR, "Class '" + presenterClassName + "' is not a Place, its @TabInfo needs to define nameToken.", null);
          throw new UnableToCompleteException();
        }         
        // TabContentProxy (not a Place, but a TabContentProxy)
        composerFactory.setSuperclass(tabContentProxyImplClassName+"<"+presenterClassName+">" );
      }
    }
    else {
      // A place
      if( tabContainerClass == null ) {
        // Place (but not a TabContentProxy)
        composerFactory.setSuperclass(proxyPlaceImplClassName+"<"+presenterClassName+">" );
      } else {
        if( tabNameToken != null ) {
          logger.log(TreeLogger.ERROR, "Class '" + presenterClassName + "' has a @NameToken and its @TabInfo has a nameToken, unsupported.", null);
          throw new UnableToCompleteException();
        }      
        // Place and TabContentProxy
        composerFactory.setSuperclass(tabContentProxyPlaceImplClassName+"<"+presenterClassName+">" );
      }
    }

    // Get a source writer
    SourceWriter writer = composerFactory.createSourceWriter(ctx, printWriter);

    // Private variable to store the gingector
    writer.println();
    writer.println( "private " + ginjectorClassName + " ginjector;" );
    
    if( nameToken != null ) {
      // Place proxy

      // BEGIN Enclosed proxy class
      writer.println();
      writer.println( "public static class WrappedProxy" );
      if( tabContainerClass == null )
        writer.println( "extends " +proxyImplClassName+"<"+presenterClassName+"> {" );
      else
        writer.println( "extends " +tabContentProxyImplClassName+"<"+presenterClassName+"> {" );
      writer.indent();

      // Enclosed proxy construcotr
      writer.println();    
      writer.println( "public WrappedProxy() {}");

      // BEGIN Enclosed proxy Bind method
      writer.println();
      writer.println( "private void delayedBind( " + ginjectorClassName + " ginjector ) {");
      writer.indent();
      // Call ProxyImpl bind method.
      writer.println( "bind( ginjector.getProxyFailureHandler() );" );
      writer.println( "EventBus eventBus = ginjector.getEventBus();"  );
      if( tabContainerClass != null ) {
        writeRequestTabHandler(logger, presenterClassName, nameToken,
            tabContainerClass, tabContainerClassName, tabPriority, tabLabel,
            tabGetLabel, writer);
      }
      writePresenterProvider(logger, ctx, writer, proxyCodeSplitAnnotation, 
          proxyCodeSplitBundleAnnotation, ginjectorClass, ginjectorClassName, 
          presenterClass, presenterClassName);
      writeSlotHandlers(logger, ctx, proxyCodeSplitAnnotation,
          proxyCodeSplitBundleAnnotation, presenterClass, presenterClassName,
          ginjectorClassName, ginjectorClass, writer);            
      writer.outdent();
      writer.println( "}" );

      // END Enclosed proxy class
      writer.outdent();
      writer.println( "}" );

      // Check if title override if needed

      // Simple string title
      if( title != null ) {
        writer.println();
        writer.println( "protected void getPlaceTitle(GetPlaceTitleEvent event) {");
        writer.indent();
        writer.println( "event.getHandler().onSetPlaceTitle( \"" + title + "\" );" );
        writer.outdent();
        writer.println( "}" );
      }

      // Presenter static method returning string
      if( titleFunctionDescription != null && titleFunctionDescription.isStatic && titleFunctionDescription.returnString ) {
        writer.println();
        writer.println( "protected void getPlaceTitle(GetPlaceTitleEvent event) {");
        writer.indent();
        writer.print( "String title = " + presenterClassName + "." );             
        writeTitleFunction(titleFunctionDescription, writer);
        writer.println();
        writer.println( "event.getHandler().onSetPlaceTitle( title );" );
        writer.outdent();
        writer.println( "}" );
      }

      // Presenter static method taking a handler (not returning a string)
      if( titleFunctionDescription != null && titleFunctionDescription.isStatic && !titleFunctionDescription.returnString ) {
        writer.println();
        writer.println( "protected void getPlaceTitle(GetPlaceTitleEvent event) {");
        writer.indent();
        writer.print( presenterClassName + "." );             
        writeTitleFunction(titleFunctionDescription, writer);
        writer.println();
        writer.println( "}" );
      }

      // Presenter non-static method returning a string
      if( titleFunctionDescription != null && !titleFunctionDescription.isStatic && titleFunctionDescription.returnString ) {
        writer.println();
        writer.println( "protected void getPlaceTitle(final GetPlaceTitleEvent event) {");
        writer.indent();
        writer.println( "getPresenter( new AsyncCallback<" + presenterClassName +  ">(){" );
        writer.indent();
        writer.indent();
        writer.println( "public void onSuccess(" + presenterClassName + " p ) {" );
        writer.indent();
        writer.print( "String title = p." );
        writeTitleFunction(titleFunctionDescription, writer);
        writer.println();
        writer.println( "event.getHandler().onSetPlaceTitle( title );" );
        writer.outdent();
        writer.println( " }" );
        writer.println( "public void onFailure(Throwable t) { event.getHandler().onSetPlaceTitle(null); }" );
        writer.outdent();
        writer.println( "} );" );
        writer.outdent();
        writer.println( "}" );
      }

      // Presenter non-static method taking a handler (not returning a string)
      if( titleFunctionDescription != null && !titleFunctionDescription.isStatic && !titleFunctionDescription.returnString ) {
        writer.println();
        writer.println( "protected void getPlaceTitle(final GetPlaceTitleEvent event) {");
        writer.indent();
        writer.println( "getPresenter( new AsyncCallback<" + presenterClassName +  ">(){" );
        writer.indent();
        writer.indent();
        writer.print( "public void onSuccess(" + presenterClassName + " p ) { p." );
        writeTitleFunction(titleFunctionDescription, writer);
        writer.println( " }" );
        writer.println( "public void onFailure(Throwable t) { event.getHandler().onSetPlaceTitle(null); }" );
        writer.outdent();
        writer.println( "} );" );
        writer.outdent();
        writer.println( "}" );
      }


    }

    // Constructor
    writer.println();    
    writer.println( "public " +  implClassName + "() {");
    writer.indent();
    writer.println( "DelayedBindRegistry.register(this);" );
    writer.outdent();
    writer.println( "}" );

    // BEGIN Bind method
    writer.println();
    writer.println( "@Override");
    writer.println( "public void delayedBind( Ginjector baseGinjector ) {");
    writer.indent();
    writeGinjector(writer, ginjectorClassName);
    if( nameToken == null ) {
      // Standard proxy (not a Place)      

      // Call ProxyImpl bind method.
      writer.println( "bind( ginjector.getProxyFailureHandler() );" );

      writer.println( "EventBus eventBus = ginjector.getEventBus();"  );
      if( tabContainerClass != null ) {
        writeRequestTabHandler(logger, presenterClassName, tabNameToken,
            tabContainerClass, tabContainerClassName, tabPriority, tabLabel,
            tabGetLabel, writer);
      }
      writePresenterProvider(logger, ctx, writer, proxyCodeSplitAnnotation, 
          proxyCodeSplitBundleAnnotation, ginjectorClass, ginjectorClassName, 
          presenterClass, presenterClassName);

      writeSlotHandlers(logger, ctx, proxyCodeSplitAnnotation,
          proxyCodeSplitBundleAnnotation, presenterClass, presenterClassName,
          ginjectorClassName, ginjectorClass, writer);        
    }
    else {

      // Place proxy

      // Call ProxyPlaceAbstract bind method.
      writer.println( "bind(  ginjector.getProxyFailureHandler(), " );
      writer.println( "    ginjector.getPlaceManager()," );
      writer.println( "    ginjector.getEventBus() );" );
      writer.println( "WrappedProxy wrappedProxy = GWT.create(WrappedProxy.class);" );
      writer.println( "wrappedProxy.delayedBind( ginjector ); " );
      writer.println( "proxy = wrappedProxy; " );
      writer.println( "String nameToken = \""+nameToken+"\"; " );
      if( newPlaceCode == null )
        writer.println( "place = new " + placeImplClassName + "( nameToken );" );
      else
        writer.println( "place = " + newPlaceCode + ";" );
    }

    // END Bind method
    writer.outdent();
    writer.println( "}" );

    writer.commit(logger);

    return generatedClassName;    
  }

  private void writeTitleFunction(
      TitleFunctionDescription titleFunctionDescription, SourceWriter writer) {
    writer.print( titleFunctionDescription.functionName + "( " );
    for( int i = 0; i<3 ; ++i ) {
      if( titleFunctionDescription.gingectorParamIndex == i ) {
        if( i > 0 ) writer.print(", ");
        writer.print( "ginjector" );
      }
      else if( titleFunctionDescription.placeRequestParamIndex == i ) {
        if( i > 0 ) writer.print(", ");
        writer.print( "event.getRequest()" );
      }
      else if( titleFunctionDescription.setPlaceTitleHandlerParamIndex == i ) {
        if( i > 0 ) writer.print(", ");
        writer.print( "event.getHandler()" );
      }
    }
    writer.print( ");" );
  }

  private void writeRequestTabHandler(TreeLogger logger,
      String presenterClassName, String nameToken,
      JClassType tabContainerClass, String tabContainerClassName,
      int tabPriority, String tabLabel, String tabGetLabel, SourceWriter writer)
  throws UnableToCompleteException {
    boolean foundRequestTabsEventType = false;
    for( JField field : tabContainerClass.getFields() ) {
      RequestTabs annotation = field.getAnnotation( RequestTabs.class );
      JParameterizedType parameterizedType = field.getType().isParameterized();
      if( annotation != null ) {
        if( !field.isStatic() || 
            parameterizedType == null || 
            !parameterizedType.isAssignableTo( typeClass ) ||
            !parameterizedType.getTypeArgs()[0].isAssignableTo(requestTabsHandlerClass) ) {
          logger.log(TreeLogger.ERROR, "Found the annotation @" + RequestTabs.class.getSimpleName() + 
              " on the invalid field '"+tabContainerClassName+"."+field.getName()+
              "'. Field must be static and its type must be Type<RequestTabsHandler<?>>.", null);
          throw new UnableToCompleteException();
        }
        foundRequestTabsEventType = true;
        writer.println( "requestTabsEventType = " + tabContainerClassName + "." + field.getName() + ";" );
        break;
      }
    }
    if( !foundRequestTabsEventType ) {
      logger.log(TreeLogger.ERROR, "Did not find any field annotated with @" + RequestTabs.class.getSimpleName() + 
          " on the container '"+tabContainerClassName+"' while building proxy for presenter '"+
          presenterClassName + "'.", null);
      throw new UnableToCompleteException();
    }
    writer.println( "priority = " + tabPriority + ";" );
    if(  tabLabel != null )
      writer.println( "label = \"" + tabLabel + "\";" );
    else
      writer.println( "label = " + tabGetLabel + ";" );
    writer.println( "historyToken = \"" + nameToken + "\";" );
    // Call TabContentProxyImpl bind method.
    writer.println( "bind( eventBus );" );
  }


  private void writeSlotHandlers(TreeLogger logger, GeneratorContext ctx,
      ProxyCodeSplit proxyCodeSplitAnnotation,
      ProxyCodeSplitBundle proxyCodeSplitBundleAnnotation,
      JClassType presenterClass, String presenterClassName,
      String ginjectorClassName, JClassType ginjectorClass, SourceWriter writer)
  throws UnableToCompleteException {
    // Register all RevealContentHandler for the @ContentSlot defined in the presenter
    writer.println();
    boolean noContentSlotFound = true;
    for( JField field : presenterClass.getFields() ) {
      ContentSlot annotation = field.getAnnotation( ContentSlot.class );
      JParameterizedType parameterizedType = field.getType().isParameterized();
      if( annotation != null ) {
        if( !field.isStatic() || 
            parameterizedType == null || 
            !parameterizedType.isAssignableTo( typeClass ) ||
            !parameterizedType.getTypeArgs()[0].isAssignableTo(revealContentHandlerClass) )
          logger.log(TreeLogger.WARN, "Found the annotation @" + ContentSlot.class.getSimpleName() + " on the invalid field '"+presenterClassName+"."+field.getName()+
              "'. Field must be static and its type must be Type<RevealContentHandler<?>>. Skipping this field.", null);      
        else {
          if( noContentSlotFound ) {
            // First content slot, fill in the required information.            
            noContentSlotFound = false;

            // Create RevealContentHandler
            writer.println();
            writer.println( "RevealContentHandler<"+presenterClassName+"> revealContentHandler = new RevealContentHandler<" + presenterClassName + ">( failureHandler, this );" );                  
          }

          writer.println( "eventBus.addHandler( " + presenterClassName + "." + field.getName() + 
          ", revealContentHandler );" );
        }
      }
    }
  }

  /**
   * Writes a local ginjector variable to the source writer.
   */
  private void writeGinjector(SourceWriter writer, String ginjectorClassName) {
    writer.println( "ginjector = (" + ginjectorClassName + ")baseGinjector;"  );
  }

  /**
   * Writes the provider for the presenter to the source writer.
   */
  private void writePresenterProvider(
      TreeLogger logger,
      GeneratorContext ctx, 
      SourceWriter writer,
      ProxyCodeSplit proxyCodeSplitAnnotation, 
      ProxyCodeSplitBundle proxyCodeSplitBundleAnnotation, 
      JClassType ginjectorClass,
      String ginjectorClassName,
      JClassType presenterClass,
      String presenterClassName ) throws UnableToCompleteException {

    TypeOracle oracle = ctx.getTypeOracle();

    // Create presenter provider and sets it in parent class
    if( proxyCodeSplitAnnotation == null && proxyCodeSplitBundleAnnotation == null ) {
      // StandardProvider

      // Find the appropriate get method in the Ginjector
      String methodName = null;
      for( JMethod method : ginjectorClass.getMethods() ) {
        JParameterizedType returnType = method.getReturnType().isParameterized();
        if( method.getParameters().length == 0 &&
            returnType != null && 
            returnType.isAssignableTo( providerClass ) &&
            returnType.getTypeArgs()[0].isAssignableTo(presenterClass) ) {
          methodName = method.getName();
          break;
        }
      }
      if( methodName == null ) {
        logger.log(TreeLogger.ERROR, "The Ginjector '"+ ginjectorClassName + 
            "' does not have a get() method returning 'Provider<"+presenterClassName+
            ">'. This is required when using @" + ProxyStandard.class.getSimpleName() + ".", null);      
        throw new UnableToCompleteException();
      }

      writer.println( "presenter = new StandardProvider<" + presenterClassName + ">( ginjector." +
          methodName + "() );" );
    }
    else if( proxyCodeSplitAnnotation != null ) {
      // CodeSplitProvider        

      // Find the appropriate get method in the Ginjector
      String methodName = null;
      for( JMethod method : ginjectorClass.getMethods() ) {
        JParameterizedType returnType = method.getReturnType().isParameterized();
        if( method.getParameters().length == 0 &&
            returnType != null && 
            returnType.isAssignableTo( asyncProviderClass ) &&
            returnType.getTypeArgs()[0].isAssignableTo(presenterClass) ) {
          methodName = method.getName();
          break;
        }
      }
      if( methodName == null ) {
        logger.log(TreeLogger.ERROR, "The Ginjector '"+ ginjectorClassName + 
            "' does not have a get() method returning 'AsyncProvider<"+presenterClassName+
            ">'. This is required when using @" + ProxyCodeSplit.class.getSimpleName() + ".", null);      
        throw new UnableToCompleteException();
      }

      writer.println( "presenter = new CodeSplitProvider<" + presenterClassName + ">( ginjector." +
          methodName + "() );" );
    }
    else {
      // CodeSplitBundleProvider

      String bundleClassName = proxyCodeSplitBundleAnnotation.bundleClass().getCanonicalName();
      JClassType bundleClass = oracle.findType(bundleClassName);

      if( bundleClass == null ) {
        logger.log(TreeLogger.ERROR, "Cannot find the bundle class '" + bundleClassName + 
            ", used with @" + ProxyCodeSplitBundle.class.getSimpleName() +
            " on presenter '" + presenterClassName + "'.", null);      
        throw new UnableToCompleteException();
      }

      // Find the appropriate get method in the Ginjector
      String methodName = null;
      for( JMethod method : ginjectorClass.getMethods() ) {
        JParameterizedType returnType = method.getReturnType().isParameterized();
        if( method.getParameters().length == 0 &&
            returnType != null && 
            returnType.isAssignableTo( asyncProviderClass ) &&
            returnType.getTypeArgs()[0].isAssignableTo(bundleClass) ) {
          methodName = method.getName();
          break;
        }
      }
      if( methodName == null ) {
        logger.log(TreeLogger.ERROR, "The Ginjector '"+ ginjectorClassName + 
            "' does not have a get() method returning 'AsyncProvider<"+bundleClassName+
            ">'. This is required when using @" + ProxyCodeSplitBundle.class.getSimpleName() + 
            " on presenter '" + presenterClassName + "'.", null);      
        throw new UnableToCompleteException();
      }

      writer.println( "presenter = new CodeSplitBundleProvider<" + presenterClassName + ", " +
          bundleClassName + ">( ginjector." + methodName + "(), " + proxyCodeSplitBundleAnnotation.id() + ");" );
    }
  }

  private static class TitleFunctionDescription {
    public String functionName;
    public boolean isStatic;
    public boolean returnString;
    public int placeRequestParamIndex = -1;
    public int gingectorParamIndex = -1;
    public int setPlaceTitleHandlerParamIndex = -1;
  }

  private TitleFunctionDescription findTitleFunction(TreeLogger logger, 
      JClassType presenterClass, String presenterClassName,
      String ginjectorClassName, JClassType ginjectorClass)
  throws UnableToCompleteException {
    // Look for the title function in the parent presenter
    TitleFunctionDescription result = null;
    for( JMethod method : presenterClass.getMethods() ) {
      TitleFunction annotation = method.getAnnotation( TitleFunction.class );
      if( annotation != null ) {
        if( result != null ) {
          logger.log(TreeLogger.ERROR, "At least two methods in presenter " + presenterClassName +
              "are annotated with @" + TitleFunction.class.getSimpleName() + ". This is illegal." );
          throw new UnableToCompleteException();
        }
        result = new TitleFunctionDescription();
        result.functionName = method.getName();
        result.isStatic = method.isStatic();

        JClassType classReturnType = method.getReturnType().isClassOrInterface();
        if( classReturnType != null && classReturnType == stringClass ) {
          result.returnString = true;
        }
        else {
          result.returnString = false;

          JPrimitiveType primitiveReturnType = method.getReturnType().isPrimitive();
          if( primitiveReturnType == null || primitiveReturnType != JPrimitiveType.VOID ) {
            logger.log(TreeLogger.WARN, "In presenter " + presenterClassName + ", method " +
                result.functionName + " annotated with @" + TitleFunction.class.getSimpleName() +
            " returns something else than void or String. Return value will be ignored and void will be assumed." );
          }
        }

        int i = 0;
        for( JParameter parameter : method.getParameters() ) {
          JClassType parameterType = parameter.getType().isClassOrInterface();
          if( parameterType.isAssignableFrom( ginjectorClass ) && result.gingectorParamIndex == -1 )
            result.gingectorParamIndex = i;
          else if( parameterType.isAssignableFrom( placeRequestClass ) && result.placeRequestParamIndex == -1 )
            result.placeRequestParamIndex = i;
          else if( parameterType.isAssignableFrom( setPlaceTitleHandlerClass ) && result.setPlaceTitleHandlerParamIndex == -1 )
            result.setPlaceTitleHandlerParamIndex = i;
          else {
            logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName + ", in method " +
                result.functionName + " annotated with @" + TitleFunction.class.getSimpleName() +
                " parameter " + i + " is invalid. Method can have at most one parameter of type " +
                ginjectorClassName + ", " + placeRequestClass.getName() + " or " +
                setPlaceTitleHandlerClass.getName() );
            throw new UnableToCompleteException();
          }
          i++;
        }

        if( result.returnString && result.setPlaceTitleHandlerParamIndex != -1 ) {
          logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName + ", the method " +
              result.functionName + " annotated with @" + TitleFunction.class.getSimpleName() +
              " returns a string and accepts a " + setPlaceTitleHandlerClass.getName() + " parameter. " +
          "This is not supported, you can have only one or the other.");
          throw new UnableToCompleteException();
        }

        if( !result.returnString && result.setPlaceTitleHandlerParamIndex == -1 ) {
          logger.log(TreeLogger.ERROR, "In presenter " + presenterClassName + ", the method " +
              result.functionName + " annotated with @" + TitleFunction.class.getSimpleName() +
              " doesn't return a string and doesn't accept a " + setPlaceTitleHandlerClass.getName() + " parameter. " +
          "You need one or the other.");
          throw new UnableToCompleteException();
        }
      }
    }

    return result;
  }




  /**
   * Make sure all the required base type information is known.
   * 
   * @param ctx The generator context.
   */
  private void findBaseTypes( GeneratorContext ctx ) {
    TypeOracle oracle = ctx.getTypeOracle();

    // Find the required base types   
    stringClass = oracle.findType( "java.lang.String" );
    basePresenterClass = oracle.findType( basePresenterClassName );
    baseGinjectorClass = oracle.findType( baseGinjectorClassName );
    typeClass = oracle.findType( typeClassName );
    revealContentHandlerClass = oracle.findType( revealContentHandlerClassName );
    requestTabsHandlerClass = oracle.findType( requestTabsHandlerClassName );
    providerClass = oracle.findType( providerClassName );
    asyncProviderClass = oracle.findType( asyncProviderClassName );
    basePlaceClass = oracle.findType( basePlaceClassName );
    tabContentProxyClass = oracle.findType( tabContentProxyClassName );
    placeRequestClass = oracle.findType( placeRequestClassName );
    setPlaceTitleHandlerClass = oracle.findType( setPlaceTitleHandlerClassName );
  }

}
