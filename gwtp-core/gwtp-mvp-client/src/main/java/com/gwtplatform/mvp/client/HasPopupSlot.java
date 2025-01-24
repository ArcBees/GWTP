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

public interface HasPopupSlot {
    /**
     * This method sets some popup content within the {@link Presenter} and centers it. The view associated with the
     * {@code content}'s presenter must inherit from {@link PopupView}. The popup will be visible and the corresponding
     * presenter will receive the lifecycle events as needed.
     * <p>
     * No {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent ResetPresentersEvent} is fired, so {@link
     * PresenterWidget#onReset()} is not invoked.
     * <p>
     *
     * @param child The popup child, a {@link PresenterWidget}.
     */
    void addToPopupSlot(PresenterWidget<? extends PopupView> child);

    /**
     * This method removes popup content within the {@link Presenter}. The view associated with the {@code content}'s
     * presenter must inherit from {@link PopupView}.
     *
     * @param child The popup child, a {@link PresenterWidget}, which has previously been added using {@link
     * #addToPopupSlot(PresenterWidget)}.
     */
    void removeFromPopupSlot(PresenterWidget<? extends PopupView> child);
}
