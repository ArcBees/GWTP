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

package com.gwtplatform.mvp.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;

import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

/**
 * Configures the basic classes for presenter.
 */
public final class PresenterSetupModule extends AbstractGinModule {

  private final Class<? extends PlaceManager> placeManagerClass;

  private final Class<? extends TokenFormatter> tokenFormatterClass;

  public PresenterSetupModule(Class<? extends PlaceManager> placeManagerClass) {
    this(placeManagerClass, ParameterTokenFormatter.class);
  }

  public PresenterSetupModule(Class<? extends PlaceManager> placeManagerClass,
      Class<? extends TokenFormatter> tokenFormatterClass) {
    this.placeManagerClass = placeManagerClass;
    this.tokenFormatterClass = tokenFormatterClass;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PresenterSetupModule;
  }

  @Override
  public int hashCode() {
    return 19;
  }

  @Override
  protected void configure() {
    bind(TokenFormatter.class).to(tokenFormatterClass);

    bind(PlaceManager.class).to(placeManagerClass);
    bind(placeManagerClass).asEagerSingleton();
  }
}
