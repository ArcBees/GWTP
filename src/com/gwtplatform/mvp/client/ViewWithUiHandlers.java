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
 * Base class for a {@link View} that implements the {@link HasUiHandlers}
 * interface. You should always call {@link #setHandlers()} from your
 * {@link Presenter} when using {@link ViewWithUiHandlers}'s empty constructor.
 * 
 * You can also use {@link #ViewWithUiHandlers(Provider)} in conjunction with
 * {@link AbstractPresenterModule#bindUiHandlers()} and use a {@link Provider} in
 * your {@link View}. That way, you won't have to set anything manually.
 * 
 * @param <C> Your {@link UiHandlers} interface type.
 * 
 * @author Christian Goudreau
 */
public abstract class ViewWithUiHandlers<C extends UiHandlers> extends ViewImpl
    implements HasUiHandlers<C> {
  private C uiHandlers;

  protected C getUiHandlers() {
    return uiHandlers;
  }

  /**
   * You should always call {@link #setHandlers()} from your {@link Presenter}
   * when using {@link ViewWithUiHandlers}'s empty constructor.
   */
  public ViewWithUiHandlers() {
  }

  /**
   * Use {@link #ViewWithUiHandlers(Provider)} in conjunction with
   * {@link AbstractPresenterModule#bindUiHandlers()} and use a {@link Provider}
   * in your {@link View}. That way, you won't have to set anything manually.
   * 
   * @param uiHandlersProvider {@link UiHandlers} provider.
   */
  public ViewWithUiHandlers(Provider<C> uiHandlersProvider) {
    this.uiHandlers = uiHandlersProvider.get();
  }

  @Override
  public void setUiHandlers(C uiHandlers) {
    this.uiHandlers = uiHandlers;
  }
}
