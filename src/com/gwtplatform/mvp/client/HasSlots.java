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

/**
 * @author Christian Goudreau
 */
public interface HasSlots { 
  /**
   * This method adds some content in a specific slot of the {@link Presenter}.
   * No {@link ResetPresentersEvent} is fired. The attached {@link View} should
   * manage this slot when its {@link View#addContent(Object, Widget)} is
   * called.
   * 
   * @param slot An opaque object identifying which slot this content is being
   *          added into.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null}
   *          will not add anything.
   */
  void addToSlot(Object slot, PresenterWidget<?> content);
  
  /**
   * This method clears the content in a specific slot. No
   * {@link ResetPresentersEvent} is fired. The attached {@link View} should
   * manage this slot when its {@link View#setContent(Object, Widget)} is
   * called. It should also clear the slot when the {@code setContent} method is
   * called with {@code null} as a parameter.
   * 
   * @param slot An opaque object identifying which slot to clear.
   */
  void clearSlot(Object slot);
  
  /**
   * This method removes some content in a specific slot of the
   * {@link Presenter}. No {@link ResetPresentersEvent} is fired. The attached
   * {@link View} should manage this slot when its
   * {@link View#removeContent(Object, Widget)} is called.
   * 
   * @param slot An opaque object identifying which slot this content is being
   *          removed from.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null}
   *          will not remove anything.
   */
  void removeFromSlot(Object slot, PresenterWidget<?> content);
  
  /**
   * This method sets some content in a specific slot of the {@link Presenter}.
   * A {@link ResetPresentersEvent} will be fired after the top-most visible
   * presenter is revealed.
   * 
   * @param slot An opaque object identifying which slot this content is being
   *          set into. The attached view should know what to do with this slot.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null}
   *          will clear the slot.
   */
  void setInSlot(Object slot, PresenterWidget<?> content);
  
  /**
   * This method sets some content in a specific slot of the {@link Presenter}.
   * The attached {@link View} should manage this slot when its
   * {@link View#setContent(Object, Widget)} is called. It should also clear the
   * slot when the {@code setContent} method is called with {@code null} as a
   * parameter.
   * 
   * @param slot An opaque object identifying which slot this content is being
   *          set into.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null}
   *          will clear the slot.
   * @param performReset Pass {@code true} if you want a
   *          {@link ResetPresentersEvent} to be fired after the content has
   *          been added and this presenter is visible, pass {@code false}
   *          otherwise.
   */
  void setInSlot(Object slot, PresenterWidget<?> content, boolean performReset);
}
