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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.Title;
import com.gwtplatform.mvp.client.annotations.TitleFunction;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.GetPlaceTitleEvent;

/**
 * TODO Document
 *
 * @author Philippe Beaudoin
 */
public class ProxyPlaceOutputter extends ProxyOutputterBase {

  private String nameToken;
  private String getGatekeeperMethod;

  private String title;
  private PresenterTitleMethod presenterTitleMethod;

  public ProxyPlaceOutputter(TypeOracle oracle,
      TreeLogger logger,
      ClassCollection classCollection,
      GinjectorInspector ginjectorInspector,
      PresenterInspector presenterInspector) {
    super(oracle, logger, classCollection, ginjectorInspector, presenterInspector);
  }

  @Override
  public void writeInnerClasses(SourceWriter writer) {
    beginWrappedProxy(writer, ClassCollection.proxyImplClassName);
    BasicProxyOutputter basicOutputter =
        new BasicProxyOutputter(oracle, logger, classCollection, ginjectorInspector, presenterInspector);
    basicOutputter.writeFields(writer);
    basicOutputter.writeInnerClasses(writer);
    basicOutputter.writeConstructor(writer, "WrappedProxy", false); // TODO Remove magic string
    basicOutputter.writeMethods(writer);
    endWrappedProxy(writer);
  }

  public void beginWrappedProxy(SourceWriter writer, String wrappedProxySuperclassName) {
    writer.println();
    writer.println("public static class WrappedProxy");
    writer.println("extends " + wrappedProxySuperclassName + "<" + presenterInspector.getPresenterClassName() + "> "
        + "implements " + ClassCollection.delayedBindClassName + " {");
    writer.indent();
  }

  public void endWrappedProxy(SourceWriter writer) {
    writer.outdent();
    writer.println("}");
  }

  @Override
  String getSuperclassName() {
    return ClassCollection.proxyPlaceImplClassName;
  }

  @Override
  public String getNameToken() {
    return nameToken;
  }

  @Override
  void initSubclass(JClassType proxyInterface)
      throws UnableToCompleteException {
    findNameToken(proxyInterface);
    findGatekeeperMethod(proxyInterface);
    findTitle(proxyInterface);
  }

  @Override
  void addSubclassImports(ClassSourceFileComposerFactory composerFactory) {
    if (title != null || presenterTitleMethod != null) {
      composerFactory.addImport(GetPlaceTitleEvent.class.getCanonicalName());
      composerFactory.addImport(AsyncCallback.class.getCanonicalName());
      composerFactory.addImport(Throwable.class.getCanonicalName());
    }
  }

  private void findNameToken(JClassType proxyInterface)
      throws UnableToCompleteException {
    NameToken nameTokenAnnotation = proxyInterface.getAnnotation(NameToken.class);
    if (nameTokenAnnotation == null) {
      logger.log(TreeLogger.ERROR,
          "The proxy for '" + presenterInspector.getPresenterClassName()
              + "' is a Place, but is not annotated with @' +"
              + NameToken.class.getSimpleName() + ".", null);
      throw new UnableToCompleteException();
    }
    nameToken = nameTokenAnnotation.value();
  }

  private void findGatekeeperMethod(JClassType proxyInterface)
      throws UnableToCompleteException {
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
      if (!customGatekeeperClass.isAssignableTo(classCollection.gatekeeperClass)) {
        logger.log(TreeLogger.ERROR, "The class '" + gatekeeperName
            + "' provided to @" + UseGatekeeper.class.getSimpleName()
            + " does not inherit from '" + ClassCollection.gatekeeperClassName + "'.", null);
        throw new UnableToCompleteException();
      }
      // Find the appropriate get method in the Ginjector
      getGatekeeperMethod = ginjectorInspector.findGetMethod(customGatekeeperClass);
      if (getGatekeeperMethod == null) {
        logger.log(TreeLogger.ERROR,
            "The Ginjector '" + ginjectorInspector.getGinjectorClassName()
                + "' does not have a get() method returning '"
                + gatekeeperName + "'. This is required when using @"
                + UseGatekeeper.class.getSimpleName() + ".", null);
        throw new UnableToCompleteException();
      }
    }
    if (getGatekeeperMethod == null
        && proxyInterface.getAnnotation(NoGatekeeper.class) == null) {
      // No Gatekeeper specified, see if there is a DefaultGatekeeper defined in the ginjector
      getGatekeeperMethod = ginjectorInspector.findAnnotatedGetMethod(
          classCollection.gatekeeperClass, DefaultGatekeeper.class, true);
    }
  }

  private void findTitle(JClassType proxyInterface)
      throws UnableToCompleteException {
    presenterTitleMethod = presenterInspector.findPresenterTitleMethod();
    Title titleAnnotation = proxyInterface.getAnnotation(Title.class);
    if (titleAnnotation != null) {
      title = titleAnnotation.value();
    }
    if (presenterTitleMethod != null && title != null) {
      logger.log(TreeLogger.ERROR, "The proxy for '" + presenterInspector.getPresenterClassName()
          + "' is annotated with @' +" + Title.class.getSimpleName()
          + " and its presenter has a method annotated with @"
          + TitleFunction.class.getSimpleName() + ". Only once can be used.",
          null);
      throw new UnableToCompleteException();
    }
  }

  private String getPlaceInstantiationString() {
    if (getGatekeeperMethod == null) {
      return "new " + ClassCollection.placeImplClassName + "( nameToken );";
    } else {
      return "new " + ClassCollection.placeWithGatekeeperClassName
          + "( nameToken, ginjector." + getGatekeeperMethod + "() );";
    }
  }

  /**
   * Writes the method {@code protected void getPlaceTitle(final GetPlaceTitleEvent event)} if
   * one is needed.
   *
   * @param writer The {@link SourceWriter}.
   */
  private void writeGetPlaceTitleMethod(SourceWriter writer) {
    if (title != null) {
      writeGetPlaceTitleMethodConstantText(writer);
    } else if (presenterTitleMethod != null) {
      presenterTitleMethod.writeProxyMethod(writer);
    }
  }

  private void writeGetPlaceTitleMethodConstantText(SourceWriter writer) {
    writer.println();
    writer.println("protected void getPlaceTitle(GetPlaceTitleEvent event) {");
    writer.indent();
    writer.println("event.getHandler().onSetPlaceTitle( \"" + title
        + "\" );");
    writer.outdent();
    writer.println("}");
  }

  @Override
  void writeSubclassDelayedBind(SourceWriter writer) {
    // TODO There is probably a way to assign directly proxy...
    writer.println("WrappedProxy wrappedProxy = GWT.create(WrappedProxy.class);");
    writer.println("wrappedProxy.delayedBind( ginjector ); ");
    writer.println("proxy = wrappedProxy; ");
    writer.println("String nameToken = \"" + getNameToken() + "\"; ");
    writer.println("place = " + getPlaceInstantiationString());
  }

  @Override
  void writeSubclassMethods(SourceWriter writer) {
    writeGetPlaceTitleMethod(writer);
  }

}
