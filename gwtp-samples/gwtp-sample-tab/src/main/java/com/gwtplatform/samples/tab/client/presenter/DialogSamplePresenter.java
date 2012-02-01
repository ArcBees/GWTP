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

package com.gwtplatform.samples.tab.client.presenter;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.RequestTabsHandler;
import com.gwtplatform.mvp.client.TabContainerPresenter;
import com.gwtplatform.mvp.client.TabView;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.RequestTabs;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.proxy.NonLeafTabContentProxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.samples.tab.client.NameTokens;

/**
 * A sample {@link com.gwtplatform.mvp.client.Presenter Presenter} that acts as a container for the
 * dialog sub tab presenters. It appears as a tab within {@link MainPagePresenter}.
 * <p />
 * It demonstrates the option 1 described in {@link TabInfo}.

 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class DialogSamplePresenter
    extends TabContainerPresenter<DialogSamplePresenter.MyView, DialogSamplePresenter.MyProxy> {
  /**
   * {@link DialogSamplePresenter}'s proxy.
   */
  @ProxyCodeSplit
  @TabInfo(container = MainPagePresenter.class,
      label = "Dialogs",
      priority = 1, // The second tab in the main page
      nameToken = NameTokens.globalDialogSamplePage)
  public interface MyProxy extends NonLeafTabContentProxy<DialogSamplePresenter> {
  }

  /**
   * {@link DialogSamplePresenter}'s view.
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
  public static final Type<RevealContentHandler<?>> TYPE_SetTabContent =
      new Type<RevealContentHandler<?>>();

  @Inject
  public DialogSamplePresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy) {
    super(eventBus, view, proxy, TYPE_SetTabContent, TYPE_RequestTabs);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetTabContent, this);
  }
}
