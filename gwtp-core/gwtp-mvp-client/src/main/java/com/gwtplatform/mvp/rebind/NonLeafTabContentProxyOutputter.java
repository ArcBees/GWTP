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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.annotations.ChangeTab;
import com.gwtplatform.mvp.client.annotations.RequestTabs;
import com.gwtplatform.mvp.client.annotations.TabInfo;

/**
 * Proxy outputter for a proxy corresponding to a presenter which is displayed within a
 * {@link com.gwtplatform.mvp.client.TabContainerPresenter TabContainerPresenter}.
 *
 * @author Philippe Beaudoin
 */
public class NonLeafTabContentProxyOutputter extends ProxyOutputterBase {

  private JClassType tabContainerClass;
  private String tabContainerClassName;
  private Integer tabPriority;
  private String tabLabel;
  private TabInfoMethod tabInfoMethod;
  private String targetNameToken;
  private String nameToken;
  private String requestTabsFieldName;
  private String changeTabFieldName;

  public NonLeafTabContentProxyOutputter(TypeOracle oracle,
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
    presenterInspector.writeContentSlotHandlerRegistration(writer);
  }

  @Override
  String getSuperclassName() {
    return ClassCollection.nonLeafTabContentProxyImplClassName;
  }

  public void setNameToken(String nameToken) {
    this.nameToken = nameToken;
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
    } else {
      tabInfoMethod.writeGetTabDataInternalMethod(writer);
    }
  }

  private void writeRequestTabHandler(SourceWriter writer) {
    writer.println("requestTabsEventType = " + tabContainerClassName + "."
        + requestTabsFieldName + ";");
    if (changeTabFieldName != null) {
      writer.println("changeTabEventType = " + tabContainerClassName + "."
          + changeTabFieldName + ";");
    }
    writer.println("tabData = getTabDataInternal(ginjector);");
    writer.println("targetHistoryToken = \"" + getTargetNameToken() + "\";");
    writer.println("addRequestTabsHandler();");
  }

  @Override
  void initSubclass(JClassType proxyInterface) throws UnableToCompleteException {
    tabInfoMethod = presenterInspector.findTabInfoMethod();

    TabInfo tabInfoAnnotation = proxyInterface.getAnnotation(TabInfo.class);
    ensureExactlyOneTabInfoAnnotation(tabInfoAnnotation);

    tabPriority = null;
    tabLabel = null;
    if (tabInfoMethod == null) {
      findTabPriority(tabInfoAnnotation);
      findTabLabel(tabInfoAnnotation);
      targetNameToken = tabInfoAnnotation.nameToken();
      tabContainerClassName = tabInfoAnnotation.container().getCanonicalName();
    } else {
      targetNameToken = tabInfoMethod.getNameToken();
      tabContainerClassName = tabInfoMethod.getTabContainerClassName();
    }
    ensureNameTokenIsValid();
    findTabContainerClass();
    findRequestTabsFieldName();
    findChangeTabFieldName();
  }

  private void findRequestTabsFieldName() throws UnableToCompleteException {
    ClassInspector tabContainerInspector = new ClassInspector(logger, tabContainerClass);
    List<JField> requestTabsFields = new ArrayList<JField>();
    tabContainerInspector.collectStaticAnnotatedFields(classCollection.typeClass,
        classCollection.requestTabsHandlerClass, RequestTabs.class, requestTabsFields);
    if (requestTabsFields.size() == 0) {
      logger.log(TreeLogger.ERROR, "Did not find any static field annotated with @"
          + RequestTabs.class.getSimpleName() + " on the container '"
          + tabContainerClassName + "' while building proxy for presenter '"
          + presenterInspector.getPresenterClassName() + "'.", null);
      throw new UnableToCompleteException();
    }
    if (requestTabsFields.size() > 1) {
      logger.log(TreeLogger.ERROR, "Found the annotation @" + RequestTabs.class.getSimpleName()
          + " on more than one field in '" + tabContainerClassName
          + "'. This is not allowed.", null);
      throw new UnableToCompleteException();
    }
    requestTabsFieldName = requestTabsFields.get(0).getName();
  }

  private void findChangeTabFieldName() throws UnableToCompleteException {
    ClassInspector tabContainerInspector = new ClassInspector(logger, tabContainerClass);
    List<JField> changeTabFields = new ArrayList<JField>();
    tabContainerInspector.collectStaticAnnotatedFields(classCollection.typeClass,
        classCollection.changeTabHandlerClass, ChangeTab.class, changeTabFields);
    if (changeTabFields.size() > 1) {
      logger.log(TreeLogger.ERROR, "Found the annotation @" + ChangeTab.class.getSimpleName()
          + " on more than one field in '" + tabContainerClassName
          + "'. This is not allowed.", null);
      throw new UnableToCompleteException();
    }
    if (changeTabFields.size() == 0) {
      changeTabFieldName = null;
    } else {
      changeTabFieldName = changeTabFields.get(0).getName();
    }
  }

  private void findTabContainerClass() throws UnableToCompleteException {
    if (tabContainerClassName == null) {
      logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
            + " in " + presenterInspector.getPresenterClassName()
            + " must define the 'container' parameter.", null);
      throw new UnableToCompleteException();
    }

    tabContainerClass = oracle.findType(tabContainerClassName);
    if (tabContainerClass == null) {
      logger.log(TreeLogger.ERROR, "The container '" + tabContainerClassName + "' in @"
          + TabInfo.class.getSimpleName() + " for '" + presenterInspector.getPresenterClassName()
          + "' was not found.", null);
      throw new UnableToCompleteException();
    }
  }

  private void ensureNameTokenIsValid() throws UnableToCompleteException {
    if (targetNameToken.length() == 0) {
      targetNameToken =  null;
    }

    if (targetNameToken != null && nameToken != null) {
      logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName() + " in "
          + presenterInspector.getPresenterClassName() + " defines the 'nameToken' parameter but "
          + "its proxy is a place, this is not permitted.", null);
      throw new UnableToCompleteException();
    }
    if (targetNameToken == null && nameToken == null) {
      logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName() + " in "
          + presenterInspector.getPresenterClassName() + " does not define the 'nameToken' "
          + "parameter and its proxy is not a place, this is not permitted.", null);
      throw new UnableToCompleteException();
    }
  }

  private void findTabLabel(TabInfo tabInfoAnnotation)
      throws UnableToCompleteException {
    if (tabInfoAnnotation.label().length() > 0) {
      tabLabel = tabInfoAnnotation.label();
    }

    if (tabLabel == null) {
      logger.log(TreeLogger.ERROR, "The @" + TabInfo.class.getSimpleName()
          + " annotating a proxy in " + presenterInspector.getPresenterClassName()
          + " must define the 'label' parameter.", null);
      throw new UnableToCompleteException();
    }
  }

  private void findTabPriority(TabInfo tabInfoAnnotation) {
    tabPriority = tabInfoAnnotation.priority();
    if (tabPriority < 0) {
      tabPriority = null;
    }
  }

  private void ensureExactlyOneTabInfoAnnotation(TabInfo tabInfoAnnotation)
      throws UnableToCompleteException {
    if (tabInfoAnnotation != null && tabInfoMethod != null) {
      logger.log(TreeLogger.ERROR, "Presenter " + presenterInspector.getPresenterClassName()
          + " contains both a proxy and a method annotated with @' +"
          + TabInfo.class.getSimpleName() + ". This is illegal.", null);
      throw new UnableToCompleteException();
    }
    if (tabInfoAnnotation == null && tabInfoMethod == null) {
      logger.log(TreeLogger.ERROR, "The proxy for '" + presenterInspector.getPresenterClassName()
          + "' is a NonLeafTabContentProxy but is not annotated with @' +"
          + TabInfo.class.getSimpleName()
          + " and its presenter has no method annotated with it either.", null);
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