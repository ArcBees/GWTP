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

package com.gwtplatform.samples.tab.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.RequestTabsHandler;
import com.gwtplatform.mvp.client.TabContainerPresenter;
import com.gwtplatform.mvp.client.TabView;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.RequestTabs;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.TabContentProxy;
import com.gwtplatform.samples.tab.client.NameTokens;
import com.gwtplatform.samples.tab.client.gin.ClientGinjector;

/**
 * @author Christian Goudreau
 */
public class HomePresenter extends
    TabContainerPresenter<HomePresenter.MyView, HomePresenter.MyProxy> {
  /**
   * {@link HomePresenter}'s proxy.
   */
  @ProxyCodeSplit
  public interface MyProxy extends TabContentProxy<HomePresenter> {
  }

  @TabInfo(container = MainPagePresenter.class, 
      priority = 0, // The first tab in the main page
      nameToken = NameTokens.homePage)
  static String getTabLabel(ClientGinjector ginjector) {
    return ginjector.getMyConstants().home();
  }

  /**
   * {@link HomePresenter}'s view.
   */
  public interface MyView extends TabView {
  }

  /**
   * This will be the event sent to our "unknown" child presenters, in order for
   * them to register their tabs.
   */
  @RequestTabs
  public static final Type<RequestTabsHandler> TYPE_RequestTabs = new Type<RequestTabsHandler>();

  /**
   * Use this in leaf presenters, inside their {@link #revealInParent} method.
   */
  @ContentSlot
  public static final Type<RevealContentHandler<?>> TYPE_SetTabContent = new Type<RevealContentHandler<?>>();

  @Inject
  public HomePresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy) {
    super(eventBus, view, proxy, TYPE_SetTabContent, TYPE_RequestTabs);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetTabContent,
        this);
  }
}