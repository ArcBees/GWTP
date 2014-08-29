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

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HasWidgets.ForIsWidget;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.presenter.OrderedSlot;
import com.gwtplatform.mvp.client.presenter.Slot;
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
    private final Map<Object, HasOneWidget> singleSlots = new HashMap<Object, HasOneWidget>();
    private final Map<Object, HasWidgets.ForIsWidget> multiSlots = new HashMap<Object, HasWidgets.ForIsWidget>();
    private final Map<OrderedSlot<?>, HasWidgets.ForIsWidget>
        orderedSlots = new HashMap<OrderedSlot<?>, HasWidgets.ForIsWidget>();

    @Override
    public void addToSlot(Object slot, IsWidget content) {
        if (multiSlots.containsKey(slot)) {
            multiSlots.get(slot).add(content);
        } else if (orderedSlots.containsKey(slot)) {
            InsertPanel.ForIsWidget container = (InsertPanel.ForIsWidget) orderedSlots.get(slot);
            Comparable w = (Comparable) content;
            int min = 0;
            int max = container.getWidgetCount();
            while (min < max) {
                int mid = min + ((max - min) / 2);
                int compare = w.compareTo(container.getWidget(mid));
                if (compare == 0) {
                    max = mid;
                    break;
                } else if (compare > 0) {
                    min = mid + 1;
                } else {
                    max = mid;
                }
            }
            container.insert(content, max);
        }
    }

    @Override
    public void removeFromSlot(Object slot, IsWidget content) {
        if (singleSlots.containsKey(slot)) {
            if (singleSlots.get(slot).getWidget() == content) {
                singleSlots.get(slot).setWidget(null);
            }
        } else if (multiSlots.containsKey(slot)) {
            multiSlots.get(slot).remove(content);
        } else if (orderedSlots.containsKey(slot)) {
            orderedSlots.get(slot).remove(content);
        }
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (singleSlots.containsKey(slot)) {
            singleSlots.get(slot).setWidget(content);
        } else if (multiSlots.containsKey(slot)) {
            multiSlots.get(slot).clear();
            if (content != null) {
                multiSlots.get(slot).add(content);
            }
        } else if (orderedSlots.containsKey(slot)) {
            orderedSlots.get(slot).clear();
            if (content != null) {
                orderedSlots.get(slot).add(content);
            }
        }
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    protected void initWidget(Widget widget) {
        this.widget = widget;
    }

    @Override
    public void registerSlot(Slot<?> slot, HasWidgets.ForIsWidget container) {
        registerUnorderedSlot(slot, container);
    }

    @Override
    public <T extends HasWidgets.ForIsWidget & InsertPanel.ForIsWidget> void registerSlot(OrderedSlot<?> slot,
            T container) {
        orderedSlots.put(slot, container);
    }

    @Override
    public void registerSlot(Type<RevealContentHandler<?>> slot, ForIsWidget container) {
        registerUnorderedSlot(slot, container);
    }

    private void registerUnorderedSlot(Object slot, ForIsWidget container) {
        if (container instanceof HasOneWidget) {
            singleSlots.put(slot, (HasOneWidget) container);
        } else {
            multiSlots.put(slot, container);
        }
    }

    @Override
    public void registerHasOneWidgetSlot(Slot<?> slot, HasOneWidget container) {
        singleSlots.put(slot, container);
    }

    @Override
    public void registerHasOneWidgetSlot(Type<RevealContentHandler<?>> slot, HasOneWidget container) {
        singleSlots.put(slot, container);
    }
}
