/**
 * Copyright 2011 ArcBees Inc.
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
import com.gwtplatform.mvp.client.ChangeTabEvent;
import com.gwtplatform.mvp.client.ChangeTabHandler;
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
public class NonLeafTabContentProxyImpl<T extends Presenter<?, ?>> extends ProxyImpl<T>
    implements NonLeafTabContentProxy<T> {

  protected TabData tabData;
  protected String targetHistoryToken;
  protected Type<RequestTabsHandler> requestTabsEventType;
  protected Type<ChangeTabHandler> changeTabEventType;

  private Tab tab;

  /**
   * Creates a {@link Proxy} for a {@link Presenter} that is meant to be contained within a
   * {@link com.gwtplatform.mvp.client.TabContainerPresenter} but not as a leaf. As such, these
   * proxy hold information that can be displayed on the tab together with the target history
   * token of the leaf page to display when the tab is clicked.
   */
  public NonLeafTabContentProxyImpl() {
  }

  @Override
  public TabData getTabData() {
    return tabData;
  }

  @Override
  public String getTargetHistoryToken() {
    return targetHistoryToken;
  }

  @Override
  public Tab getTab() {
    return tab;
  }

  @Override
  public void changeTab(TabData tabData) {
    this.tabData = tabData;
    if (changeTabEventType != null) {
      ChangeTabEvent.fire(this, changeTabEventType, this);
    }
  }

  protected void addRequestTabsHandler() {
    eventBus.addHandler(requestTabsEventType, new RequestTabsHandler() {
      @Override
      public void onRequestTabs(RequestTabsEvent event) {
        tab = event.getTabContainer().addTab(NonLeafTabContentProxyImpl.this);
      }
    });
  }

  @Override
  public void changeTab(TabData tabData, String targetHistoryToken) {
    this.targetHistoryToken = targetHistoryToken;
    changeTab(tabData);
  }
}
