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

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HasWidgets.ForIsWidget;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.presenter.slots.ISlot;
import com.gwtplatform.mvp.client.presenter.slots.OrderedSlot;
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
    private final Map<Object, HasWidgets.ForIsWidget>
        orderedSlots = new HashMap<Object, HasWidgets.ForIsWidget>();
    private final Map<Object, List<Comparable<Comparable<?>>>>
    orderedSlotComparators = new HashMap<Object, List<Comparable<Comparable<?>>>>();

    @Override
    public void addToSlot(Object slot, IsWidget content) {
        if (multiSlots.containsKey(slot)) {
            multiSlots.get(slot).add(content);
        } else if (orderedSlots.containsKey(slot)) {
            if (!orderedSlotComparators.containsKey(slot)) {
                orderedSlotComparators.put(slot, new ArrayList<Comparable<Comparable<?>>>());
            }
            orderedSlotComparators.get(slot).add((Comparable<Comparable<?>>) content);
            Collections.sort(orderedSlotComparators.get(slot));
            int index = Collections.binarySearch(orderedSlotComparators.get(slot),
                    (Comparable<Comparable<?>>) content);
            InsertPanel.ForIsWidget container = (InsertPanel.ForIsWidget) orderedSlots.get(slot);
            container.insert(content, index);
        }
    }

    @Override
    public void removeFromSlot(Object slot, IsWidget content) {
        if (singleSlots.containsKey(slot)) {
            if (singleSlots.get(slot).getWidget() == content.asWidget()) {
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
    public void registerSlot(Class<? extends ISlot<?>> slot, ForIsWidget container) {
        registerUnorderedSlot(slot, container);
    }

    @Override
    public void registerHasOneWidgetSlot(Class<? extends ISlot<?>> slot, HasOneWidget container) {
        singleSlots.put(slot, container);
    }

    @Override
    public void registerHasOneWidgetSlot(Type<RevealContentHandler<?>> slot, HasOneWidget container) {
        singleSlots.put(slot, container);
    }

    @Override
    public <T extends ForIsWidget & com.google.gwt.user.client.ui.InsertPanel.ForIsWidget> void registerOrderedSlot(
            Class<? extends OrderedSlot<?>> slot, T container) {
        orderedSlots.put(slot, container);
    }
}
