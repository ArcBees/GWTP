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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.Widget;

/**
 * A presenter that does not have to be a singleton. Single pages from your
 * application will usually implement the singleton {@link Presenter} interface.
 * Use the {@link PresenterWidget} interface for complex widget that need to
 * interact with model objects, but that can be instantiated in many different
 * places in your application.
 * <p />
 * {@link PresenterWidget}s and {@link Presenter}s are organized in a hierarchy.
 * Parent presenters have links to their child presenters. To reveal a presenter
 * you should build a {@link PlaceRequest} and call one of the following method:
 * <ul>
 * <li>{@link PlaceManager#revealPlace(PlaceRequest)}</li>
 * <li>{@link PlaceManager#revealRelativePlace(PlaceRequest)}</li>
 * <li>{@link PlaceManager#revealRelativePlace(PlaceRequest, int)}</li>
 * </ul>
 * The presenter's {@link Proxy} then asks the presenter to reveal itself by
 * calling {@link Presenter#forceReveal()}, triggering the following sequence of
 * operations:
 * <ul>
 * <li>The presenter that wants to reveal itself asks to be set in one of its
 * parent slot by firing a {@link RevealContentEvent} ;
 * <li>If a presenter already occupies this slot it is removed. If the parent is
 * currently visible, then {@link PresenterWidgetImpl#onHide()} is called on the
 * removed presenter and, recursively, on its children (bottom up: first the
 * child, then the parent) ;
 * <li>If the parent is not visible, it asks to be set in one of its parent slot
 * by firing a {@link RevealContentEvent} too, this continue recursively until
 * the top-level or until a visible presenter is reached ;
 * <li>When a visible presenter is reached, it calls
 * {@link PresenterWidgetImpl#onReveal()} on the presenter it just added to a
 * slot and, recursively, on that presenter's children (top down: first the
 * parent, then the children);</li>
 * <li>Finally {@link PresenterWidgetImpl#onReset()} is called on all the
 * currently visible presenters</li>
 * starting from the top-level presenter and going down to the leaf presenters
 * (top down: first the parent, then the children).</li>
 * </ul>
 * 
 * @param <V> The {@link View} type.
 * 
 * @author Philippe Beaudoin
 */
public interface PresenterWidget<V extends View> extends HandlerContainer, HasEventBus {

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
   * Convenience method to register an event handler to the {@link EventBus}.
   * The handler will be automatically unregistered when
   * {@link HandlerContainer#unbind()} is called.
   * 
   * @param <H> The handler type
   * @param type See {@link Type}
   * @param handler The handler to register
   */
  <H extends EventHandler> void addRegisteredHandler(Type<H> type, H handler);

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
   * Makes it possible to access the {@link EventBus} object associated with
   * that presenter.
   * 
   * @return The EventBus associated with that presenter.
   */
  EventBus getEventBus();
  
  /**
   * Returns the {@link View} for the current presenter.
   * 
   * @return The view.
   */
  V getView();
  
  /**
   * Makes it possible to access the {@link Widget} object associated with that
   * presenter.
   * 
   * @return The Widget associated with that presenter.
   */
  Widget getWidget();
  
  /**
   * Verifies if the presenter is currently visible on the screen. A presenter
   * should be visible if it successfully revealed itself and was not hidden
   * later.
   * 
   * @return {@code true} if the presenter is visible, {@code false} otherwise.
   */
  boolean isVisible(); 
  
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
}