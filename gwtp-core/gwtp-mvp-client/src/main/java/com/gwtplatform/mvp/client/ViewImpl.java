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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.presenter.slots.OrderedSlot;
import com.gwtplatform.mvp.client.presenter.slots.SingleSlot;
import com.gwtplatform.mvp.client.presenter.slots.Slot;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * A simple implementation of {@link View} that simply disregard every call to
 * {@link #setInSlot(Object, IsWidget)}, {@link #addToSlot(Object, IsWidget)}, and
 * {@link #removeFromSlot(Object, IsWidget)}.
 * <p/>
 * Feel free not to inherit from this if you need another base class (such as
 * {@link com.google.gwt.user.client.ui.Composite}), but you will have to define
 * the above methods.
 * <p/>
 * * <b>Important</b> call {@link #initWidget(Widget)} in your {@link com.gwtplatform.mvp.client.View}'s
 * constructor.
 */
public abstract class ViewImpl implements View {
    private Widget widget;
    private Map<Object, HasOneWidget> oneWidgetSlots = new HashMap<Object, HasOneWidget>();

    private Map<Object,HasWidgets> hasWidgetSlots = new HashMap<Object, HasWidgets>();

    private Map<OrderedSlot<?>, List<Comparable<Comparable<?>>>> orderedSlots
        = new HashMap<OrderedSlot<?>, List<Comparable<Comparable<?>>>>();

    @SuppressWarnings("unchecked")
    @Override
    public void addToSlot(Object slot, IsWidget content) {
        if (hasWidgetSlots.containsKey(slot)) {
            if (orderedSlots.containsKey(slot)) {
                List<Comparable<Comparable<?>>> list = orderedSlots.get(slot);
                list.add((Comparable<Comparable<?>>) content);
                Collections.sort(list);
                int index = Collections.binarySearch(list, (Comparable<Comparable<?>>) content);

                InsertPanel insertPanel = (InsertPanel) hasWidgetSlots.get(slot);
                insertPanel.insert(content.asWidget(), index);
            } else {
                hasWidgetSlots.get(slot).add(content.asWidget());
            }
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
            if (orderedSlots.containsKey(slot)) {
                orderedSlots.get(slot).remove(content);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (oneWidgetSlots.containsKey(slot)) {
            oneWidgetSlots.get(slot).setWidget(content);
        } else if (hasWidgetSlots.containsKey(slot)) {
            hasWidgetSlots.get(slot).clear();
            if (content != null) {
                hasWidgetSlots.get(slot).add(content.asWidget());
            }
            if (orderedSlots.containsKey(slot)) {
                orderedSlots.get(slot).clear();
                if (content != null) {
                    orderedSlots.get(slot).add((Comparable<Comparable<?>>) content);
                }
            }
        }
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    /**
     * Link a slot to a container
     * @param slot - the slot
     * @param container - the container must implement HasWidgets.ForIsWidget.
     */
    protected void bindSlot(GwtEvent.Type<RevealContentHandler<?>> slot, HasWidgets container) {
        internalBindSlot(slot, container);
    }

    /**
     * Link a slot to a container
     * @param slot - the slot
     * @param container - the container must implement HasWidgets.ForIsWidget.
     */
    protected void bindSlot(SingleSlot<?> slot, HasWidgets container) {
        internalBindSlot(slot, container);
    }

    /**
     * Link a slot to a container
     * @param slot - the slot
     * @param container - the container must implement HasWidgets.ForIsWidget.
     */
    protected void bindSlot(Slot<?> slot, HasWidgets container) {
        internalBindSlot(slot, container);
    }

    /**
     * Link a slot to a container
     * @param slot - the slot
     * @param container - the container must implement HasWidgets.ForIsWidget.
     */
    protected <T extends HasWidgets & InsertPanel> void bindSlot(
            OrderedSlot<?> slot, T container) {
        orderedSlots.put(slot, new ArrayList<Comparable<Comparable<?>>>());
        hasWidgetSlots.put(slot, container);
    }

    protected void initWidget(Widget widget) {
        this.widget = widget;
    }

    private void internalBindSlot(Object slot, HasWidgets container) {
        if (container instanceof HasOneWidget) {
            oneWidgetSlots.put(slot, (HasOneWidget) container);
        } else {
            hasWidgetSlots.put(slot, container);
        }
    }
}
