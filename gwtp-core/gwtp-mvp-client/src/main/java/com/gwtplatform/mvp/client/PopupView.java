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

import com.gwtplatform.mvp.client.view.PopupPositioner;

/**
 * The interface for {@link View} classes that are meant to be displayed as popup, like a GWT {@link
 * com.google.gwt.user.client.ui.PopupPanel PopupPanel} or a {@link com.google.gwt.user.client.ui.DialogBox DialogBox}.
 */
public interface PopupView extends View {
    /**
     * Make sure the {@link PopupView} is hidden. You can call this method directly.
     */
    void hide();

    /**
     * Indicates that the view should automatically hide when a {@link com.gwtplatform.mvp.client.proxy.NavigationEvent
     * NavigationEvent} is fired. This is better than using GWT's {@link
     * com.google.gwt.user.client.ui.PopupPanel#setAutoHideOnHistoryEventsEnabled(boolean)
     * PopupPanel#setAutoHideOnHistoryEventsEnabled} since the latter will automatically hide the dialog even if
     * navigation is refused through {@link com.gwtplatform.mvp.client.proxy.PlaceManager#setOnLeaveConfirmation(String)
     * PlaceManager#setOnLeaveConfirmation} .
     */
    void setAutoHideOnNavigationEventEnabled(boolean autoHide);

    /**
     * Identifies which {@link PopupViewCloseHandler} should be called when this view closed (either automatically or
     * through a call to {@link #hide()}.
     *
     * @param popupViewCloseHandler The {@link PopupViewCloseHandler} or {@code null} to unregister any handlers.
     */
    void setCloseHandler(PopupViewCloseHandler popupViewCloseHandler);

    /**
     * <b>Important!</b> Do not call this directly. Instead use {@link PresenterWidget#addToPopupSlot(PresenterWidget)},
     * passing this view's {@link PresenterWidget}.
     * <p>
     * Make sure the {@link PopupView} is visible. Will not reposition the popup before showing it.
     */
    void show();

    /**
     * You don't need to call this directly. It is automatically called during {@link PresenterWidget#onReveal()}. Will
     * position the popup before showing it.
     */
    void showAndReposition();

    /**
     * Set the {@link PopupPositioner} which will position this popup when it's presenter is revealed.
     *
     * @param positioner The {@link PopupPositioner} will automatically position the popup before {@link
     * PresenterWidget#onReveal()};
     *
     * @see com.gwtplatform.mvp.client.view.CenterPopupPositioner
     * @see com.gwtplatform.mvp.client.view.RelativeToWidgetPopupPositioner
     * @see com.gwtplatform.mvp.client.view.TopLeftPopupPositioner
     */
    void setPopupPositioner(PopupPositioner positioner);
}
