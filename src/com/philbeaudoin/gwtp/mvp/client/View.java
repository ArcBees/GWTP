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

package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * The interface for view classes that handles all
 * the UI-related code for a {@link Presenter}.
 * 
 * @author Philippe Beaudoin
 */
public interface View {

  /**
   * Requests the view to set content within a specific slot,
   * clearing anything that was already contained there.
   * If the view doesn't know about this slot, it can silently 
   * ignore the request.
   * 
   * @param slot An opaque object indicating the slot to add into.
   * @param content The content to add, a {@link Widget}. Pass {@code null} to clear the slot entirely.
   */
  public void setContent( Object slot, Widget content );
  
  /**
   * Requests the view to add content within a specific slot.
   * If the view doesn't know about this slot, it can silently 
   * ignore the request .
   * 
   * @param slot An opaque object indicating the slot to add into.
   * @param content The content to add, a {@link Widget}.
   */
  public void addContent( Object slot, Widget content );
  
  /**
   * Retrieves this view as a {@link Widget} so that it can be inserted within the DOM.
   * 
   * @return This view as a DOM object.
   */
  public Widget asWidget();

}