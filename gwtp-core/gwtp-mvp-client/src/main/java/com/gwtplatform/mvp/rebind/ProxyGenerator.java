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

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Philippe Beaudoin
 * @author Olivier Monaco
 */
public class ProxyGenerator extends Generator {

  private ClassCollection classCollection;
  private GinjectorInspector ginjectorInspector;
  private PresenterInspector presenterInspector;
  private ProxyOutputterFactory proxyOutputterFactory;

  @Override
  public String generate(TreeLogger logger, GeneratorContext ctx,
      String requestedClass) throws UnableToCompleteException {

    // Initializes dependencies
    TypeOracle oracle = ctx.getTypeOracle();
    classCollection = new ClassCollection(oracle);
    ginjectorInspector = new GinjectorInspector(classCollection, ctx, logger);
    presenterInspector = new PresenterInspector(oracle, logger, classCollection, ginjectorInspector);
    proxyOutputterFactory = new ProxyOutputterFactory(oracle, logger, classCollection,
        ginjectorInspector, presenterInspector);

    // Find the requested class
    JClassType proxyInterface = oracle.findType(requestedClass);

    if (proxyInterface == null) {
      logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
          + requestedClass + "'", null);

      throw new UnableToCompleteException();
    }

    // If it's not an interface it's a custom user-made proxy class. Don't use generator.
    if (proxyInterface.isInterface() == null) {
      return null;
    }

    // If it's not annotated don't use generator.
    if (!presenterInspector.init(proxyInterface)) {
      return null;
    }

    // Find the package, build the generated class name.
    JPackage interfacePackage = proxyInterface.getPackage();
    String packageName = interfacePackage == null ? "" : interfacePackage.getName();
    String implClassName = presenterInspector.getPresenterClassName()
        + proxyInterface.getSimpleSourceName() + "Impl";
    String generatedClassName = packageName + "." + implClassName;

    // Create the printWriter
    PrintWriter printWriter = ctx.tryCreate(logger, packageName, implClassName);
    if (printWriter == null) {
      // We've already created it, so nothing to do
      return generatedClassName;
    }

    ginjectorInspector.init();
    ProxyOutputter proxyOutputter = proxyOutputterFactory.create(proxyInterface);

    // Start composing the class
    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
        packageName, implClassName);
    proxyOutputter.initComposerFactory(composerFactory);

    // Get a source writer
    SourceWriter writer = composerFactory.createSourceWriter(ctx, printWriter);

    proxyOutputter.writeFields(writer);

    if (proxyOutputter.getNameToken() == null) {
      // Not a place proxy
      proxyOutputter.writeGetTabDataInternalMethod(writer);
    } else {
      // Place proxy

      // BEGIN Enclosed proxy class
      writer.println();
      writer.println("public static class WrappedProxy");
      writer.println("extends " + proxyOutputter.getWrappedProxySuperclassName() + "<"
          + presenterInspector.getPresenterClassName() + "> {");
      writer.indent();

      proxyOutputter.writeGetTabDataInternalMethod(writer);

      // Enclosed proxy constructor
      writer.println();
      writer.println("public WrappedProxy() {}");

      // BEGIN Enclosed proxy Bind method
      writer.println();
      writer.println("private void delayedBind(" + ginjectorInspector.getGinjectorClassName()
          + " ginjector) {");
      writer.indent();

      proxyOutputter.writeRequestTabHandler(writer);

      // Call ProxyImpl bind method.
      writer.println("bind(ginjector.getProxyFailureHandler(),ginjector.getEventBus());");

      presenterInspector.writeProvider(writer);
      presenterInspector.writeSlotHandlers(writer);
      writer.outdent();
      writer.println("}");

      // END Enclosed proxy class
      writer.outdent();
      writer.println("}");

      // Check if title override if needed

      // Simple string title
      proxyOutputter.writeGetPlaceTitleMethod(writer);
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
    writeGinjector(writer, ginjectorInspector.getGinjectorClassName());
    if (proxyOutputter.getNameToken() == null) {
      // Standard proxy (not a Place)

      proxyOutputter.writeRequestTabHandler(writer);

      // Call ProxyImpl bind method.
      writer.println("bind(ginjector.getProxyFailureHandler(),ginjector.getEventBus());");

      presenterInspector.writeProvider(writer);
      presenterInspector.writeSlotHandlers(writer);
    } else {

      // Place proxy

      // Call ProxyPlaceAbstract bind method.
      writer.println("bind(ginjector.getProxyFailureHandler(), ");
      writer.println("    ginjector.getPlaceManager(),");
      writer.println("    ginjector.getEventBus());");
      writer.println("WrappedProxy wrappedProxy = GWT.create(WrappedProxy.class);");
      writer.println("wrappedProxy.delayedBind( ginjector ); ");
      writer.println("proxy = wrappedProxy; ");
      writer.println("String nameToken = \"" + proxyOutputter.getNameToken() + "\"; ");
      writer.println("place = " +
          proxyOutputter.getPlaceInstantiationString());
    }

    proxyOutputter.writeAddHandlerForProxyEvents(writer);

    // END Bind method
    writer.outdent();
    writer.println("}");

    proxyOutputter.writeHandlerMethodsForProxyEvents(writer);

    writer.commit(logger);

    return generatedClassName;
  }

  /**
   * Writes a local ginjector variable to the source writer.
   */
  private void writeGinjector(SourceWriter writer, String ginjectorClassName) {
    writer.println("ginjector = (" + ginjectorClassName + ")baseGinjector;");
  }
}
