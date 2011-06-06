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
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * A {@link ProxyOutputter} that is at once a {@link ProxyPlaceOutputter} and a
 * {@link TabContentProxyOutputter}.
 *
 * @author Philippe Beaudoin
 */
public class TabContentProxyPlaceOutputter extends ProxyOutputterBase {

  private final ProxyPlaceOutputter proxyPlaceOutputter;
  private final TabContentProxyOutputter tabContentProxyOutputter;

  /**
   * Create a {@link TabContentProxyPlaceOutputter} based on the superclass passed as a parameter.
   */
  public TabContentProxyPlaceOutputter(TypeOracle oracle,
      TreeLogger logger,
      ClassCollection classCollection,
      GinjectorInspector ginjectorInspector,
      PresenterInspector presenterInspector,
      ProxyPlaceOutputter proxyPlaceOutputter,
      TabContentProxyOutputter tabContentProxyOutputter) {
    super(oracle, logger, classCollection, ginjectorInspector, presenterInspector);
    this.proxyPlaceOutputter = proxyPlaceOutputter;
    this.tabContentProxyOutputter = tabContentProxyOutputter;
  }

  @Override
  void initSubclass(JClassType proxyInterface) throws UnableToCompleteException {
    proxyPlaceOutputter.init(proxyInterface);
    tabContentProxyOutputter.setNameToken(proxyPlaceOutputter.getNameToken());
    tabContentProxyOutputter.init(proxyInterface);
  }

  @Override
  void addSubclassImports(ClassSourceFileComposerFactory composerFactory) {
    proxyPlaceOutputter.addSubclassImports(composerFactory);
    tabContentProxyOutputter.addSubclassImports(composerFactory);
  }

  @Override
  public void writeInnerClasses(SourceWriter writer) {
    proxyPlaceOutputter.beginWrappedProxy(writer, ClassCollection.tabContentProxyImplClassName);
    tabContentProxyOutputter.writeFields(writer);
    tabContentProxyOutputter.writeInnerClasses(writer);
    tabContentProxyOutputter.writeConstructor(writer, ProxyPlaceOutputter.WRAPPED_CLASS_NAME,
        false);
    tabContentProxyOutputter.writeMethods(writer);
    proxyPlaceOutputter.endWrappedProxy(writer);
  }

  @Override
  String getSuperclassName() {
    return ClassCollection.tabContentProxyPlaceImplClassName;
  }

  @Override
  void writeSubclassDelayedBind(SourceWriter writer) {
    proxyPlaceOutputter.writeSubclassDelayedBind(writer);
  }

  @Override
  void writeSubclassMethods(SourceWriter writer) {
    proxyPlaceOutputter.writeSubclassMethods(writer);
  }
}
