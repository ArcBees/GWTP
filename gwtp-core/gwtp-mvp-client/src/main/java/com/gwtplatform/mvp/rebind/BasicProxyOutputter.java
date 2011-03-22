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
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * TODO Document
 *
 * @author Philippe Beaudoin
 */
public class BasicProxyOutputter extends ProxyOutputterBase {

  public BasicProxyOutputter(TypeOracle oracle,
      TreeLogger logger,
      ClassCollection classCollection,
      GinjectorInspector ginjectorInspector,
      PresenterInspector presenterInspector,
      ProxyOutputterBase parent) {
    super(oracle, logger, classCollection, ginjectorInspector, presenterInspector, parent);
  }

  @Override
  void initSubclass(JClassType proxyInterface) throws UnableToCompleteException {
  }

  @Override
  void addSubclassImports(ClassSourceFileComposerFactory composerFactory) {
  }

  @Override
  public String getSuperclassName() {
    return ClassCollection.proxyImplClassName;
  }

  @Override
  public String getWrappedProxySuperclassName() {
    return null;
  }

  @Override
  public String getNameToken() {
    return null;
  }

  @Override
  public void writeGetTabDataInternalMethod(SourceWriter writer) {
  }

  @Override
  public void writeRequestTabHandler(SourceWriter writer)
      throws UnableToCompleteException {
  }

  @Override
  public String getPlaceInstantiationString() {
    return null;
  }

  @Override
  public void writeGetPlaceTitleMethod(SourceWriter writer) {
  }
}
