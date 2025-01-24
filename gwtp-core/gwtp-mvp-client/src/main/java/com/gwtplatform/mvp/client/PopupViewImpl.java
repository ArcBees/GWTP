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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.view.CenterPopupPositioner;
import com.gwtplatform.mvp.client.view.PopupPositioner;
import com.gwtplatform.mvp.client.view.PopupPositioner.PopupPosition;

/**
 * A simple implementation of {@link PopupView} that can be used when the widget returned by {@link #asWidget()}
 * inherits from {@link PopupPanel}.
 * <p>
 * Also, this implementation simply disregards every call to {@link #setInSlot(Object, IsWidget)}, {@link
 * #addToSlot(Object, IsWidget)}, and {@link #removeFromSlot(Object, IsWidget)}.
 */
public abstract class PopupViewImpl extends ViewImpl implements PopupView {
    private HandlerRegistration autoHideHandler;

    private final EventBus eventBus;

    private HandlerRegistration closeHandlerRegistration;
    private PopupViewCloseHandler popupViewCloseHandler;
    private PopupPositioner positioner;

    /**
     * By default the popup will position itself in the center of the window. To use a different positioner use {@link
     * #PopupViewImpl(EventBus, PopupPositioner)} instead.
     *
     * @param eventBus The {@link EventBus}.
     */
    protected PopupViewImpl(EventBus eventBus) {
        this(eventBus, new CenterPopupPositioner());
    }

    /**
     * @param eventBus The {@link EventBus}.
     * @param positioner The {@link PopupPositioner} used to position the popup onReveal();
     *
     * @see com.gwtplatform.mvp.client.view.CenterPopupPositioner
     * @see com.gwtplatform.mvp.client.view.RelativeToWidgetPopupPositioner
     * @see com.gwtplatform.mvp.client.view.TopLeftPopupPositioner
     */
    protected PopupViewImpl(
            EventBus eventBus,
            PopupPositioner positioner) {
        this.eventBus = eventBus;
        setPopupPositioner(positioner);

        if (repositionOnWindowResize()) {
            Window.addResizeHandler(event -> {
                if (asPopupPanel().isShowing()) {
                    showAndReposition();
                }
            });
        }
    }

    @Override
    public void hide() {
        asPopupPanel().hide();

        // events will not fire call closeHandler manually.
        if (popupViewCloseHandler != null) {
            popupViewCloseHandler.onClose();
        }
    }

    @Override
    public void setAutoHideOnNavigationEventEnabled(boolean autoHide) {
        if (autoHide) {
            if (autoHideHandler != null) {
                return;
            }
            autoHideHandler = eventBus.addHandler(NavigationEvent.getType(), navigationEvent -> hide());
        } else {
            if (autoHideHandler != null) {
                autoHideHandler.removeHandler();
            }
        }
    }

    @Override
    public void setCloseHandler(final PopupViewCloseHandler popupViewCloseHandler) {
        this.popupViewCloseHandler = popupViewCloseHandler;
        if (closeHandlerRegistration == null) {
            closeHandlerRegistration = asPopupPanel().addCloseHandler(event -> {
                if (PopupViewImpl.this.popupViewCloseHandler != null) {
                    PopupViewImpl.this.popupViewCloseHandler.onClose();
                }
            });
        }
    }

    @Override
    public void setPopupPositioner(PopupPositioner popupPositioner) {
        this.positioner = popupPositioner;
    }

    @Override
    public void show() {
        asPopupPanel().show();
    }

    @Override
    public void showAndReposition() {
        onReposition();
        asPopupPanel().setPopupPositionAndShow((offsetWidth, offsetHeight) -> {
            PopupPosition popupPosition = positioner.getPopupPosition(offsetWidth, offsetHeight);
            asPopupPanel().setPopupPosition(popupPosition.getLeft(), popupPosition.getTop());
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

    /**
     * Override this method to add custom logic that runs before the popup is repositioned. By default the popup will be
     * repositioned on resize and this method will be called. So you can add any resize logic here as well.
     */
    protected void onReposition() {
    }

    /**
     * By default PopupViews will reposition themselves when the window is resized. If you don't want the popup to be
     * repositioned or want to handle the resize yourself overide this method to return false.
     *
     * @return whether to reposition on resize.
     */
    protected boolean repositionOnWindowResize() {
        return true;
    }

    private native void hidePopup(PopupPanel popupPanel) /*-{
        var resizeAnimation = popupPanel.@com.google.gwt.user.client.ui.PopupPanel::resizeAnimation;
        resizeAnimation.@com.google.gwt.user.client.ui.PopupPanel.ResizeAnimation::setState(ZZ)(false, false);
    }-*/;
}
