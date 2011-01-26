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
import com.google.inject.Inject;

import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.annotations.TabLabel;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;
import com.gwtplatform.samples.tab.client.NameTokens;
import com.gwtplatform.samples.tab.client.view.DialogSampleUiHandlers;

/**
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class DialogSamplePresenter extends
    Presenter<DialogSamplePresenter.MyView, DialogSamplePresenter.MyProxy> implements
    DialogSampleUiHandlers {
  /**
   * {@link DialogSamplePresenter}'s proxy.
   */
  @ProxyCodeSplit
  @NameToken(NameTokens.dialogSamplePage)
  @TabInfo(container = MainPagePresenter.class, priority = 1) // The second tab
  @TabLabel("Dialog samples")
  public interface MyProxy extends TabContentProxyPlace<DialogSamplePresenter> {
  }

  /**
   * {@link DialogSamplePresenter}'s view.
   */
  public interface MyView extends View, HasUiHandlers<DialogSampleUiHandlers> {
  }

  private final LocalDialogPresenterWidget detailsDialog;
  private final GlobalDialogPresenterWidget wizardDialog;
  private final PopupPresenterWidget infoPopup;

  @Inject
  public DialogSamplePresenter(final EventBus eventBus, final MyView view,
      final MyProxy proxy, final LocalDialogPresenterWidget dialogBox,
      final GlobalDialogPresenterWidget wizardDialog,
      final PopupPresenterWidget infoPopup) {
    super(eventBus, view, proxy);
    this.detailsDialog = dialogBox;
    this.wizardDialog = wizardDialog;
    this.infoPopup = infoPopup;

    getView().setUiHandlers(this);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetTabContent, this);
  }

  @Override
  public void showDetailsDialog() {
    addToPopupSlot(detailsDialog);
  }
  
  @Override
  public void showWizardDialog() {
    RevealRootPopupContentEvent.fire(this, wizardDialog);
  }

  @Override
  public void showInfoPopup(int mousePosX, int mousePosY) {
    addToPopupSlot(infoPopup, false);
    PopupView popupView = (PopupView) infoPopup.getView();
    popupView.setPosition(mousePosX + 15, mousePosY);
  }
}
