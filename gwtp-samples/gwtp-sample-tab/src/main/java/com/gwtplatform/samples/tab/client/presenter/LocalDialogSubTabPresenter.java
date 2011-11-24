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

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;
import com.gwtplatform.samples.tab.client.NameTokens;

/**
 * A sample {@link Presenter} that demonstrates how to trigger a local dialog box. It appears as a
 * tab within {@link DialogSamplePresenter}, which is itself a s tab in {@link MainPagePresenter}.
 * <p />
 * It demonstrates the option 1 described in {@link TabInfo}.
 *
 * @author Philippe Beaudoin
 */
public class LocalDialogSubTabPresenter extends
    Presenter<LocalDialogSubTabPresenter.MyView, LocalDialogSubTabPresenter.MyProxy> {

  /**
   * {@link LocalDialogSubTabPresenter}'s proxy.
   */
  @ProxyCodeSplit
  @NameToken(NameTokens.localDialogSamplePage)
  @TabInfo(container = DialogSamplePresenter.class,
      label = "Local",
      priority = 5) // The second tab in the dialog tab
  public interface MyProxy extends TabContentProxyPlace<LocalDialogSubTabPresenter> {
  }

  /**
   * {@link LocalDialogSubTabPresenter}'s view.
   */
  public interface MyView extends View {
    void setPresenter(LocalDialogSubTabPresenter presenter);
  }

  private final LocalDialogPresenterWidget localDialog;

  @Inject
  public LocalDialogSubTabPresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy, final LocalDialogPresenterWidget localDialog) {
    super(eventBus, view, proxy);
    this.localDialog = localDialog;
    view.setPresenter(this);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, DialogSamplePresenter.TYPE_SetTabContent, this);
  }

  public void showLocalDialog() {
    addToPopupSlot(localDialog);
  }
}
