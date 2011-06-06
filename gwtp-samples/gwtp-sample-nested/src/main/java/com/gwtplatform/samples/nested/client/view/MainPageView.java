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

package com.gwtplatform.samples.nested.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.samples.nested.client.presenter.MainPagePresenter;
import com.gwtplatform.samples.nested.client.presenter.MainPagePresenter.MyView;

/**
 * This is the top-level view of the application. Every time another presenter
 * wants to reveal itself, {@link MainPageView} will add its content of the
 * target inside the {@code mainContantPanel}.
 *
 * @author Christian Goudreau
 */
public class MainPageView extends ViewImpl implements MyView {
  interface MainPageViewUiBinder extends UiBinder<Widget, MainPageView> {
  }

  private static MainPageViewUiBinder uiBinder = GWT.create(MainPageViewUiBinder.class);

  public final Widget widget;

  @UiField
  FlowPanel mainContentPanel;

  @UiField
  Element loadingMessage;

  public MainPageView() {
    widget = uiBinder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setInSlot(Object slot, Widget content) {
    if (slot == MainPagePresenter.TYPE_SetMainContent) {
      setMainContent(content);
    } else {
      super.setInSlot(slot, content);
    }
  }

  private void setMainContent(Widget content) {
    mainContentPanel.clear();

    if (content != null) {
      mainContentPanel.add(content);
    }
  }

  @Override
  public void showLoading(boolean visibile) {
    loadingMessage.getStyle().setVisibility(
        visibile ? Visibility.VISIBLE : Visibility.HIDDEN);
  }
}