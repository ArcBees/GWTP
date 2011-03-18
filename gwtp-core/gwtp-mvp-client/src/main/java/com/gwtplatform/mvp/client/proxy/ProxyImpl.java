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

package com.gwtplatform.mvp.client.proxy;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.mvp.client.Presenter;

/**
 * @author Philippe Beaudoin
 *
 * @param <P> The presenter's type.
 */
public class ProxyImpl<P extends Presenter<?, ?>> implements Proxy<P> {

  protected ProxyFailureHandler failureHandler;
  protected IndirectProvider<P> presenter;
  private EventBus eventBus;

  /**
   * Creates a Proxy class for a specific presenter.
   */
  public ProxyImpl() {
  }

  @Override
  public void getPresenter(AsyncCallback<P> callback) {
    presenter.get(callback);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void getRawPresenter(AsyncCallback<Presenter<?, ?>> callback) {
    presenter.get((AsyncCallback<P>) callback);
  }

  /**
   * Injects the various resources and performs other bindings.
   * <p />
   * Never call directly, it should only be called by GIN. Method injection is
   * used instead of constructor injection, because the latter doesn't work well
   * with GWT generators.
   *
   * @param failureHandler The {@link ProxyFailureHandler}.
   * @param eventBus The {@link EventBus}.
   */
  @Inject
  protected void bind(ProxyFailureHandler failureHandler, EventBus eventBus) {
    this.failureHandler = failureHandler;
    this.eventBus = eventBus;
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    eventBus.fireEventFromSource(event, this);
  }

  @Override
  public final EventBus getEventBus() {
    return eventBus;
  }
}
