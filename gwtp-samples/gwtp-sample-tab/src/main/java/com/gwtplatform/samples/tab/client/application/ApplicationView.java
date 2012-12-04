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

package com.gwtplatform.samples.tab.client.application;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.samples.tab.client.application.ui.linkmenu.LinkMenu;
import com.gwtplatform.samples.tab.client.application.ui.roundtab.RoundTabPanel;

/**
 * The view implementation for
 * {@link com.gwtplatform.samples.tab.client.application.ApplicationPresenter}.
 * 
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public class ApplicationView extends ViewImpl implements ApplicationPresenter.MyView {

  /**
   */
  public interface Binder extends UiBinder<Widget, ApplicationView> {
  }

  @UiField(provided = true)
  RoundTabPanel tabPanel;
  @UiField
  InlineLabel topMessage;
  @UiField(provided = true)
  LinkMenu linkMenu;

  public final Widget widget;

  @Inject
  public ApplicationView(Binder uiBinder, RoundTabPanel tabPanel, final LinkMenu linkMenu) {
    this.tabPanel = tabPanel;
    this.linkMenu = linkMenu;

    widget = uiBinder.createAndBindUi(this);
  }

  @Override
  public Tab addTab(TabData tabData, String historyToken) {
    return tabPanel.addTab(tabData, historyToken);
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
  public void changeTab(Tab tab, TabData tabData, String historyToken) {
    tabPanel.changeTab(tab, tabData, historyToken);
  }

  @Override
  public void setInSlot(Object slot, Widget content) {
    if (slot == ApplicationPresenter.TYPE_SetTabContent) {
      tabPanel.setPanelContent(content);
    } else {
      super.setInSlot(slot, content);
    }
  }

  @Override
  public void refreshTabs() {
    tabPanel.refreshTabs();
  }

  @Override
  public void setTopMessage(String string) {
    if (string == null || string.length() == 0) {
      topMessage.setVisible(false);
      topMessage.setText("");
    } else {
      topMessage.setVisible(true);
      topMessage.setText(string);
    }
  }
}
