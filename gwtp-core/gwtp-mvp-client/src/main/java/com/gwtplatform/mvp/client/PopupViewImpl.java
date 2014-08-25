/**
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

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import com.gwtplatform.mvp.client.view.CenterPopupPositioner;
import com.gwtplatform.mvp.client.view.PopupPositioner;
import com.gwtplatform.mvp.client.view.PopupPositioner.PopupPosition;

/**
 * A simple implementation of {@link PopupView} that can be used when the widget
 * returned by {@link #asWidget()} inherits from {@link PopupPanel}.
 * <p/>
 * Also, this implementation simply disregards every call to
 * {@link #setInSlot(Object, com.google.gwt.user.client.ui.IsWidget)}, {@link #addToSlot(Object,
 * com.google.gwt.user.client.ui.IsWidget)}, and
 * {@link #removeFromSlot(Object, com.google.gwt.user.client.ui.IsWidget)}.
 */
public abstract class PopupViewImpl extends ViewImpl implements PopupView {
    private HandlerRegistration autoHideHandler;

    private HandlerRegistration closeHandlerRegistration;
    private final EventBus eventBus;

    private final PopupPositioner positioner;

    /**
     * By default the popup will position itself in the center of the window.
     * To use a different positioner use {@link #PopupViewImpl(EventBus, PopupPosition)} instead.
     * @param eventBus The {@link EventBus}.
     */
    public PopupViewImpl(EventBus eventBus) {
        this(eventBus, new CenterPopupPositioner());
    }

    /**
     * @param eventBus The {@link EventBus}.
     * @param positioner The {@link PopupPositioner} used to position the popup onReveal();
     * @see
     * {@link com.gwtplatform.mvp.client.view.CenterPopupPositioner},
     * {@link com.gwtplatform.mvp.client.view.RelativeToWidgetPopupPositioner},
     * {@link com.gwtplatform.mvp.client.view.TopLeftPopupPositioner},
     */
    protected PopupViewImpl(EventBus eventBus, PopupPositioner positioner) {
        this.eventBus = eventBus;
        this.positioner = positioner;
    }

    @Override
    public void hide() {
        asPopupPanel().hide();
    }

    @Override
    public void setAutoHideOnNavigationEventEnabled(boolean autoHide) {
        if (autoHide) {
            if (autoHideHandler != null) {
                return;
            }
            autoHideHandler = eventBus.addHandler(NavigationEvent.getType(),
                    new NavigationHandler() {
                @Override
                public void onNavigation(NavigationEvent navigationEvent) {
                    hide();
                }
            });
        } else {
            if (autoHideHandler != null) {
                autoHideHandler.removeHandler();
            }
        }
    }

    @Override
    public void setCloseHandler(final PopupViewCloseHandler popupViewCloseHandler) {
        if (closeHandlerRegistration != null) {
            closeHandlerRegistration.removeHandler();
        }
        if (popupViewCloseHandler == null) {
            closeHandlerRegistration = null;
        } else {
            closeHandlerRegistration = asPopupPanel().addCloseHandler(
                    new CloseHandler<PopupPanel>() {
                        @Override
                        public void onClose(CloseEvent<PopupPanel> event) {
                            popupViewCloseHandler.onClose();
                        }
                    });
        }
    }

    @Override
    public void setPosition(int left, int top) {
        asPopupPanel().setPopupPosition(left, top);
    }

    @Override
    public void show() {
        asPopupPanel().show();
    }

    @Override
    public void showAndReposition() {
        asPopupPanel().setPopupPositionAndShow(new PositionCallback() {

            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                PopupPosition popupPosition = positioner.getPopupPosition(offsetWidth, offsetHeight);
                asPopupPanel().setPopupPosition(popupPosition.getLeft(), popupPosition.getTop());
            }
        });
    }

    /**
     * Retrieves this view as a {@link PopupPanel}. See {@link #asWidget()}.
     *
     * @return This view as a {@link PopupPanel} object.
     */
    protected PopupPanel asPopupPanel() {
        return (PopupPanel) asWidget();
    }
}
