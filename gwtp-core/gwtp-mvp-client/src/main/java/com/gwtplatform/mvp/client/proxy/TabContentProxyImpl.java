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

import com.google.gwt.event.shared.GwtEvent.Type;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.RequestTabsEvent;
import com.gwtplatform.mvp.client.RequestTabsHandler;
import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.TabData;

/**
 * @param <T> The Presenter's type.
 *
 * @author Philippe Beaudoin
 */
public class TabContentProxyImpl<T extends Presenter<?, ?>> extends ProxyImpl<T>
    implements TabContentProxy<T> {

  protected String targetHistoryToken;
  protected TabData tabData;
  protected Type<RequestTabsHandler> requestTabsEventType;

  private Tab tab;

  /**
   * Creates a {@link Proxy} for a {@link Presenter} that is meant to be
   * contained within at {@link com.gwtplatform.mvp.client.TabContainerPresenter}. As such, these proxy
   * hold a string that can be displayed on the tab.
   */
  public TabContentProxyImpl() {
  }

  @Override
  public String getTargetHistoryToken() {
    return targetHistoryToken;
  }

  @Override
  public TabData getTabData() {
    return tabData;
  }

  @Override
  public Tab getTab() {
    return tab;
  }

  protected void addRequestTabsHandler() {
    eventBus.addHandler(requestTabsEventType, new RequestTabsHandler() {
      @Override
      public void onRequestTabs(RequestTabsEvent event) {
        tab = event.getTabContainer().addTab(TabContentProxyImpl.this);
      }
    });
  }

}
