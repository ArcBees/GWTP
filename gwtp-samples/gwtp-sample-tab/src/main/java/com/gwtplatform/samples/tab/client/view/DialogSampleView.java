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

package com.gwtplatform.samples.tab.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.samples.tab.client.presenter.DialogSamplePresenter.MyView;

/**
 * The view implementation for
 * {@link com.gwtplatform.samples.tab.client.presenter.DialogPresenter}.
 *
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
public class DialogSampleView extends ViewWithUiHandlers<DialogSampleUiHandlers>
    implements MyView {

  /**
   */
  public interface Binder extends UiBinder<Widget, DialogSampleView> { }

  @UiField
  Button localDialog;

  @UiField
  Button globalDialog;

  @UiField
  Anchor popupLink;

  private final Widget widget;

  @Inject
  public DialogSampleView(Binder uiBinder) {
    widget = uiBinder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @UiHandler("localDialog")
  void onDetailsClicked(ClickEvent event) {
    getUiHandlers().showDetailsDialog();
  }

  @UiHandler("globalDialog")
  void onWizardClicked(ClickEvent event) {
    getUiHandlers().showWizardDialog();
  }

  @UiHandler("popupLink")
  void onPopupLinkClicked(MouseDownEvent event) {
    getUiHandlers().showInfoPopup(event.getClientX(), event.getClientY());
  }
}