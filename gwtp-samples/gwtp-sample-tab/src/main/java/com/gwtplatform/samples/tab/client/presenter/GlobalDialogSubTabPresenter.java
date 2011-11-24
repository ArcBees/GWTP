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
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;
import com.gwtplatform.samples.tab.client.NameTokens;
import com.gwtplatform.samples.tab.client.view.GlobalDialogSubTabUiHandlers;

/**
 * A sample {@link Presenter} that demonstrates how to trigger a global dialog box. It appears as a
 * tab within {@link DialogSamplePresenter}, which is itself a s tab in {@link MainPagePresenter}.
 * <p />
 * It demonstrates the option 3 described in {@link TabInfo}.
 *
 * @author Philippe Beaudoin
 */
public class GlobalDialogSubTabPresenter extends
    Presenter<GlobalDialogSubTabPresenter.MyView, GlobalDialogSubTabPresenter.MyProxy>
    implements GlobalDialogSubTabUiHandlers {

  /**
   * {@link GlobalDialogSubTabPresenter}'s proxy.
   */
  @ProxyCodeSplit
  @NameToken(NameTokens.globalDialogSamplePage)
  @TabInfo(container = DialogSamplePresenter.class,
    label = "Global",
    priority = 0) // The leftmost tab in the dialog tab
  public interface MyProxy extends TabContentProxyPlace<GlobalDialogSubTabPresenter> {
  }

  /**
   * {@link GlobalDialogSubTabPresenter}'s view.
   */
  public interface MyView extends View, HasUiHandlers<GlobalDialogSubTabUiHandlers> {
  }

  private final GlobalDialogPresenterWidget globalDialog;
  private final InfoPopupPresenterWidget infoPopup;

  @Inject
  public GlobalDialogSubTabPresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy, final GlobalDialogPresenterWidget globalDialog,
      final InfoPopupPresenterWidget infoPopup) {
    super(eventBus, view, proxy);
    this.globalDialog = globalDialog;
    this.infoPopup = infoPopup;

    getView().setUiHandlers(this);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, DialogSamplePresenter.TYPE_SetTabContent, this);
  }

  public void showGlobalDialog() {
    RevealRootPopupContentEvent.fire(this, globalDialog);
  }

  @Override
  public void showInfoPopup(int mousePosX, int mousePosY) {
    addToPopupSlot(infoPopup, false);
    PopupView popupView = (PopupView) infoPopup.getView();
    popupView.setPosition(mousePosX + 15, mousePosY);
  }
}
