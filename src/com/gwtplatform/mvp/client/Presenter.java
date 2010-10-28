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

import com.google.gwt.event.shared.EventBus;
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
   * Returns the {@link Proxy} for the current presenter.
   * 
   * @return The proxy.
   */
  public final Proxy_ getProxy() {
    return proxy;
  }

  /**
   * Verifies if this presenter can be revealed automatically or if it is meant to be
   * revealed manually.
   * Normally, the user wants to reveal a presenter manually when it cannot be used
   * until some data is received from the server. For example, a form
   * to edit client details is unusable until all the data for this user has been 
   * received. Fetching this data should be done in the {@link #prepareFromRequest(PlaceRequest)}
   * method.
   * <p />
   * In order to use manual reveal, override this method to return {@code true}.
   * Then you can either:
   * <ul>
   * <li> Fetch the data using a {@link com.gwtplatform.mvp.clien.proxy.ProxyPlace.ManualRevealCallback}, 
   *      which will automatically reveal the presenter upon success.</li>
   * <li> Fetch the data by any other mean and call 
   * {@link com.gwtplatform.mvp.client.proxy.ProxyPlace#manualReveal(Presenter)} when 
   * your data is available. In this case you also have to call 
   * {@link com.gwtplatform.mvp.client.proxy.ProxyPlace#manualRevealFailed(Presenter)} 
   * if loading fails, otherwise your application will become unusable.</li>
   * </li>
   * The default implementation uses automatic reveal, and therefore returns {@code false}.
   * 
   * @return {@code true} if you want to use manual reveal, or {@code false} to use
   *         automatic reveal.
   */
  public boolean useManualReveal() {
    return false;
  }

  /**
   * This method is called when a {@link Presenter} should prepare itself based
   * on a {@link PlaceRequest}. The presenter should extract any parameters it
   * needs from the request. A presenter should override this method if it
   * handles custom parameters, but it should call the parent's
   * {@code prepareFromRequest} method.
   * <p />
   * If your presenter needs to fetch some information from the server while
   * preparing itself, consider using manual reveal. See {@link #useManualReveal()}.
   * 
   * @param request The {@link PlaceRequest}.
   */
  public void prepareFromRequest(PlaceRequest request) {
  }  
  
  /**
   * Called whenever the presenter needs to set its content in a parent. You
   * need to override this method. You should usually fire a
   * {@link RevealContentEvent}.
   */
  protected abstract void revealInParent();
}
