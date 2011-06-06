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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;
import com.gwtplatform.samples.tab.client.presenter.InfoPopupPresenterWidget;

/**
 * The view implementation for
 * {@link com.gwtplatform.samples.tab.client.presenter.InfoPopupPresenterWidget}.
 *
 * @author Philippe Beaudoin
 */
public class InfoPopupView extends PopupViewImpl implements
    InfoPopupPresenterWidget.MyView {

  /**
   */
  public interface Binder extends UiBinder<PopupPanel, InfoPopupView> { }

  private final PopupPanel widget;

  @Inject
  public InfoPopupView(Binder uiBinder, EventBus eventBus) {
    super(eventBus);
    widget = uiBinder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

}
