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

package com.gwtplatform.mvp.rebind;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.annotations.TitleFunction;

/**
 * Represents a method, in the presenter, that is responsible of returning the place title.
 *
 * @author Philippe Beaudoin
 */
public class PresenterTitleMethod {

  private final TreeLogger logger;
  private final ClassCollection classCollection;
  private final GinjectorInspector ginjectorInspector;
  private final PresenterInspector presenterInspector;

  private String functionName;
  private int gingectorParamIndex = -1;
  private boolean isStatic;
  private int placeRequestParamIndex = -1;
  private boolean returnString;
  private int setPlaceTitleHandlerParamIndex = -1;

  public PresenterTitleMethod(TreeLogger logger,
      ClassCollection classCollection, GinjectorInspector ginjectorInspector,
      PresenterInspector presenterInspector) {
    this.logger = logger;
    this.classCollection = classCollection;
    this.ginjectorInspector = ginjectorInspector;
    this.presenterInspector = presenterInspector;
  }

  public void init(JMethod method) throws UnableToCompleteException {

    functionName = method.getName();
    isStatic = method.isStatic();

    JClassType classReturnType = method.getReturnType().isClassOrInterface();
    if (classReturnType != null && classReturnType == classCollection.stringClass) {
      returnString = true;
    } else {
      returnString = false;

      JPrimitiveType primitiveReturnType = method.getReturnType().isPrimitive();
      if (primitiveReturnType == null
          || primitiveReturnType != JPrimitiveType.VOID) {
        logger.log(
            TreeLogger.WARN,
            "In presenter " + presenterInspector.getPresenterClassName()
                + ", method " + functionName + " annotated with @"
                + TitleFunction.class.getSimpleName()
                + " returns something else than void or String. "
                + "Return value will be ignored and void will be assumed.");
      }
    }

    int i = 0;
    for (JParameter parameter : method.getParameters()) {
      JClassType parameterType = parameter.getType().isClassOrInterface();
      if (parameterType.isAssignableFrom(ginjectorInspector.getGinjectorClass())
          && gingectorParamIndex == -1) {
        gingectorParamIndex = i;
      } else if (parameterType.isAssignableFrom(classCollection.placeRequestClass)
          && placeRequestParamIndex == -1) {
        placeRequestParamIndex = i;
      } else if (parameterType.isAssignableFrom(classCollection.setPlaceTitleHandlerClass)
          && setPlaceTitleHandlerParamIndex == -1) {
        setPlaceTitleHandlerParamIndex = i;
      } else {
        logger.log(TreeLogger.ERROR, "In presenter " +  presenterInspector.getPresenterClassName()
            + ", in method " + functionName + " annotated with @"
            + TitleFunction.class.getSimpleName() + ", parameter " + i
            + " is invalid. Method can have at most one parameter of type "
            + ginjectorInspector.getGinjectorClassName() + ", " + ClassCollection.placeRequestClassName
            + " or " + ClassCollection.setPlaceTitleHandlerClassName);
        throw new UnableToCompleteException();
      }
      i++;
    }

    if (returnString && setPlaceTitleHandlerParamIndex != -1) {
      logger.log(
          TreeLogger.ERROR,
          "In presenter " + presenterInspector.getPresenterClassName() + ", the method "
              + functionName + " annotated with @" + TitleFunction.class.getSimpleName()
              + " returns a string and accepts a " + ClassCollection.setPlaceTitleHandlerClassName
              + " parameter. This is not supported, you can have only one or the other.");
      throw new UnableToCompleteException();
    }

    if (!returnString && setPlaceTitleHandlerParamIndex == -1) {
      logger.log(TreeLogger.ERROR, "In presenter " + presenterInspector.getPresenterClassName()
          + ", the method " + functionName + " annotated with @"
          + TitleFunction.class.getSimpleName()
          + " doesn't return a string and doesn't accept a "
          + ClassCollection.setPlaceTitleHandlerClassName + " parameter. You need one or the other.");
      throw new UnableToCompleteException();
    }
  }

  public void writeProxyMethod(SourceWriter writer) {
    if (isStatic) {
      if (returnString) {
        writeProxyMethodStaticReturningString(writer);
      } else {
        writeProxyMethodStaticWithHandler(writer);
      }
    } else {
      if (returnString) {
        writeProxyMethodNonStaticReturnString(writer);
      } else {
        writeProxyMethodNonStaticWithHandler(writer);
      }
    }
  }

  private void writeProxyMethodStaticReturningString(SourceWriter writer) {
    writer.println();
    writer.println("protected void getPlaceTitle(GetPlaceTitleEvent event) {");
    writer.indent();
    writer.print("String title = " + presenterInspector.getPresenterClassName() + ".");
    writePresenterMethodCall(writer);
    writer.println();
    writer.println("event.getHandler().onSetPlaceTitle( title );");
    writer.outdent();
    writer.println("}");
  }

  private void writeProxyMethodStaticWithHandler(SourceWriter writer) {
    writer.println();
    writer.println("protected void getPlaceTitle(GetPlaceTitleEvent event) {");
    writer.indent();
    writer.print(presenterInspector.getPresenterClassName() + ".");
    writePresenterMethodCall(writer);
    writer.println();
    writer.println("}");
  }

  private void writeProxyMethodNonStaticReturnString(SourceWriter writer) {
    writer.println();
    writer.println("protected void getPlaceTitle(final GetPlaceTitleEvent event) {");
    writer.indent();
    writer.println("getPresenter( new NotifyingAsyncCallback<" + presenterInspector.getPresenterClassName()
        + ">(getEventBus()){");
    writer.indent();
    writer.indent();
    writer.println("public void success(" + presenterInspector.getPresenterClassName() + " p ) {");
    writer.indent();
    writer.print("String title = p.");
    writePresenterMethodCall(writer);
    writer.println();
    writer.println("event.getHandler().onSetPlaceTitle( title );");
    writer.outdent();
    writer.println(" }");
    writer.println("public void failure(Throwable t) { event.getHandler().onSetPlaceTitle(null); }");
    writer.outdent();
    writer.println("} );");
    writer.outdent();
    writer.println("}");
  }

  private void writeProxyMethodNonStaticWithHandler(SourceWriter writer) {
    writer.println();
    writer.println("protected void getPlaceTitle(final GetPlaceTitleEvent event) {");
    writer.indent();
    writer.println("getPresenter( new NotifyingAsyncCallback<" + presenterInspector.getPresenterClassName()
        + ">(getEventBus()){");
    writer.indent();
    writer.indent();
    writer.print("public void success(" + presenterInspector.getPresenterClassName()
        + " p ) { p.");
    writePresenterMethodCall(writer);
    writer.println(" }");
    writer.println("public void failure(Throwable t) { event.getHandler().onSetPlaceTitle(null); }");
    writer.outdent();
    writer.println("} );");
    writer.outdent();
    writer.println("}");
  }

  private void writePresenterMethodCall(SourceWriter writer) {
    writer.print(functionName + "( ");
    for (int i = 0; i < 3; ++i) {
      if (gingectorParamIndex == i) {
        if (i > 0) {
          writer.print(", ");
        }
        writer.print("ginjector");
      } else if (placeRequestParamIndex == i) {
        if (i > 0) {
          writer.print(", ");
        }
        writer.print("event.getRequest()");
      } else if (setPlaceTitleHandlerParamIndex == i) {
        if (i > 0) {
          writer.print(", ");
        }
        writer.print("event.getHandler()");
      }
    }
    writer.print(");");
  }
}