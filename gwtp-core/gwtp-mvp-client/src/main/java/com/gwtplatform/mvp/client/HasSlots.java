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
 * Interface of objects containing slots in which {@link PresenterWidget} can
 * be inserted.
 * <p />
 * Slots are opaque objects and can be of any type. For slots meant to hold
 * only {@link PresenterWidget}s, you will usually declare an {@link Object}
 * constant within your presenter class:
 * <pre>
 * public static final Object TYPE_RevealTopBarContent = new Object();
 * </pre>
 * For slots in which you want to insert {@link Presenter}s, you must
 * use a {@link com.google.gwt.event.shared.GwtEvent.Type Type&lt;RevealContentHandler&lt;?&gt;&gt;}
 * object and annotate it with
 * {@link com.gwtplatform.mvp.client.annotations.ContentSlot ContentSlot}:
 * <pre>
 *{@literal @}ContentSlot
 * public static final Type&lt;RevealContentHandler&lt;?&gt;&gt; TYPE_RevealMainContent
 *   = new Type&lt;RevealContentHandler&lt;?&gt;&gt;();
 * </pre>
 *
 * @author Christian Goudreau
 * @author Philippe Beaudoin
 */
public interface HasSlots {
  /**
   * This method adds some content in a specific slot of the {@link Presenter}.
   * The attached {@link View} should manage this slot when its
   * {@link View#addToSlot(Object, com.google.gwt.user.client.ui.Widget)} is called.
   * <p />
   * Contrary to the {@link #setInSlot} method, no
   * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} is fired,
   * so {@link PresenterWidget#onReset()} is not invoked.
   * <p />
   * For more details on slots, see {@link HasSlots}.
   *
   * @param slot An opaque object identifying which slot this content is being
   *          added into.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null}
   *          will not add anything.
   */
  void addToSlot(Object slot, PresenterWidget<?> content);

  /**
   * This method clears the content in a specific slot. No
   * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} is fired.
   * The attached {@link View} should manage this slot when its
   * {@link View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} is called. It should also clear
   * the slot when the {@link View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} method is
   * called with {@code null} as a parameter.
   * <p />
   * For more details on slots, see {@link HasSlots}.
   *
   * @param slot An opaque object identifying which slot to clear.
   */
  void clearSlot(Object slot);

  /**
   * This method removes some content in a specific slot of the
   * {@link Presenter}. No
   * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} is fired.
   * The attached {@link View} should manage this slot when its
   * {@link View#removeFromSlot(Object, com.google.gwt.user.client.ui.Widget)} is called.
   * <p />
   * For more details on slots, see {@link HasSlots}.
   *
   * @param slot An opaque object identifying which slot this content is being
   *          removed from.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null}
   *          will not remove anything.
   */
  void removeFromSlot(Object slot, PresenterWidget<?> content);

  /**
   * This method sets some content in a specific slot of the {@link Presenter}.
   * A {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} will be fired
   * after the top-most visible presenter is revealed, resulting in a call to
   * {@link PresenterWidget#onReset()}.
   * <p />
   * For more details on slots, see {@link HasSlots}.
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
   * {@link View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} is called. It should also clear the
   * slot when the {@code setInSlot} method is called with {@code null} as a
   * parameter.
   * <p />
   * For more details on slots, see {@link HasSlots}.
   *
   * @param slot An opaque object identifying which slot this content is being
   *          set into.
   * @param content The content, a {@link PresenterWidget}. Passing {@code null}
   *          will clear the slot.
   * @param performReset Pass {@code true} if you want a
   *          {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} to be fired
   *          after the content has been added and this presenter is visible, pass
   *          {@code false} otherwise.
   */
  void setInSlot(Object slot, PresenterWidget<?> content, boolean performReset);
}
