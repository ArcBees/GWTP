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
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.RequestTabs;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.samples.tab.client.CurrentUserChangedEvent;
import com.gwtplatform.samples.tab.client.CurrentUserChangedEvent.CurrentUserChangedHandler;

/**
 * This is the main presenter of the application. It's a
 * {@link com.gwtplatform.mvp.client.TabContainerPresenter}.
 * 
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class MainPagePresenter
    extends TabContainerPresenter<MainPagePresenter.MyView, MainPagePresenter.MyProxy> 
    implements CurrentUserChangedHandler {
  /**
   * {@link MainPagePresenter}'s proxy.
   */
  @ProxyStandard
  public interface MyProxy extends Proxy<MainPagePresenter> {
  }

  /**
   * {@link MainPagePresenter}'s view.
   */
  public interface MyView extends TabView {
    void refreshTabs();
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
  public MainPagePresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy) {
    super(eventBus, view, proxy, TYPE_SetTabContent, TYPE_RequestTabs);
  }

  @Override
  protected void revealInParent() {
    RevealRootContentEvent.fire(this, this);
  }

  @ProxyEvent
  @Override
  public void onCurrentUserChanged(CurrentUserChangedEvent event) {
    getView().refreshTabs();
  }
  
}
