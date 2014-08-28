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

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.InsertPanel.ForIsWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.presenter.ManySlot;
import com.gwtplatform.mvp.client.presenter.OrderedSlot;
import com.gwtplatform.mvp.client.presenter.SingleSlot;
import com.gwtplatform.mvp.client.presenter.Slot;

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
    private final Map<Slot<?>, HasOneWidget> singleSlots = new HashMap<Slot<?>, HasOneWidget>();
    private final Map<Slot<?>, HasWidgets> multiSlots = new HashMap<Slot<?>, HasWidgets>();
    private final Map<Slot<?>, HasWidgets> orderedSlots = new HashMap<Slot<?>, HasWidgets>();

    @Override
    public void addToSlot(Object slot, IsWidget content) {
        if (multiSlots.containsKey(slot)) {
            multiSlots.get(slot).add(content.asWidget());
        } else if (orderedSlots.containsKey(slot)) {
            ForIsWidget container = (ForIsWidget) orderedSlots.get(slot);
            Comparable w = (Comparable) content;
            int i;
            for (i = 0; i < container.getWidgetCount(); i++) {
                if (w.compareTo(container.getWidget(i)) >= 0) {
                    break;
                }
            }
            container.insert(content, i);
        }
    }

    @Override
    public void removeFromSlot(Object slot, IsWidget content) {
        if (singleSlots.containsKey(slot)) {
            if (singleSlots.get(slot).getWidget() == content) {
                singleSlots.get(slot).setWidget(null);
            }
        } else if (multiSlots.containsKey(slot)) {
            multiSlots.get(slot).remove(content.asWidget());
        } else if (orderedSlots.containsKey(slot)) {
            orderedSlots.get(slot).remove(content.asWidget());
        }
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (singleSlots.containsKey(slot)) {
            singleSlots.get(slot).setWidget(content);
        } else if (multiSlots.containsKey(slot)) {
            multiSlots.get(slot).clear();
            if (content != null) {
                multiSlots.get(slot).add(content.asWidget());
            }
        } else if (orderedSlots.containsKey(slot)) {
            orderedSlots.get(slot).clear();
            if (content != null) {
                orderedSlots.get(slot).add(content.asWidget());
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
    public <T extends HasOneWidget> void registerSlot(SingleSlot<?> slot, T container) {
        singleSlots.put(slot, container);
    }

    @Override
    public <T extends HasWidgets> void registerSlot(SingleSlot<?> slot, T container) {
        multiSlots.put(slot, container);
    }

    @Override
    public <T extends HasWidgets> void registerSlot(ManySlot<?> slot, T container) {
        multiSlots.put(slot, container);
    }

    @Override
    public <T extends HasWidgets & ForIsWidget> void registerSlot(OrderedSlot<?> slot, T container) {
        orderedSlots.put(slot, container);
    }

}
