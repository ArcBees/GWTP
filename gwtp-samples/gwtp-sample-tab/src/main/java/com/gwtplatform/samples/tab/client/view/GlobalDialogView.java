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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;
import com.gwtplatform.samples.tab.client.presenter.GlobalDialogPresenterWidget.MyView;

/**
 * The view implementation for
 * {@link com.gwtplatform.samples.tab.client.presenter.GlobalDialogPresenterWidget}.
 *
 * @author Philippe Beaudoin
 */
public class GlobalDialogView extends PopupViewImpl implements MyView {

  /**
   */
  public interface Binder extends UiBinder<PopupPanel, GlobalDialogView> { }

  @UiField
  Button okButton;

  @UiField
  Label navigationMessage;

  private final PopupPanel widget;

  @Inject
  public GlobalDialogView(Binder uiBinder, EventBus eventBus) {
    super(eventBus);
    widget = uiBinder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @UiHandler("okButton")
  void okButtonClicked(ClickEvent event) {
    widget.hide();
  }

  @Override
  public void setNavigationPlace(String placeName) {
    if (placeName == null) {
      navigationMessage.setText("");
    } else {
      navigationMessage.setText("Looks like you just navigated to '" + placeName + "'.");
    }
  }
}
