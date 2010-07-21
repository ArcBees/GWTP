/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gwtplatform.mvp.client;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public interface Tab extends HasText {

  /**
   * Sets the text displayed on the tab.
   * 
   * @param text The text.
   * 
   * @see HasText#setText(String)
   */
  @Override
  public abstract void setText(String text);

  /**
   * Gets the text displayed on the tab.
   * 
   * @return The text.
   * 
   * @see HasText#getText()
   */
  @Override
  public abstract String getText();

  /**
   * Sets the history token this tab links to.
   * 
   * @param historyToken The history token.
   */
  public abstract void setTargetHistoryToken(String historyToken);

  
  /**
   * A tab priority indicates where it should appear within the tab 
   * strip. A tab with low priority will be placed more towards the 
   * left of the strip. Two tabs with the same priority will be placed
   * in an arbitrary order.
   * 
   * @return The priority.
   */
  public abstract float getPriority();


  /**
   * Every tab should be able to return itself as an instance of a 
   * widget class.
   * 
   * @return The tab as a {@link Widget}.
   */
  public abstract Widget asWidget();

  /**
   * Should not be called directly. Call
   * {@link TabPanel#setActiveTab(Tab)} instead.
   */
  public void activate();
  
  /**
   * Should not be called directly. Call
   * {@link TabPanel#setActiveTab(Tab)} instead.
   */
  public void deactivate();
  
}