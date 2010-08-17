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

import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;

/**
 * @param <V> The {@link View} type.
 * @param <Proxy_> The {@link Proxy} type.
 * 
 * @author Philippe Beaudoin
 */
public abstract class PresenterImpl<V extends View, Proxy_ extends Proxy<?>>
    extends PresenterWidgetImpl<V> implements Presenter<V> {

  /**
   * The light-weight {@PresenterProxy} around this presenter.
   */
  private final Proxy_ proxy;

  /**
   * Creates a basic {@link Presenter}.
   * 
   * @param eventBus The event bus.
   * @param view The view attached to this presenter.
   * @param proxy The presenter proxy.
   */
  public PresenterImpl(final EventBus eventBus, final V view, final Proxy_ proxy) {
    super(eventBus, view);
    this.proxy = proxy;
  }

  @Override
  public final void forceReveal() {
    if (isVisible()) {
      return;
    }
    revealInParent();
  }

  @Override
  public final Proxy_ getProxy() {
    return proxy;
  }

  @Override
  public void prepareFromRequest(PlaceRequest request) {
    // By default, no parameter to extract from request.
  }

  @Override
  public PlaceRequest prepareRequest(PlaceRequest request) {
    // By default, no parameter to add to request
    return request;
  }

  /**
   * <b>Deprecated!</b> This method will soon be removed from the API. For more
   * information see <a
   * href="http://code.google.com/p/gwt-platform/issues/detail?id=136">Issue
   * 136</a>.
   * <p />
   * Notify others that this presenter has been changed. This is especially
   * useful for stateful presenters that store parameters within the history
   * token. Calling this will make sure the history token is updated with the
   * right parameters.
   */
  @Deprecated
  protected final void notifyChange() {
    getProxy().onPresenterChanged(this);
  }

  @Override
  protected void onReset() {
    super.onReset();
    getProxy().onPresenterRevealed(this);
  }

  /**
   * Called whenever the presenter needs to set its content in a parent. You
   * need to override this method. You should usually fire a
   * {@link RevealContentEvent}.
   */
  protected abstract void revealInParent();

}
