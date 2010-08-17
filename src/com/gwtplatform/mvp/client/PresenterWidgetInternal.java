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
 * @param <V> The {@link View} type.
 * 
 * @author Christian Goudreau
 */
interface PresenterWidgetInternal<V extends View>  {
  /**
   * Called right after the widget has been made revealed on screen. You should
   * not call this, fire a {@link ResetPresentersEvent} instead.
   */
  void notifyHide();
  
  /**
   * <b>Important!</b> Do not call directly, this is meant to be called only
   * internally by GWTP.
   * <p/>
   * This methods attaches this presenter to its parent.
   * 
   * @param newParent The {@link PresenterWidgetImpl} that will be this
   *          presenter's new parent, or {@code null} to detach from all
   *          parents.
   */
  void reparent(PresenterWidget<?> newParent); 
  
  /**
   * Called by a child {@link PresenterWidget} when it wants to detach itself
   * from this parent.
   * 
   * @param childPresenter The {@link PresenterWidgetImpl} that is a child of
   *          this presenter.
   */
  void detach(PresenterWidget<?> childPresenter);
  
  /**
   * Called right after the widget has been revealed on screen. You should not
   * call this. Fire a {@link ResetPresentersEvent} instead.
   */
  void notifyReveal();
  
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
  void addContent(Object slot, PresenterWidget<?> content);
  
  /**
   * This method clears the content in a specific slot. No
   * {@link ResetPresentersEvent} is fired. The attached {@link View} should
   * manage this slot when its {@link View#setContent(Object, Widget)} is
   * called. It should also clear the slot when the {@code setContent} method is
   * called with {@code null} as a parameter.
   * 
   * @param slot An opaque object identifying which slot to clear.
   */
  void clearContent(Object slot);
  
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
  void removeContent(Object slot, PresenterWidget<?> content); 
  
  /**
   * Makes sure we monitor the specified popup presenter so that we know when it
   * is closing. This way we can make sure it doesn't receive future messages.
   * 
   * @param popupPresenter The {@link PresenterWidgetImpl} to monitor.
   */
  void monitorCloseEvent(PopupPresenter<?> popupPresenter); 
  
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
   void setContent(Object slot, PresenterWidget<?> content);
   
   /**
    * Called whenever the presenters need to be reset. You should not call this,
    * fire a {@link ResetPresentersEvent} instead.
    */
   void reset();
}
