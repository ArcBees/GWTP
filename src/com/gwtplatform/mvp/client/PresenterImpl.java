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

package com.gwtplatform.mvp.client;

import com.gwtplatform.mvp.client.proxy.Proxy;

/**
 * Use {@link Presenter} instead.
 * 
 * @param <V> The {@link View} type.
 * @param <Proxy_> The {@link Proxy} type.
 * 
 * @author Philippe Beaudoin
 */
@Deprecated
public abstract class PresenterImpl<V extends View, Proxy_ extends Proxy<?>>
    extends Presenter<V, Proxy_> {
  /**
   * Creates a basic {@link Presenter}.
   * 
   * @param eventBus The event bus.
   * @param view The view attached to this presenter.
   * @param proxy The presenter proxy.
   */
  public PresenterImpl(final EventBus eventBus, final V view, final Proxy_ proxy) {
    super(eventBus, view, proxy);
  }
}
