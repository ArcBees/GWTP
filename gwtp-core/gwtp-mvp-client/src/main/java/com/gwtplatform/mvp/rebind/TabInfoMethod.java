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
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.annotations.TabInfo;

/**
 * Represents a static method, in the presenter, that returns tab information.
 *
 * @author Philippe Beaudoin
 */
public class TabInfoMethod {
  private final TreeLogger logger;
  private final ClassCollection classCollection;
  private final GinjectorInspector ginjectorInspector;
  private final PresenterInspector presenterInspector;

  private TabInfo annotation;
  private Integer tabPriority;
  private String functionName;
  private boolean hasGingectorParam;
  private boolean returnString; // Either returns a String or a TabInfo

  public TabInfoMethod(TreeLogger logger,
      ClassCollection classCollection,
      GinjectorInspector ginjectorInspector,
      PresenterInspector presenterInspector) {
    this.logger = logger;
    this.classCollection = classCollection;
    this.ginjectorInspector = ginjectorInspector;
    this.presenterInspector = presenterInspector;
  }

  public void init(JMethod method) throws UnableToCompleteException {
    annotation = method.getAnnotation(TabInfo.class);
    assert annotation != null;
    tabPriority = annotation.priority();
    if (tabPriority < 0) {
      tabPriority = null;
    }

    functionName = method.getName();
    if (!method.isStatic()) {
      logger.log(TreeLogger.ERROR, getErrorPrefix() + " is not static. This is not supported.");
      throw new UnableToCompleteException();
    }

    if (annotation.label().length() != 0) {
      logger.log(TreeLogger.ERROR, getErrorPrefix()
          + ". The annotation defines a 'label', this is not permitted.", null);
      throw new UnableToCompleteException();
    }

    JClassType classReturnType = method.getReturnType().isClassOrInterface();
    if (classReturnType == classCollection.stringClass) {
      returnString = true;
    } else if (classReturnType.isAssignableFrom(classCollection.tabDataClass)) {
      returnString = false;
    } else {
      logger.log(TreeLogger.ERROR, getErrorPrefix() + " must return either a String or a "
              + TabData.class.getSimpleName());
      throw new UnableToCompleteException();
    }

    JParameter[] parameters = method.getParameters();
    if (parameters.length > 1) {
      logger.log(TreeLogger.ERROR, getErrorPrefix()
          + " accepts more than one parameter. This is illegal.");
      throw new UnableToCompleteException();
    }

    if (parameters.length == 1) {
      JClassType parameterType = parameters[0].getType().isClassOrInterface();
      if (parameterType.isAssignableFrom(ginjectorInspector.getGinjectorClass())) {
          hasGingectorParam = true;
      } else {
        logger.log(TreeLogger.ERROR, getErrorPrefix() + " has a parameter that is not of type "
                + ginjectorInspector.getGinjectorClassName() + ". This is illegal.");
        throw new UnableToCompleteException();
      }
    }

    if (tabPriority != null && !returnString) {
      logger.log(TreeLogger.ERROR, getErrorPrefix()
          + ". The annotation defines a 'tabPriority' but the method returns TabData, "
          + "this is not permitted.", null);
      throw new UnableToCompleteException();
    }
    if (tabPriority == null && returnString) {
      logger.log(TreeLogger.ERROR, getErrorPrefix()
          + ". The annotation does not define a 'tabPriority' but the method returns a String, "
          + "this is not permitted.", null);
      throw new UnableToCompleteException();
    }
  }

  private String getErrorPrefix() {
    return "In presenter " + presenterInspector.getPresenterClassName()
        + ", method " + functionName + " annotated with @" + TabInfo.class.getSimpleName();
  }

  /**
   * Writes the {@code getTabDataInternal} method definition in the generated proxy.
   *
   * @param writer The {@code SourceWriter}
   */
  public void writeGetTabDataInternalMethod(SourceWriter writer) {
    if (returnString) {
      // Presenter static method returning a string
      assert tabPriority != null;
      writer.println();
      writer.println("protected TabData getTabDataInternal("
          + ginjectorInspector.getGinjectorClassName() + " ginjector) {");
      writer.indent();
      writer.print("  return new TabDataBasic(");
      writer.print(presenterInspector.getPresenterClassName() + ".");
      writeTabInfoFunctionCall(writer);
      writer.println(", " + tabPriority + ");");
      writer.outdent();
      writer.println("}");
    } else {
      // Presenter static method returning tab data
      writer.println();
      writer.println("protected TabData getTabDataInternal("
          + ginjectorInspector.getGinjectorClassName() + " ginjector) {");
      writer.indent();
      writer.print("  return ");
      writer.print(presenterInspector.getPresenterClassName() + ".");
      writeTabInfoFunctionCall(writer);
      writer.println(";");
      writer.outdent();
      writer.println("}");
    }
  }

  private void writeTabInfoFunctionCall(SourceWriter writer) {
    writer.print(functionName + "( ");
    if (hasGingectorParam) {
      writer.print("ginjector");
    }
    writer.print(")");
  }

  public String getTabContainerClassName() {
    return annotation.container().getCanonicalName();
  }

  public String getNameToken() {
    return annotation.nameToken();
  }
}