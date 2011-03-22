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

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * TODO Document
 *
 * @author Philippe Beaudoin
 */
public interface ProxyOutputter {

  /**
   * Initializes this proxy outputter given the specified proxy interface.
   */
  void init(JClassType proxyInterface) throws UnableToCompleteException;

  /**
   * Initializes the composer factory, adding all the imports, setting the
   * implemented interfaces and extending the correct superclass.
   *
   * @param composerFactory The composer factory used to generate to proxy.
   */
  void initComposerFactory(ClassSourceFileComposerFactory composerFactory);

  /**
   * Write all the fields of the proxy.
   *
   * @param writer The {@link SourceWriter}.
   */
  void writeFields(SourceWriter writer);

  /**
   * Access the name of the superclass from which the wrapped proxy implementation
   * should inherit.
   *
   * @return The name of the superclass for the wrapped proxy.
   */
  String getWrappedProxySuperclassName();

  /**
   * Writes all the calls to {@code addHandler} needed to register all the
   * proxy events.
   *
   * @param writer The {@link SourceWriter}.
   */
  void writeAddHandlerForProxyEvents(SourceWriter writer);

  /**
   * Writes all the handlers needed to handle the proxy events.
   *
   * @param writer The {@link SourceWriter}.
   */
  void writeHandlerMethodsForProxyEvents(SourceWriter writer);

  /**
   * Writes the method {@code protected void getPlaceTitle(final GetPlaceTitleEvent event)} if
   * one is needed.
   *
   * @param writer The {@link SourceWriter}.
   */
  void writeGetPlaceTitleMethod(SourceWriter writer);

  /**
   * Access the name token associated with this proxy.
   * TODO Try to remove this.
   *
   * @return The name token, {@code null} if none exists.
   */
  String getNameToken();

  /**
   * TODO Remove this.
   */
  void writeGetTabDataInternalMethod(SourceWriter writer);

  /**
   * TODO Remove this.
   */
  void writeRequestTabHandler(SourceWriter writer) throws UnableToCompleteException;

  /**
   * TODO Remove this.
   */
  String getPlaceInstantiationString();


}
