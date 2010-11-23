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

package com.gwtplatform.samples.tab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.samples.tab.client.presenter.MainPagePresenter;
import com.gwtplatform.samples.tab.client.ui.RoundTabPanel;

/**
 * This is the main view of the application. Every time a leaf presenter wants
 * to reveal himself, mainPage will add the content of the target inside the
 * mainContantPanel.
 * 
 * @author Christian Goudreau
 */
public class MainPageView extends ViewImpl implements MainPagePresenter.MyView {
  interface MainPageViewUiBinder extends UiBinder<Widget, MainPageView> {
  }

  private static MainPageViewUiBinder uiBinder = GWT.create(MainPageViewUiBinder.class);

  public final Widget widget;

  @UiField
  RoundTabPanel tabPanel;

  public MainPageView() {
    widget = uiBinder.createAndBindUi(this);
  }

  @Override
  public Tab addTab(String tabName, String historyToken, float priority) {
    return tabPanel.addTab(tabName, historyToken, priority);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void removeTab(Tab tab) {
    tabPanel.removeTab(tab);
  }

  @Override
  public void removeTabs() {
    tabPanel.removeTabs();
  }

  @Override
  public void setActiveTab(Tab tab) {
    tabPanel.setActiveTab(tab);
  }

  @Override
  public void setInSlot(Object slot, Widget content) {
    if (slot == MainPagePresenter.TYPE_SetTabContent) {
      tabPanel.setPanelContent(content);
    } else {
      super.setInSlot(slot, content);
    }
  }
}