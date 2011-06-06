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

package com.gwtplatform.samples.tab.client.ui;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.TabPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a basic implementation of a {@link TabPanel} that must be
 * subclassed. Subclasses can style the tab panel in any way they want.
 * A {@link BaseTabPanel} will hold a number of {@link BaseTab}.
 *
 * @author Philippe Beaudoin
 */
public abstract class BaseTabPanel extends Composite implements TabPanel {

  Tab currentActiveTab;

  @UiField
  FlowPanel tabContentContainer;

  @UiField
  FlowPanel tabPanel;
  private final List<BaseTab> tabList = new ArrayList<BaseTab>();

  @Override
  public Tab addTab(TabData tabData, String historyToken) {
    BaseTab newTab = createNewTab(tabData);
    int beforeIndex;
    for (beforeIndex = 0; beforeIndex < tabList.size(); ++beforeIndex) {
      if (newTab.getPriority() < tabList.get(beforeIndex).getPriority()) {
        break;
      }
    }
    tabPanel.insert(newTab.asWidget(), beforeIndex);
    tabList.add(beforeIndex, newTab);
    newTab.setTargetHistoryToken(historyToken);
    setTabVisibility(newTab);
    return newTab;
  }

  @Override
  public void removeTab(Tab tab) {
    tabPanel.getElement().removeChild(tab.asWidget().getElement());
    tabList.remove(tab);
  }

  @Override
  public void removeTabs() {
    for (Tab tab : tabList) {
      tabPanel.getElement().removeChild(tab.asWidget().getElement());
    }
    tabList.clear();
  }

  @Override
  public void setActiveTab(Tab tab) {
    if (currentActiveTab != null) {
      currentActiveTab.deactivate();
    }
    if (tab != null) {
      tab.activate();
    }
    currentActiveTab = tab;
  }

  /**
   * Sets the content displayed in the main panel.
   *
   * @param panelContent The {@link Widget} to set in the main panel, or
   *          {@code null} to clear the panel.
   */
  public void setPanelContent(Widget panelContent) {
    tabContentContainer.clear();
    if (panelContent != null) {
      tabContentContainer.add(panelContent);
    }
  }

  /**
   * Ensures that all tabs are visible or hidden as they should.
   */
  public void refreshTabs() {
    for (BaseTab tab : tabList) {
      setTabVisibility(tab);
    }
  }

  /**
   * Ensures the specified tab is visible or hidden as it should.
   *
   * @param tab The {@link BaseTab} to check.
   */
  private void setTabVisibility(BaseTab tab) {
    boolean visible = (tab == currentActiveTab) || tab.canUserAccess();
    tab.setVisible(visible);
  }

  /**
   * Returns a new tab of the type specific for this tab panel.
   *
   * @param tabData Some data on the tab to create.
   * @return The new tab.
   */
  protected abstract BaseTab createNewTab(TabData tabData);

}