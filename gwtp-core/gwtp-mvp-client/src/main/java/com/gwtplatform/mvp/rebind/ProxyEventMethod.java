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
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;

/**
 * Represents a method, in the presenter, that is responsible of handling an event.
 *
 * @author Philippe Beaudoin
 */
public class ProxyEventMethod {

  private final TreeLogger logger;
  private final ClassCollection classCollection;
  private final PresenterInspector presenterInspector;

  private String functionName;
  private String eventTypeName;
  private String handlerTypeName;
  private String handlerMethodName;

  private ClassInspector eventInspector;

  public ProxyEventMethod(TreeLogger logger,
      ClassCollection classCollection,
      PresenterInspector presenterInspector) {
    this.logger = logger;
    this.classCollection = classCollection;
    this.presenterInspector = presenterInspector;
  }

  public void init(JMethod method) throws UnableToCompleteException {

    ProxyEvent annotation = method.getAnnotation(ProxyEvent.class);
    assert annotation != null;

    functionName = method.getName();
    if (method.getReturnType().isPrimitive() != JPrimitiveType.VOID) {
      logger.log(TreeLogger.WARN, getErrorPrefix()
          + " returns something else than void. Return value will be ignored.");
    }

    if (method.getParameters().length != 1) {
      logger.log(TreeLogger.ERROR, getErrorPrefix()
              + " needs to have exactly 1 parameter of a type derived from GwtEvent.");
      throw new UnableToCompleteException();
    }

    JClassType eventType = method.getParameters()[0].getType().isClassOrInterface();
    if (eventType == null || !eventType.isAssignableTo(classCollection.gwtEventClass)) {
      logger.log(TreeLogger.ERROR, getErrorPrefix()
          + " must take a parameter extending " + ClassCollection.gwtEventClassName);
      throw new UnableToCompleteException();
    }

    eventInspector = new ClassInspector(logger, eventType);
    eventTypeName = eventType.getQualifiedSourceName();
    ensureStaticGetTypeMethodExists(eventType);

    JClassType handlerType = findHandlerType(eventType);
    handlerTypeName = handlerType.getQualifiedSourceName();

    JMethod handlerMethod = findHandlerMethod(handlerType);
    handlerMethodName = handlerMethod.getName();

    // Warn if handlerMethodName is different
    if (!handlerMethodName.equals(functionName)) {
      logger.log(TreeLogger.WARN, getErrorPrefix(eventTypeName, handlerTypeName)
          + ". The handler method '" + handlerMethodName + "' differs from the annotated method '"
          + functionName + ". You should use the same method name for easier reference.");
    }
  }

  private JMethod findHandlerMethod(JClassType handlerType)
      throws UnableToCompleteException {
    if (handlerType.getMethods().length != 1) {
      logger.log(TreeLogger.ERROR, getErrorPrefix(eventTypeName, handlerTypeName)
          + ", but the handler interface has more than one method.");
      throw new UnableToCompleteException();
    }

    JMethod handlerMethod = handlerType.getMethods()[0];
    if (handlerMethod.getReturnType().isPrimitive() != JPrimitiveType.VOID) {
      logger.log(TreeLogger.WARN, getErrorPrefix(eventTypeName, handlerTypeName)
          + ", but the handler's method does not return void. Return value will be ignored.");
    }
    return handlerMethod;
  }

  private JClassType findHandlerType(JClassType eventType)
      throws UnableToCompleteException {
    JMethod eventMethod = eventInspector.findMethod("dispatch",
        classCollection.eventHandlerClass);
    if (eventMethod == null) {
      logger.log(TreeLogger.ERROR, getErrorPrefix(eventType.getName())
          + ", but the event class has no valid 'dispatch' method.");
      throw new UnableToCompleteException();
    }
    return eventMethod.getParameters()[0].getType().isClassOrInterface();
  }

  private void ensureStaticGetTypeMethodExists(JClassType eventType)
      throws UnableToCompleteException {
    JMethod getTypeMethod = eventType.findMethod("getType", new JType[0]);
    if (getTypeMethod == null
        || !getTypeMethod.isStatic()
        || getTypeMethod.getParameters().length != 0) {
      logger.log(TreeLogger.ERROR, getErrorPrefix(eventType.getName())
          + ", but this event class does not have a static getType method with no parameters.");
      throw new UnableToCompleteException();
    }

    JClassType getTypeReturnType = getTypeMethod.getReturnType().isClassOrInterface();
    if (getTypeReturnType == null
        || !classCollection.gwtEventTypeClass.isAssignableFrom(getTypeReturnType)) {
      logger.log(TreeLogger.ERROR, getErrorPrefix(eventType.getName())
          + ", but this event class getType() method does not return on object of type "
          + ClassCollection.gwtEventTypeClassName);
      throw new UnableToCompleteException();
    }
  }

  private String getErrorPrefix() {
    return "In presenter " + presenterInspector.getPresenterClassName()
        + ", method " + functionName + " annotated with @" + ProxyEvent.class.getSimpleName();
  }

  private String getErrorPrefix(String eventTypeName) {
    return getErrorPrefix() + " refers to event " + eventTypeName;
  }

  private String getErrorPrefix(String eventTypeName, String handlerTypeName) {
    return getErrorPrefix(eventTypeName) + " with handler " + handlerTypeName;
  }

  /**
   * Ensures that this method does not clash with the one passed as parameters.
   * Logs an error and throws {@link UnableToCompleteException} if a clash occurs.
   *
   * @param previousMethod The method to check against.
   * @throws UnableToCompleteException If a clash is observed.
   */
  public void ensureNoClashWith(ProxyEventMethod previousMethod)
      throws UnableToCompleteException {
    if (previousMethod.handlerMethodName.equals(handlerMethodName)
        && previousMethod.eventTypeName.equals(eventTypeName)) {
      logger.log(TreeLogger.ERROR, getErrorPrefix()
          + ". The handler method " + handlerMethodName + " is already used by method "
          + previousMethod.functionName + ".");
      throw new UnableToCompleteException();
    }
  }

  /**
   * Ensures the class being built by the {@link ClassSourceFileComposerFactory} implements the
   * interface required to handle this event.
   *
   * @param composerFactory The composer factory.
   */
  public void addImplementedInterface(
      ClassSourceFileComposerFactory composerFactory) {
    composerFactory.addImplementedInterface(handlerTypeName);
  }

  /**
   * Writes the call required to ensure the proxy handles this event.
   *
   * @param writer The {@link SourceWriter}.
   */
  public void writeAddHandler(SourceWriter writer) {
    writer.println("getEventBus().addHandler( " + eventTypeName + ".getType(), this );");
  }

  /**
   * Writes the definition of the method responsible of handling this event.
   *
   * @param writer The {@link SourceWriter}.
   */
  public void writeHandlerMethod(SourceWriter writer) {
    writer.println("");
    writer.println("@Override");
    writer.println("public final void " + handlerMethodName + "( final " + eventTypeName
        + " event ) {");
    writer.indent();
    writer.println("getPresenter( new NotifyingAsyncCallback<" + presenterInspector.getPresenterClassName()
        + ">(getEventBus()) {");
    writer.indent();
    writer.println("@Override");
    writer.println("public void success(final " + presenterInspector.getPresenterClassName()
        + " presenter) {");
    writer.indent();
    writer.println("Scheduler.get().scheduleDeferred( new Command() {");
    writer.indent();
    writer.println("@Override");
    writer.println("public void execute() {");
    writer.indent();
    writer.println("presenter." + functionName + "( event );");
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
}