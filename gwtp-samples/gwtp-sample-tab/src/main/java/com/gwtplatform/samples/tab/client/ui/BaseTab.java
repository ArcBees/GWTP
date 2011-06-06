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

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.TabData;

/**
 * This is a basic implementation of a {@link Tab} that must be
 * subclassed. Subclasses can style the tab in any way they want.
 * A {@link BaseTab} is added to a {@link BaseTabPanel}.
 *
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public abstract class BaseTab extends Composite implements Tab {
  /**
   * {@link BaseTab}'s CssResource.
   */
  protected interface Style extends CssResource {
    String active();

    String inactive();
  }

  @UiField
  Hyperlink hyperlink;

  @UiField
  Style style;

  private final float priority;

  public BaseTab(TabData tabData) {
    super();
    this.priority = tabData.getPriority();
  }

  @Override
  public void activate() {
    removeStyleName(style.inactive());
    addStyleName(style.active());
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void deactivate() {
    removeStyleName(style.active());
    addStyleName(style.inactive());
  }

  @Override
  public float getPriority() {
    return priority;
  }

  @Override
  public String getText() {
    return hyperlink.getText();
  }

  @Override
  public void setTargetHistoryToken(String historyToken) {
    hyperlink.setTargetHistoryToken(historyToken);
  }

  @Override
  public void setText(String text) {
    hyperlink.setText(text);
  }

  /**
   * Checks whether or not the current user has the right to access this tab.
   * By default, all tabs can be accessed.
   *
   * @return {@code true} if the user can access this tab, {@code false} otherwise.
   */
  public boolean canUserAccess() {
    return true;
  }

}