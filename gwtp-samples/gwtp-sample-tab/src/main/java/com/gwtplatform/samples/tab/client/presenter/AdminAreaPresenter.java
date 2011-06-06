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

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;
import com.gwtplatform.samples.tab.client.IsAdminGatekeeper;
import com.gwtplatform.samples.tab.client.NameTokens;
import com.gwtplatform.samples.tab.client.TabDataExt;
import com.gwtplatform.samples.tab.client.gin.ClientGinjector;

/**
 * A sample {@link Presenter} that should only be displayed to administrator users.
 * It appears as a tab within {@link MainPagePresenter}.
 * <p />
 * It uses {@link IsAdminGatekeeper} to prevent access to non-administrator users.
 * <p />
 * It uses the option 3 described in {@link TabInfo} to describe the tab using a
 * {@link TabDataExt} that ensures the tab is not visible to non-administrator users.
 *
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class AdminAreaPresenter
    extends Presenter<AdminAreaPresenter.MyView, AdminAreaPresenter.MyProxy>  {
  /**
   * {@link AdminAreaPresenter}'s proxy.
   */
  @ProxyCodeSplit
  @NameToken(NameTokens.adminPage)
  @UseGatekeeper(IsAdminGatekeeper.class)
  public interface MyProxy extends TabContentProxyPlace<AdminAreaPresenter> {
  }

  @TabInfo(container = MainPagePresenter.class)
  static TabData getTabLabel(ClientGinjector ginjector) {
    // Priority = 1000, means it will be the right-most tab in the home tab
    return new TabDataExt("Admin area", 1000,
        ginjector.getIsAdminGatekeeper());
  }

  /**
   * {@link AdminAreaPresenter}'s view.
   */
  public interface MyView extends View {
  }

  @Inject
  public AdminAreaPresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy) {
    super(eventBus, view, proxy);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetTabContent,
        this);
  }

}