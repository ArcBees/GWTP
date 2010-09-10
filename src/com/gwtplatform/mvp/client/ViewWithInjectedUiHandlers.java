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

import com.google.inject.Provider;

/**
 * Base class for a {@link View} that is injected with a
 * {@link UiHandlers} interface. Use this class together with
 * {@link com.gwtplatform.mvp.client.gin.AbstractPresenterModule#bindUiHandlers()}. 
 * <p />
 * This class works only for singleton {@link Presenter} and {@link PresenterWidget}.
 * If the view is instantiated multiple times and attached to different presenters,
 * use {@link ViewWithUiHandlers} instead.
 * <p />
 * <b>Important!</b> Never call {@link #getUiHandlers()} inside your constructor,
 * this would cause an infinite loop since the presenter is not yet instantiated.
 * 
 * @param <C> Your {@link UiHandlers} interface type.
 * 
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public abstract class ViewWithInjectedUiHandlers<C extends UiHandlers> extends ViewImpl {
  private final Provider<C> uiHandlersProvider;

  /**
   * Access the {@link UiHandlers} associated with this {@link View}.
   * <p>
   * <b>Important!</b> Never call {@link #getUiHandlers()} inside your constructor,
 * this would cause an infinite loop since the presenter is not yet instantiated.
   * 
   * @return The {@link UiHandlers}.
   */
  protected C getUiHandlers() {
    return uiHandlersProvider.get();
  }

  /**
   * Constructor that initializes a {@link Provider} to your {@link UiHandlers}.
   * Inject your subclass' construction with this {@link Provider} and bind it with
   * {@link com.gwtplatform.mvp.client.gin.AbstractPresenterModule#bindUiHandlers()}. 
   * 
   * @param uiHandlersProvider A {@link Provider} for your {@link UiHandlers}.
   */
  public ViewWithInjectedUiHandlers(Provider<C> uiHandlersProvider) {
    this.uiHandlersProvider = uiHandlersProvider;
  }

}
