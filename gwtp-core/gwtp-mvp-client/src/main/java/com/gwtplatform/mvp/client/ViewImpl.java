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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.presenter.slots.ISingleSlot;
import com.gwtplatform.mvp.client.presenter.slots.Slot;

/**
 * A simple implementation of {@link View} that simply disregard every call to {@link #setInSlot(Object, IsWidget)},
 * {@link #addToSlot(Object, IsWidget)}, and {@link #removeFromSlot(Object, IsWidget)}.
 * <p/>
 * Feel free not to inherit from this if you need another base class (such as {@link
 * com.google.gwt.user.client.ui.Composite Composite}), but you will have to define the above methods.
 * <p/>
 * * <b>Important</b> call {@link #initWidget(IsWidget)} in your {@link View}'s constructor.
 */
public abstract class ViewImpl implements View {
    private Widget widget;
    private Map<Object, HasOneWidget> oneWidgetSlots = new HashMap<Object, HasOneWidget>();

    private Map<Object,HasWidgets> hasWidgetSlots = new HashMap<Object, HasWidgets>();

    @Override
    public void addToSlot(Object slot, IsWidget content) {
        if (hasWidgetSlots.containsKey(slot)) {
            hasWidgetSlots.get(slot).add(content.asWidget());
        }
    }

    @Override
    public void removeFromSlot(Object slot, IsWidget content) {
        if (oneWidgetSlots.containsKey(slot)) {
            if (oneWidgetSlots.get(slot).getWidget() == content.asWidget()) {
                oneWidgetSlots.get(slot).setWidget(null);
            }
        } else if (hasWidgetSlots.containsKey(slot)) {
            hasWidgetSlots.get(slot).remove(content.asWidget());
        }
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (oneWidgetSlots.containsKey(slot)) {
            oneWidgetSlots.get(slot).setWidget(content);
        } else if (hasWidgetSlots.containsKey(slot)) {
            hasWidgetSlots.get(slot).clear();
            if (content != null) {
                hasWidgetSlots.get(slot).add(content.asWidget());
            }
        }
    }

    @Override
    public Widget asWidget() {
        if (widget == null) {
            throw new NullPointerException("widget cannot be null, you should call ViewImpl.initWidget() before.");
        }

        return widget;
    }

    /**
     * Link a slot to a container.
     * @param slot - the slot
     * @param container - the container must implement HasWidgets.ForIsWidget.
     */
    protected void bindSlot(ISingleSlot<?> slot, HasWidgets container) {
        internalBindSlot(slot, container);
    }

    /**
     * Link a slot to a container.
     * @param slot - the slot
     * @param container - the container must implement HasWidgets.ForIsWidget.
     */
    protected void bindSlot(Slot<?> slot, HasWidgets container) {
        internalBindSlot(slot, container);
    }

    protected void initWidget(IsWidget widget) {
        if (this.widget != null) {
            throw new IllegalStateException("ViewImpl.initWidget() may only be called once.");
        } else if (widget == null) {
            throw new NullPointerException("widget cannot be null");
        }

        this.widget = widget.asWidget();

        asWidget().addAttachHandler(new Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    onAttach();
                } else {
                    onDetach();
                }
            }
        });
    }

    /**
     * Method called after the view is attached to the DOM.
     * <p/>
     * You should override this method to perform any ui related initialization that needs to be done after
     * that the view is attached <b>and that the presenter doesn't have to be aware of</b> (attach event handlers
     * for instance)
     */
    protected void onAttach() {
    }

    /**
     * Method called after the view is detached to the DOM.
     * <p/>
     * You should override this method to release any resources created directly or indirectly during the
     * call to {@link #onAttach()}
     */
    protected void onDetach() {
    }

    private void internalBindSlot(Object slot, HasWidgets container) {
        if (container instanceof HasOneWidget) {
            oneWidgetSlots.put(slot, (HasOneWidget) container);
        } else {
            hasWidgetSlots.put(slot, container);
        }
    }
}
