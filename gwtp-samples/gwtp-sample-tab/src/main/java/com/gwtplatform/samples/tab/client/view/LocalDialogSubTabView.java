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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.samples.tab.client.presenter.LocalDialogSubTabPresenter;
import com.gwtplatform.samples.tab.client.presenter.LocalDialogSubTabPresenter.MyView;

/**
 * The view implementation for
 * {@link com.gwtplatform.samples.tab.client.presenter.LocalDialogSubTabPresenter}.
 *
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
public class LocalDialogSubTabView extends ViewImpl implements MyView {

  /**
   */
  public interface Binder extends UiBinder<Widget, LocalDialogSubTabView> { }

  @UiField
  Button localDialog;

  private final Widget widget;

  private LocalDialogSubTabPresenter presenter;

  @Inject
  public LocalDialogSubTabView(Binder uiBinder) {
    widget = uiBinder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setPresenter(LocalDialogSubTabPresenter presenter) {
    this.presenter = presenter;
  }

  @UiHandler("localDialog")
  void onLocalClicked(ClickEvent event) {
    if (presenter != null) {
      presenter.showLocalDialog();
    }
  }
}