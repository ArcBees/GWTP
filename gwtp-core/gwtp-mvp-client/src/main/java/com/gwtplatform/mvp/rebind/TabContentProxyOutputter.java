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
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.annotations.RequestTabs;
import com.gwtplatform.mvp.client.annotations.TabInfo;

/**
 * TODO Document
 *
 * @author Philippe Beaudoin
 */
public class TabContentProxyOutputter extends ProxyOutputterBase {

  private JClassType tabContainerClass;
  private String tabContainerClassName;
  private Integer tabPriority;
  private String tabLabel;
  private TabInfoFunctionDescription tabInfoFunctionDescription;
  private String targetNameToken;
  private String nameToken;
  private String requestTabFieldName;

  public TabContentProxyOutputter(TypeOracle oracle,
      TreeLogger logger,
      ClassCollection classCollection,
      GinjectorInspector ginjectorInspector,
      PresenterInspector presenterInspector) {
    super(oracle, logger, classCollection, ginjectorInspector, presenterInspector);
  }

  @Override
  public void writeInnerClasses(SourceWriter writer) {
  }

  @Override
  void writeSubclassMethods(SourceWriter writer) {
    writeGetTabDataInternalMethod(writer);
  }

  @Override
  void writeSubclassDelayedBind(SourceWriter writer) {
    writeRequestTabHandler(writer);
    presenterInspector.writeProviderAssignation(writer);
    presenterInspector.writeSlotHandlers(writer);
  }

  @Override
  String getSuperclassName() {
    return ClassCollection.tabContentProxyImplClassName;
  }

  public void setNameToken(String nameToken) {
    this.nameToken = nameToken;
  }

  @Override
  public String getNameToken() {
    return null;
  }

  private void writeGetTabDataInternalMethod(SourceWriter writer) {
    if (tabLabel != null) {
      // Simple string tab label
      writer.println();
      writer.println("protected TabData getTabDataInternal("
          + ginjectorInspector.getGinjectorClassName() + " ginjector) {");
      writer.indent();
      writer.println("  return new TabDataBasic(\"" + tabLabel + "\", " + tabPriority + ");");
      writer.outdent();
      writer.println("}");
    } else if (tabInfoFunctionDescription.returnString) {
      // Presenter static method returning a string
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

  private void writeRequestTabHandler(SourceWriter writer) {
    writer.println("requestTabsEventType = " + tabContainerClassName + "."
          + requestTabFieldName + ";");
    writer.println("tabData = getTabDataInternal(ginjector);");
    writer.println("targetHistoryToken = \"" + getTargetNameToken() + "\";");
    writer.println("addRequestTabsHandler();");
  }

  private void writeTabInfoFunctionCall(SourceWriter writer) {
    writer.print(tabInfoFunctionDescription.functionName + "( ");
    if (tabInfoFunctionDescription.hasGingectorParam) {
      writer.print("ginjector");
    }
    writer.print(")");
  }

  @Override
  // TODO Refactor
  void initSubclass(JClassType proxyInterface)
      throws UnableToCompleteException {
    TabInfo tabInfoAnnotation = proxyInterface.getAnnotation(TabInfo.class);
    tabInfoFunctionDescription =  presenterInspector.findTabInfoFunction();

    // Ensure @TabInfo is there exactly once
    if (tabInfoAnnotation != null && tabInfoFunctionDescription != null) {
      logger.log(TreeLogger.ERROR, "Presenter " + presenterInspector.getPresenterClassName()
          + " contains both a proxy and a method annotated with @' +"
          + TabInfo.class.getSimpleName() + ". This is illegal.", null);
      throw new UnableToCompleteException();
    }
    if (tabInfoFunctionDescription != null) {
      tabInfoAnnotation = tabInfoFunctionDescription.annotation;
    }
    if (tabInfoAnnotation == null) {
      logger.log(TreeLogger.ERROR, "The proxy for '" + presenterInspector.getPresenterClassName()
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
          + " in " + presenterInspector.getPresenterClassName() + " defines the 'label' parameter and"
          + " annotates a method, this is not permitted.", null);
      throw new UnableToCompleteException();
    }
    if (tabLabel == null && tabInfoFunctionDescription == null) {
      logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
          + " in " + presenterInspector.getPresenterClassName() + " does not define the 'label' parameter and"
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
          + " in " + presenterInspector.getPresenterClassName() + " defines the 'priority' parameter and"
          + " annotates a method returning TabData, this is not permitted.", null);
      throw new UnableToCompleteException();
    }
    if (tabPriority == null &&
        (tabInfoFunctionDescription == null || tabInfoFunctionDescription.returnString)) {
      logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
          + " in " + presenterInspector.getPresenterClassName() + " does not define the 'priority' parameter and"
          + " does not annotate a method returning TabData, this is not permitted.", null);
      throw new UnableToCompleteException();
    }

    // Find the container
    tabContainerClass = oracle.findType(tabInfoAnnotation.container().getCanonicalName());
    if (tabContainerClass == null) {
      logger.log(TreeLogger.ERROR, "The container '"
          + tabInfoAnnotation.container().getCanonicalName()
          + "' in the proxy annotation for '" + presenterInspector.getPresenterClassName()
          + "' was not found.", null);
      throw new UnableToCompleteException();
    }
    tabContainerClassName = tabContainerClass.getParameterizedQualifiedSourceName();

    // Find the name token to use when the tab is clicked
    if (tabInfoAnnotation.nameToken().length() > 0) {
      targetNameToken = tabInfoAnnotation.nameToken();
    }
    if (targetNameToken != null && nameToken != null) {
      logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
          + " in " + presenterInspector.getPresenterClassName() + " defines the 'nameToken' parameter but"
          + " its proxy is a place, this is not permitted.", null);
      throw new UnableToCompleteException();
    }
    if (targetNameToken == null && nameToken == null) {
      logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
          + " in " + presenterInspector.getPresenterClassName() + " does not define the 'nameToken' parameter and"
          + " its proxy is not a place, this is not permitted.", null);
      throw new UnableToCompleteException();
    }

    // Find the field annotated with @RequestTabs in the tabContainerClass
    requestTabFieldName = null;
    for (JField field : tabContainerClass.getFields()) {
      RequestTabs annotation = field.getAnnotation(RequestTabs.class);
      JParameterizedType parameterizedType = field.getType().isParameterized();
      if (annotation != null) {
        if (!field.isStatic()
            || parameterizedType == null
            || !parameterizedType.isAssignableTo(classCollection.typeClass)
            || !parameterizedType.getTypeArgs()[0].isAssignableTo(classCollection.requestTabsHandlerClass)) {
          logger.log(
              TreeLogger.ERROR,
              "Found the annotation @" + RequestTabs.class.getSimpleName() + " on the invalid field '"
                  + tabContainerClassName + "." + field.getName()
                  + "'. Field must be static and its type must be Type<RequestTabsHandler<?>>.", null);
          throw new UnableToCompleteException();
        }
        if (requestTabFieldName != null) {
          logger.log(
              TreeLogger.ERROR,
              "Found the annotation @" + RequestTabs.class.getSimpleName() + " on more than one field in '"
                  + tabContainerClassName + "'. This is not allowed.", null);
          throw new UnableToCompleteException();
        }
        requestTabFieldName = field.getName();
      }
    }
    if (requestTabFieldName == null) {
      logger.log(TreeLogger.ERROR, "Did not find any field annotated with @"
          + RequestTabs.class.getSimpleName() + " on the container '"
          + tabContainerClassName + "' while building proxy for presenter '"
          + presenterInspector.getPresenterClassName() + "'.", null);
      throw new UnableToCompleteException();
    }
  }

  @Override
  void addSubclassImports(ClassSourceFileComposerFactory composerFactory) {
  }

  private String getTargetNameToken() {
    if (targetNameToken != null) {
      return targetNameToken;
    }
    return nameToken;
  }
}
