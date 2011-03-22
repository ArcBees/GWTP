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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * A {@link ProxyOutputter} that can be used to combine together multiple
 * different proxy outputters. Use {@link #add} to add {@link ProxyOutputter}
 * to this composite. The superclass of the proxy and wrapped proxy corresponding to
 * this outputter is set at construction time and is returned by {@link #getSuperclassName}
 * and {@link #getWrappedProxySuperclassName}. All other method calls are resolved in
 * the order in which the proxy outputters were added.
 *
 * @author Philippe Beaudoin
 */
public class CompositeProxyOutputter extends ProxyOutputterBase {

  private final List<ProxyOutputterBase> proxyOutputters = new ArrayList<ProxyOutputterBase>();
  private final String superclassName;
  private final String wrappedProxySuperclassName;

  /**
   * Create a {@link CompositeProxyOutputter} based on the superclass passed as a parameter.
   */
  public CompositeProxyOutputter(TypeOracle oracle,
      TreeLogger logger,
      ClassCollection classCollection,
      GinjectorInspector ginjectorInspector,
      PresenterInspector presenterInspector,
      ProxyOutputterBase parent,
      String superclassName,
      String wrappedProxySuperclassName) {
    super(oracle, logger, classCollection, ginjectorInspector, presenterInspector, parent);
    this.superclassName = superclassName;
    this.wrappedProxySuperclassName = wrappedProxySuperclassName;
  }

  public void add(ProxyOutputterBase proxyOutputter) {
    proxyOutputters.add(proxyOutputter);
  }

  @Override
  void initSubclass(JClassType proxyInterface) throws UnableToCompleteException {
    for (ProxyOutputterBase proxyOutputter : proxyOutputters) {
      proxyOutputter.init(proxyInterface);
    }
  }

  @Override
  void addSubclassImports(ClassSourceFileComposerFactory composerFactory) {
    for (ProxyOutputterBase proxyOutputter : proxyOutputters) {
      proxyOutputter.addSubclassImports(composerFactory);
    }
  }

  @Override
  public String getSuperclassName() {
    return superclassName;
  }

  @Override
  public String getWrappedProxySuperclassName() {
    return wrappedProxySuperclassName;
  }
  @Override
  public String getNameToken() {
    for (ProxyOutputterBase proxyOutputter : proxyOutputters) {
      String nameToken = proxyOutputter.getNameToken();
      if (nameToken != null) {
        return nameToken;
      }
    }
    return null;
  }

  @Override
  public void writeGetTabDataInternalMethod(SourceWriter writer) {
    for (ProxyOutputterBase proxyOutputter : proxyOutputters) {
      proxyOutputter.writeGetTabDataInternalMethod(writer);
    }
  }

  @Override
  public void writeRequestTabHandler(SourceWriter writer)
      throws UnableToCompleteException {
    for (ProxyOutputterBase proxyOutputter : proxyOutputters) {
      proxyOutputter.writeRequestTabHandler(writer);
    }
  }

  @Override
  public String getPlaceInstantiationString() {
    for (ProxyOutputterBase proxyOutputter : proxyOutputters) {
      String result = proxyOutputter.getPlaceInstantiationString();
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  @Override
  public void writeGetPlaceTitleMethod(SourceWriter writer) {
    for (ProxyOutputterBase proxyOutputter : proxyOutputters) {
      proxyOutputter.writeGetPlaceTitleMethod(writer);
    }
  }
}
