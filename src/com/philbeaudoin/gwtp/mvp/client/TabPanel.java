/**
 * Copyright 2010 Philippe Beaudoin
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

package com.philbeaudoin.gwtp.mvp.client;

public interface TabPanel {

  /**
   * Adds a new tab to the widget.
   * 
   * @param text The text to display on the tab.
   * @param historyToken The history token the tab points to.
   * @param priority The priority of the {@link Tab} to add.
   * @return The newly added {@link Tab}.
   */
  public Tab addTab( String text, String historyToken, float priority );
  
  
  /**
   * Removes a tab from the widget.
   * 
   * @param tab The tab to remove
   */
  public void removeTab( Tab tab );

  
  /**
   * Removes all tabs from the widget.
   */
  public void removeTabs();

  /**
   * Sets the currently active tab.
   * 
   * @param tab The tab to activate
   */
  public void setActiveTab( Tab tab );
  
}
