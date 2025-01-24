/*
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

package com.gwtplatform.mvp.client;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import com.gwtplatform.mvp.client.presenter.slots.IsSingleSlot;
import com.gwtplatform.mvp.client.presenter.slots.IsSlot;
import com.gwtplatform.mvp.client.presenter.slots.MultiSlot;
import com.gwtplatform.mvp.client.presenter.slots.OrderedSlot;
import com.gwtplatform.mvp.client.presenter.slots.RemovableSlot;
import com.gwtplatform.mvp.client.presenter.slots.Slot;

/**
 * Interface of objects containing slots in which {@link PresenterWidget} can be inserted.
 * <p>
 * Slots are opaque objects and can be of any type. For slots meant to hold only {@link PresenterWidget}s, you will
 * usually declare a {@link IsSlot} constant within your presenter class:
 * <pre>
 * public static final PermanentSlot&lt;PresenterWidget&lt;?&gt;&gt; SLOT_TOP_BAR = new PermanentSlot&lt;&gt;();
 * </pre>
 * For slots in which you want to insert {@link Presenter}s, you must use a {@link
 * com.gwtplatform.mvp.client.presenter.slots.NestedSlot NestedSlot} object:
 * <pre>
 * public static final NestedSlot SLOT_MAIN_CONTENT = new NestedSlot();
 * </pre>
 */
public interface HasSlots {
    /**
     * This method adds some content in a specific slot of the {@link Presenter}. The attached {@link View} should
     * manage this slot when its {@link View#addToSlot(Object, IsWidget)} is called.
     * <p>
     * Contrary to the {@link #setInSlot} method, no {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent
     * ResetPresentersEvent} is fired, so {@link PresenterWidget#onReset()} is not invoked.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot The slot into which the content is being added.
     * @param child The content, a {@link PresenterWidget}. Passing {@code null} will not add anything.
     */
    <T extends PresenterWidget<?>> void addToSlot(MultiSlot<T> slot, T child);

    /**
     * This method clears the content in a specific slot. No
     * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent ResetPresentersEvent} is fired. The attached
     * {@link View} should manage this slot when its {@link View#setInSlot(Object, IsWidget)} is called. It should also
     * clear the slot when the {@link View#setInSlot(Object, IsWidget)} method is called with {@code null} as a
     * parameter.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot The slot to clear.
     */
    void clearSlot(RemovableSlot<?> slot);

    /**
     * This method removes some content in a specific slot of the {@link Presenter}. No {@link
     * com.gwtplatform.mvp.client.proxy.ResetPresentersEvent ResetPresentersEvent} is fired. The attached {@link View}
     * should manage this slot when its {@link View#removeFromSlot(Object, IsWidget)} is called.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot The slot for which the content is being removed.
     * @param child The content, a {@link PresenterWidget}. Passing {@code null} will not remove anything.
     */
    <T extends PresenterWidget<?>> void removeFromSlot(RemovableSlot<T> slot, T child);

    /**
     * This method sets some content in a specific slot of the {@link Presenter}. A {@link
     * com.gwtplatform.mvp.client.proxy.ResetPresentersEvent ResetPresentersEvent} will be fired after the top-most
     * visible presenter is revealed, resulting in a call to {@link PresenterWidget#onReset()}.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot The slot for which the content is being set. The attached view should know what to do with this
     * slot.
     * @param child The content, a {@link PresenterWidget}. Passing {@code null} will clear the slot.
     */
    <T extends PresenterWidget<?>> void setInSlot(IsSlot<T> slot, T child);

    /**
     * This method sets some content in a specific slot of the {@link Presenter}. The attached {@link View} should
     * manage this slot when its {@link View#setInSlot(Object, IsWidget)} is called. It should also clear the slot when
     * the {@code setInSlot} method is called with {@code null} as a parameter.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot The slot for which the content is being set.
     * @param child The content, a {@link PresenterWidget}. Passing {@code null} will clear the slot.
     * @param performReset Pass {@code true} if you want a {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent
     * ResetPresentersEvent} to be fired after the content has been added and this presenter is visible, pass {@code
     * false} otherwise.
     */
    <T extends PresenterWidget<?>> void setInSlot(IsSlot<T> slot, T child, boolean performReset);

    /**
     * Get the child of SingleSlot.
     *
     * @param slot - the slot
     *
     * @return the child of the slot or null if the slot is empty.
     */
    <T extends PresenterWidget<?>> T getChild(IsSingleSlot<T> slot);

    /**
     * Get the children of a slot.
     *
     * @param slot - the slot
     *
     * @return the children of the slot.
     */
    <T extends PresenterWidget<?>> Set<T> getChildren(Slot<T> slot);

    /**
     * Get the children of an ordered slot.
     *
     * @param slot - an ordered slot
     *
     * @return the children of the slot in a sorted list.
     */
    <T extends PresenterWidget<?> & Comparable<T>> List<T> getChildren(OrderedSlot<T> slot);
}
