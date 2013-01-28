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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;

/**
 * A simple implementation of {@link PopupView} that can be used when the widget
 * returned by {@link #asWidget()} inherits from {@link PopupPanel}.
 * <p/>
 * Also, this implementation simply disregards every call to
 * {@link #setInSlot(Object, com.google.gwt.user.client.ui.Widget)}, {@link #addToSlot(Object,
 * com.google.gwt.user.client.ui.Widget)}, and
 * {@link #removeFromSlot(Object, com.google.gwt.user.client.ui.Widget)}.
 * <p/>
 * <b>Important</b> call {@link #initWidget(com.google.gwt.user.client.ui.Widget)} in your {@link com.gwtplatform.mvp.client.View}'s
 * constructor.
 */
public abstract class PopupViewImpl extends ViewImpl implements PopupView {
    private HandlerRegistration autoHideHandler;
    private HandlerRegistration closeHandlerRegistration;
    
    private final EventBus eventBus;

    /**
     * The {@link PopupViewImpl} class uses the {@link EventBus} to listen to
     * {@link NavigationEvent} in order to automatically close when this event is
     * fired, if desired. See
     * {@link #setAutoHideOnNavigationEventEnabled(boolean)} for details.
     *
     * @param eventBus The {@link EventBus}.
     */
    protected PopupViewImpl(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void center() {
        doCenter();
        // We center again in a deferred command to solve a bug in IE where newly
        // created window are sometimes not centered.
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                doCenter();
            }
        });
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

    /**
     * Retrieves this view as a {@link PopupPanel}. See {@link #asWidget()}.
     *
     * @return This view as a {@link PopupPanel} object.
     */
    protected PopupPanel asPopupPanel() {
        return (PopupPanel) asWidget();
    }

    /**
     * This method centers the popup panel, temporarily making it visible if
     * needed.
     */
    private void doCenter() {
        // We can't use Element.center() method as it will show the popup
        // by default and not only centering it. This is resulting in onAttach()
        // being called twice when using setInSlot() or addToPopupSlot() in PresenterWidget

        // If left/top are set from a previous doCenter() call, and our content
        // has changed, we may get a bogus getOffsetWidth because our new content
        // is wrapping (giving a lower offset width) then it would without the
        // previous left. Clearing left/top to avoids this.
        PopupPanel popup = asPopupPanel();
        Element elem = popup.getElement();
        elem.getStyle().clearLeft();
        elem.getStyle().clearTop();

        int left = (Window.getClientWidth() - popup.getOffsetWidth()) >> 1;
        int top = (Window.getClientHeight() - popup.getOffsetHeight()) >> 1;
        popup.setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), Math.max(
                Window.getScrollTop() + top, 0));
    }
}
