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

import com.google.gwt.user.client.ui.IsWidget;

/**
 * The interface for view classes that handles all the UI-related code for a
 * {@link Presenter}.
 */
public interface View extends IsWidget {

    /**
     * Requests the view to add content within a specific slot.
     * <p>
     * Override the default implementation and manage all the slots of your view
     * into which content can be added. You should also consider overriding
     * {@link #removeFromSlot(Object, IsWidget)}.
     * If the view doesn't know about this slot, it can silently ignore the request.
     * <p>
     * Used by {@link PresenterWidget#addToSlot(Object, PresenterWidget)}.
     *
     * @param slot    An opaque object indicating the slot to add into.
     * @param content The content to add, a {@link IsWidget}.
     */
    void addToSlot(Object slot, IsWidget content);

    /**
     * Requests the view to remove content from a specific slot.
     * <p>
     * Override the default implementation and manage all the slots of your view
     * into which content can be added and removed. You should also override
     * {@link #addToSlot(Object, IsWidget)}.
     * If the view doesn't know about this slot, it can silently ignore the request.
     * <p>
     * Used by {@link PresenterWidget#removeFromSlot(Object, PresenterWidget)}.
     *
     * @param slot    An opaque object indicating the slot to remove from.
     * @param content The content to remove, a {@link IsWidget}.
     */
    void removeFromSlot(Object slot, IsWidget content);

    /**
     * Requests the view to set content within a specific slot, clearing anything
     * that was already contained there.
     * <p>
     * Override the default implementation and manage all the slots of your view
     * into which content can be placed. If the view doesn't know about this slot,
     * it can silently ignore the request. When {@code null} is passed, your
     * implementation should clear the slot.
     * <p>
     * Used by {@link PresenterWidget#setInSlot(Object, PresenterWidget)} and
     * {@link PresenterWidget#clearSlot(Object)}.
     *
     * @param slot    An opaque object indicating the slot to add into.
     * @param content The content to add, a {@link IsWidget}. Pass {@code null} to
     *                clear the slot entirely.
     */
    void setInSlot(Object slot, IsWidget content);
}
