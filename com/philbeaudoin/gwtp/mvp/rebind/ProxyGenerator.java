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
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.Ginjector;
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
import com.philbeaudoin.gwtp.mvp.client.proxy.Place;
import com.philbeaudoin.gwtp.mvp.client.proxy.PlaceImpl;
import com.philbeaudoin.gwtp.mvp.client.proxy.ProxyFailureHandler;
import com.philbeaudoin.gwtp.mvp.client.proxy.ProxyImpl;
import com.philbeaudoin.gwtp.mvp.client.proxy.ProxyPlace;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealContentEvent;
import com.philbeaudoin.gwtp.mvp.client.proxy.RevealContentHandler;
import com.philbeaudoin.gwtp.mvp.client.proxy.TabContentProxy;
import com.philbeaudoin.gwtp.mvp.client.proxy.TabContentProxyImpl;
import com.philbeaudoin.gwtp.mvp.client.proxy.TabContentProxyPlace;

public class ProxyGenerator extends Generator {

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
  private static final String placeImplClassName = PlaceImpl.class.getCanonicalName();
  private static final String delayedBindClassName = DelayedBind.class.getCanonicalName();
  private static final String proxyImplClassName = ProxyImpl.class.getCanonicalName();
  private static final String proxyPlaceClassName = ProxyPlace.class.getCanonicalName();
  private static final String tabContentProxyImplClassName = TabContentProxyImpl.class.getCanonicalName();
  private static final String tabContentProxyPlaceClassName = TabContentProxyPlace.class.getCanonicalName();

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
    }


    // Check if this proxy is also a TabContentProxy.
    JClassType tabContainerClass = null;
    String tabContainerClassName = null;
    int tabPriority = 0;
    String tabLabel = null;
    String tabGetLabel = null;
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
      tabContainerClassName = tabContainerClass.getName();
      tabPriority = tabInfoAnnotation.priority();
      if( tabInfoAnnotation.label().length() > 0 )
        tabLabel = tabInfoAnnotation.label();
      if( tabInfoAnnotation.getLabel().length() > 0 )
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
    composerFactory.addImport(proxyPlaceClassName);
    composerFactory.addImport(RevealContentHandler.class.getCanonicalName());
    composerFactory.addImport(delayedBindClassName);
    composerFactory.addImport(DelayedBindRegistry.class.getCanonicalName());
    composerFactory.addImport(Ginjector.class.getCanonicalName());
    composerFactory.addImport(RevealContentEvent.class.getCanonicalName());  // Obsolete?


    // Sets interfaces and superclass
    composerFactory.addImplementedInterface(
        proxyInterface.getParameterizedQualifiedSourceName());
    composerFactory.addImplementedInterface(delayedBindClassName);
    if( nameToken == null ) {
      // Not a place
      if( tabContainerClass == null )
        // Standard proxy (not a Place, not a TabContentProxy)
        composerFactory.setSuperclass(proxyImplClassName+"<"+presenterClassName+">" );
      else
        // TabContentProxy (not a Place, but a TabContentProxy)
        composerFactory.setSuperclass(tabContentProxyImplClassName+"<"+presenterClassName+">" );
    }
    else {
      // A place
      if( tabContainerClass == null )
        // Place (but not a TabContentProxy)
        composerFactory.setSuperclass(proxyPlaceClassName+"<"+presenterClassName+">" );
      else
        // Place and TabContentProxy
        composerFactory.setSuperclass(tabContentProxyPlaceClassName+"<"+presenterClassName+">" );
    }

    // Get a source writer
    SourceWriter writer = composerFactory.createSourceWriter(ctx, printWriter);

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

    if( nameToken == null ) {
      // Standard proxy (not a Place)      

      writeGinjector(writer, ginjectorClassName);
      // Call ProxyImpl bind method.
      writer.println( "bind( ginjector.getProxyFailureHandler() );" );
      if( tabContainerClass != null ) {
        // TODO
        assert false : "Automatic proxys for non-place TabContentProxy not yet supported!";
      }
      
      writer.println( "EventBus eventBus = ginjector.getEventBus();"  );
      writePresenterProvider(logger, ctx, writer, proxyCodeSplitAnnotation, 
          proxyCodeSplitBundleAnnotation, ginjectorClass, ginjectorClassName, 
          presenterClass, presenterClassName);

      writeSlotHandlers(logger, ctx, proxyCodeSplitAnnotation,
          proxyCodeSplitBundleAnnotation, presenterClass, presenterClassName,
          ginjectorClassName, ginjectorClass, writer);        
    }
    else {

      // Place proxy

      writeGinjector(writer, ginjectorClassName);
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
    writer.println( ginjectorClassName + " ginjector = (" + ginjectorClassName + ")baseGinjector;"  );
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


  /**
   * Make sure all the required base type information is known.
   * 
   * @param ctx The generator context.
   */
  private void findBaseTypes( GeneratorContext ctx ) {
    TypeOracle oracle = ctx.getTypeOracle();

    // Find the required base types
    basePresenterClass = oracle.findType( basePresenterClassName );
    baseGinjectorClass = oracle.findType( baseGinjectorClassName );
    typeClass = oracle.findType( typeClassName );
    revealContentHandlerClass = oracle.findType( revealContentHandlerClassName );
    requestTabsHandlerClass = oracle.findType( requestTabsHandlerClassName );
    providerClass = oracle.findType( providerClassName );
    asyncProviderClass = oracle.findType( asyncProviderClassName );
    basePlaceClass = oracle.findType( basePlaceClassName );
    tabContentProxyClass = oracle.findType( tabContentProxyClassName );
  }

}
