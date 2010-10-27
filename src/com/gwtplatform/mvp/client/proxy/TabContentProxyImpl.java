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
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.RequestTabsEvent;
import com.gwtplatform.mvp.client.RequestTabsHandler;
import com.gwtplatform.mvp.client.Tab;

/**
 * @param <T> The Presenter's type.
 * 
 * @author Philippe Beaudoin
 */
public class TabContentProxyImpl<T extends Presenter<?, ?>> extends ProxyImpl<T>
    implements TabContentProxy<T> {

  protected String historyToken;
  protected String label;
  protected float priority;
  protected Type<RequestTabsHandler> requestTabsEventType;

  private Tab tab;

  /**
   * Creates a {@link Proxy} for a {@link Presenter} that is meant to be
   * contained within at {@link TabContainerPresenter}. As such, these proxy
   * hold a string that can be displayed on the tab.
   */
  public TabContentProxyImpl() {
  }

  @Override
  public String getHistoryToken() {
    return historyToken;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public float getPriority() {
    return priority;
  }

  @Override
  public Tab getTab() {
    return tab;
  }
  
  @Inject
  @Override
  protected void bind(ProxyFailureHandler failureHandler, EventBus eventBus) {
    super.bind(failureHandler, eventBus);
    eventBus.addHandler(requestTabsEventType, new RequestTabsHandler() {
      @Override
      public void onRequestTabs(RequestTabsEvent event) {
        tab = event.getTabContainer().addTab(TabContentProxyImpl.this);
      }
    });
  }

}
