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

import com.google.inject.Singleton;

import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;

/**
 * A singleton presenter, one of the basic building block of the <a href=
 * "http://code.google.com/intl/nl/events/io/2009/sessions/GoogleWebToolkitBestPractices.html"
 * > model-view-presenter</a> architecture. Each page in your application should
 * correspond to a singleton {@link Presenter} and it should have an
 * accompanying singleton {@link View} and {@link Proxy}.
 * 
 * @param <V> The {@link View} type.
 * @param <Proxy_> The {@link Proxy} type.
 * 
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
@Singleton
public abstract class Presenter<V extends View, Proxy_ extends Proxy<?>> extends PresenterWidget<V> {
  /**
   * The light-weight {@PresenterProxy} around this presenter.
   */
  private final Proxy_ proxy;
  
  public Presenter(EventBus eventBus, V view, Proxy_ proxy) {
    super(eventBus, view);
    this.proxy = proxy;
  }
  
  /**
   * Returns the {@link Proxy} for the current presenter.
   * 
   * @return The proxy.
   */
  public final Proxy_ getProxy() {
    return proxy;
  }

  /**
   * This method can be used to reveal any presenter. This call will go up the
   * hierarchy, revealing any parent of this presenter. This method will also
   * bypass the change confirmation mechanism (see
   * {@link PlaceManager#setOnLeaveConfirmation(String)}).
   * 
   * <b>Important:</b> If you're using this method to reveal a place, it will
   * not update the browser history. Consider using
   * {@link PlaceManager#revealPlace(PlaceRequest)}.
   */
  public final void forceReveal() {
    if (isVisible()) {
      return;
    }
    
    revealInParent();
  }

  /**
   * This method is called when a {@link Presenter} should prepare itself based
   * on a {@link PlaceRequest}. The presenter should extract any parameters it
   * needs from the request. A presenter should override this method if it
   * handles custom parameters, but it should call the parent's
   * {@code prepareFromRequest} method.
   * 
   * @param request The request.
   */
  public void prepareFromRequest(PlaceRequest request) {
  }

  /**
   * This method is called when creating a {@link PlaceRequest} for this
   * {@link Presenter}. The presenter should add all the required parameters to
   * the request.
   * <p/>
   * <p/>
   * If nothing is to be done, simply return the {@code request} unchanged.
   * Otherwise, call {@link PlaceRequest#with(String, String)} to add
   * parameters. Eg:
   * <p/>
   * 
   * <pre>
   * return request.with( &quot;id&quot;, getId() );
   * </pre>
   * <p/>
   * A presenter should override this method if it handles custom parameters,
   * but it should call the parent's {@code prepareRequest} method.
   * 
   * @param request The current request.
   * @return The prepared place request.
   */
  public PlaceRequest prepareRequest(PlaceRequest request) {
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
