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

package com.gwtplatform.mvp.client;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * This interface represents a tab after it has been instantiated
 * as a {@link Widget}. The full description of a tab before it is 
 * created is given by {@link TabData}.
 * 
 * @author Philippe Beaudoin
 */
public interface Tab extends HasText {

  /**
   * Should not be called directly. Call {@link TabPanel#setActiveTab(Tab)}
   * instead.
   */
  void activate();

  /**
   * Every tab should be able to return itself as an instance of a widget class.
   * 
   * @return The tab as a {@link Widget}.
   */
  Widget asWidget();

  /**
   * Should not be called directly. Call {@link TabPanel#setActiveTab(Tab)}
   * instead.
   */
  void deactivate();

  /**
   * A tab priority indicates where it should appear within the tab strip. In
   * typical implementations of {@link TabPanel}, a tab with low priority will 
   * be placed more towards the left of the strip. Two tabs with the same 
   * priority will be placed in an arbitrary order.
   * 
   * @return The priority.
   */
  float getPriority();

  /**
   * Gets the text displayed on the tab.
   * 
   * @return The text.
   * 
   * @see HasText#getText()
   */
  @Override
  String getText();

  /**
   * Sets the history token this tab links to.
   * 
   * @param historyToken The history token.
   */
  void setTargetHistoryToken(String historyToken);

  /**
   * Sets the text displayed on the tab.
   * 
   * @param text The text.
   * 
   * @see HasText#setText(String)
   */
  @Override
  void setText(String text);

}