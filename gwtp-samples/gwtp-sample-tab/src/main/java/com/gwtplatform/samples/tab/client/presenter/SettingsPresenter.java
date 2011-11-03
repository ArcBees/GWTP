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

import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;
import com.gwtplatform.samples.tab.client.CurrentUser;
import com.gwtplatform.samples.tab.client.NameTokens;
import com.gwtplatform.samples.tab.client.view.SettingsUiHandlers;

/**
 * A sample {@link Presenter} that lets user toggle between being an
 * administrator and a regular user.
 * <p />
 * It demonstrates the option 1 described in {@link TabInfo}.
 *
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class SettingsPresenter
    extends Presenter<SettingsPresenter.MyView, SettingsPresenter.MyProxy>
    implements SettingsUiHandlers {
  /**
   * {@link SettingsPresenter}'s proxy.
   */
  @ProxyCodeSplit
  @NameToken(NameTokens.settingsPage)
  @TabInfo(container = MainPagePresenter.class,
      label = "Settings",
      priority = 2) // The third tab in the main page
  public interface MyProxy extends TabContentProxyPlace<SettingsPresenter> {
  }

  /**
   * {@link SettingsPresenter}'s view.
   */
  public interface MyView extends View, HasUiHandlers<SettingsUiHandlers>  {
    void setAdmin(boolean isAdmin);
  }

  private final CurrentUser currentUser;

  @Inject
  public SettingsPresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy, final CurrentUser currentUser) {
    super(eventBus, view, proxy);
    this.currentUser = currentUser;
    view.setUiHandlers(this);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetTabContent,
        this);
  }

  @Override
  protected void onReveal() {
    updateView();
  }

  @Override
  public void togglePrivileges() {
    currentUser.setAdmin(!currentUser.isAdmin());
    updateView();
  }

  private void updateView() {
    getView().setAdmin(currentUser.isAdmin());
  }
}