/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.philbeaudoin.gwtp.mvp.client.EventBus;
import com.philbeaudoin.gwtp.mvp.client.Presenter;
import com.philbeaudoin.gwtp.mvp.client.RequestTabsEvent;
import com.philbeaudoin.gwtp.mvp.client.RequestTabsHandler;
import com.philbeaudoin.gwtp.mvp.client.Tab;
import com.philbeaudoin.gwtp.mvp.client.TabContainerPresenter;

public class TabContentProxyImpl<P extends Presenter> 
extends ProxyImpl<P> implements TabContentProxy<P> {

  protected Type<RequestTabsHandler> requestTabsEventType;
  protected float priority;
  protected String label;
  protected String historyToken;

  private Tab tab = null;

  /**
   * Creates a {@link Proxy} for a {@link Presenter} that 
   * is meant to be contained within at {@link TabContainerPresenter}.
   * As such, these proxy hold a string that can be displayed on the 
   * tab. 
   */
  public TabContentProxyImpl() {}

  @Override
  public Tab getTab() {
    return tab;
  }
  
  @Inject
  protected void bind( EventBus eventBus ) {
    eventBus.addHandler(requestTabsEventType, new RequestTabsHandler(){
      @Override
      public void onRequestTabs(RequestTabsEvent event) {
        tab = event.getTabContainer().addTab( TabContentProxyImpl.this );
      }
    } );
  }

  @Override
  public float getPriority() {
    return priority;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public String getHistoryToken() {
    return historyToken;
  }
  
}
