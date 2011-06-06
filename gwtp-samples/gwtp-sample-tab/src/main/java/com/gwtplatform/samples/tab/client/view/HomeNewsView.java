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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.samples.tab.client.presenter.HomeNewsPresenter;

/**
 * The view implementation for
 * {@link com.gwtplatform.samples.tab.client.presenter.HomeNewsPresenter}.
 *
 * @author Christian Goudreau
 */
public class HomeNewsView extends ViewImpl implements HomeNewsPresenter.MyView {

  /**
   */
  public interface Binder extends UiBinder<Widget, HomeNewsView> { }

  private final Widget widget;

  @UiField
  Anchor confirmationLink;

  private HomeNewsPresenter presenter;

  @Inject
  public HomeNewsView(Binder uiBinder) {
    widget = uiBinder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setConfirmationText(String text) {
    confirmationLink.setText(text);
  }

  @Override
  public void setPresenter(HomeNewsPresenter presenter) {
    this.presenter = presenter;
  }

  @UiHandler("confirmationLink")
  public void onClick(ClickEvent clickEvent) {
    presenter.toggleConfirmation();
  }
}