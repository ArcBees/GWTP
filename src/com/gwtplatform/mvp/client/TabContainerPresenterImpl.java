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

import com.google.gwt.event.shared.GwtEvent.Type;

import com.gwtplatform.mvp.client.proxy.TabContentProxy;

/**
 * A presenter that can display many tabs and the content of one of these tabs.
 * 
 * @param <V> The specific type of the {@link View}. Must implement
 *          {@link TabPanel}.
 * @param <Proxy_> The specific type of the proxy, must be a
 *          {@link TabContainerProxy}.
 * 
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
public abstract class TabContainerPresenterImpl<V extends View & TabPanel, Proxy_ extends TabContentProxy<?>>
    extends TabContainerPresenter<V, Proxy_> {
  /**
   * Create a presenter that can display many tabs and the content of one of
   * these tabs.
   * 
   * @param eventBus The {@link EventBus}.
   * @param view The {@link View}.
   * @param proxy The proxy, a {@link TabContainerProxy}.
   * @param tabContentSlot An opaque object identifying the slot in which the
   *          main content should be displayed.
   * @param requestTabsEventType The {@link Type} of the object to fire to
   *          identify all the displayed tabs.
   */
  public TabContainerPresenterImpl(final EventBus eventBus, final V view,
      final Proxy_ proxy, final Object tabContentSlot,
      final Type<RequestTabsHandler> requestTabsEventType) {
    super(eventBus, view, proxy, tabContentSlot, requestTabsEventType);
  }
}
