/**
 * Copyright 2014 ArcBees Inc.
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
package com.gwtplatform.mvp.client.presenter;

import java.util.Set;
import java.util.SortedSet;

import com.gwtplatform.mvp.client.GenericPresenterWidget;
import com.gwtplatform.mvp.client.presenter.slots.AbstractSlot;
import com.gwtplatform.mvp.client.presenter.slots.MultiSlot;
import com.gwtplatform.mvp.client.presenter.slots.OrderedSlot;
import com.gwtplatform.mvp.client.presenter.slots.SingleSlot;
import com.gwtplatform.mvp.client.presenter.slots.Slot;

public interface HasSlots
        extends com.gwtplatform.mvp.client.HasSlots<Class<? extends AbstractSlot<?>>, Class<? extends MultiSlot<?>>> {

    /**
     * Gets the current children of the slot.
     * @param slot - the slot
     * @return the children of this slot.
     */
    <T extends GenericPresenterWidget<Class<? extends AbstractSlot<?>>, Class<? extends MultiSlot<?>>, ?>>
        Set<T> getSlotChildren(Class<? extends Slot<T>> slot);

    /**
     * Gets the children of an ordered slot in order.
     * @param slot - the slot
     * @return the children of the slot in a sorted set.
     */
    <T extends GenericPresenterWidget<Class<? extends AbstractSlot<?>>, Class<? extends MultiSlot<?>>, ?>
    & Comparable<T>> SortedSet<T> getOrderedSlotChildren(Class<? extends OrderedSlot<T>> slot);

    /**
     * Gets the child of a SingleSlot.
     * @param slot - the slot
     * @return the child of the slot or null if the slot is empty.
     */
    <T extends GenericPresenterWidget<Class<? extends AbstractSlot<?>>, Class<? extends MultiSlot<?>>, ?>>
    T getSlotChild(Class<? extends SingleSlot<T>> slot);
}
