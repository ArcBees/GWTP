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

import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Interface of classes that are able to output the code of a specific
 * type of proxy. Usually produced via {@link ProxyOutputterFactory}.
 *
 * @author Philippe Beaudoin
 */
public interface ProxyOutputter {

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
   * Write all the inner classes of the proxy.
   *
   * @param writer The {@link SourceWriter}.
   */
  void writeInnerClasses(SourceWriter writer);

  /**
   * Write all the empty constructor of the proxy.
   *
   * @param writer The {@link SourceWriter}.
   * @param className The class name, which will be the name of the constructor method.
   * @param registerDelayedBind {@code true} if the constructor should register this class towards the
   *        {@link com.gwtplatform.mvp.client.DelayedBindRegistry DelayedBindRegistry}, {@code false} otherwise.
   */
  void writeConstructor(SourceWriter writer, String className, boolean registerDelayedBind);

  /**
   * Write all the methods of the proxy, not including the constructor.
   *
   * @param writer The {@link SourceWriter}.
   */
  void writeMethods(SourceWriter writer);
}
