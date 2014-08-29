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

package com.gwtplatform.mvp.client.presenter;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.GenericPresenterWidget;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.presenter.slots.AbstractSlot;
import com.gwtplatform.mvp.client.presenter.slots.MultiSlot;
import com.gwtplatform.mvp.client.presenter.slots.OrderedSlot;
import com.gwtplatform.mvp.client.presenter.slots.SingleSlot;
import com.gwtplatform.mvp.client.presenter.slots.Slot;

/**
 * A presenter that does not have to be a singleton. Pages from your
 * application will usually be singletons and extend the {@link Presenter} class.
 * <p/>
 * Choosing between a {@link Presenter} and {@link PresenterWidget} is a design decision that
 * requires some thought. For example, a {@link PresenterWidget} is useful when
 * you need a custom widget with extensive logic. For example, a chat box that can be instantiated
 * multiple times and needs to communicate with the server would be a good candidate for
 * a {@link PresenterWidget}. The drawback of a {@link PresenterWidget} is that it is managed by its
 * parent presenter, which increases coupling. Therefore, you should use a {@link Presenter} when
 * the parent is not expected to know its child. Moreover, only {@link Presenter} can be attached
 * to name tokens in order to support browser history.
 * <p/>
 * {@link PresenterWidget}s and {@link Presenter}s are organized in a hierarchy.
 * Internally, parent presenters have links to their currently attached children presenters. A
 * parent {@link Presenter} can contain either {@link Presenter}s or {@link PresenterWidget}s,
 * but a {@link PresenterWidget} can only contain {@link PresenterWidget}s.
 * <p/>
 * To reveal a {@link PresenterWidget} you should insert it within a {@link HasSlots slot} of its
 * containing presenter using one of the following methods:
 * <ul>
 * <li>{@link #setInSlot(Object, PresenterWidget)}
 * <li>{@link #setInSlot(Object, PresenterWidget, boolean)}
 * <li>{@link #addToSlot(Object, PresenterWidget)}
 * <li>{@link #addToPopupSlot(PresenterWidget)}
 * <li>{@link #addToPopupSlot(PresenterWidget, boolean)}
 * </ul>
 * Revealing a {@link Presenter} is done differently, refer to the class documentation for more details.
 * <p/>
 * To hide a {@link PresenterWidget} or a {@link Presenter} you can use {@link #setInSlot} to place
 * another presenter in the same slot, or you can call one of the following methods:
 * <ul>
 * <li>{@link #removeFromSlot(Object, PresenterWidget)}
 * <li>{@link #clearSlot(Object)}
 * <li>{@link PopupView#hide()} if the presenter is a popup or a dialog box.
 * </ul>
 * Hide a {@link Presenter} using these methods, but
 * <p/>
 * A presenter has a number of lifecycle methods that you can hook on to:
 * <ul>
 * <li>{@link #onBind()}
 * <li>{@link #onReveal()}
 * <li>{@link #onReset()}
 * <li>{@link #onHide()}
 * <li>{@link #onUnbind()}
 * </ul>
 * Revealing or hiding a {@link PresenterWidget} triggers an internal chain of events that result in
 * these lifecycle methods being called. For an example, here is what happens following
 * a call to {@link #setInSlot(Object, PresenterWidget)}:
 * <ul>
 * <li>If a presenter already occupies this slot it is removed.</li>
 * <ul><li>If the presenter owning the slot is currently visible then
 * {@link #onHide()} is called on the removed presenter and, recursively,
 * on its children (bottom-up: first the children, then the parent)</li>
 * <li>If the parent is not visible and is a {@link Presenter}, it asks to be
 * set in one of its parent slot by firing a
 * {@link com.gwtplatform.mvp.client.proxy.RevealContentEvent RevealContentEvent}.
 * For more details, see the documentation for {@link Presenter}.</li>
 * </ul>
 * <li>If, at this point, the presenter owning the slot is not visible, then the
 * chain stops. Otherwise, {@link #onReveal()} is called on the {@link PresenterWidget} that
 * was just added.</li>
 * <li>{@link #onReveal()} is called recursively on that presenter's children
 * (top-down: first the parent, then the children).</li>
 * <li>If {@link #setInSlot(Object, PresenterWidget, boolean)} was called with {@code false}
 * as the third parameter then the process stops. Otherwise, {@link #onReset()} is
 * called on all the currently visible presenters (top-down: first the parent, then
 * the children).</li>
 * </ul>
 *
 * @param <V> The {@link View} type.
 */
public abstract class PresenterWidget<V extends View> extends GenericPresenterWidget<AbstractSlot<?>,MultiSlot<?>, V>
    implements HasSlots {
    public PresenterWidget(boolean autoBind, EventBus eventBus, V view) {
        super(autoBind, eventBus, view);
    }

    public PresenterWidget(EventBus eventBus, V view) {
        super(eventBus, view);
    }

    @Override
    public <T extends PresenterWidget<?>> Set<T> getSlotsChildren(Slot<T> slot) {
        return unsafeGetChildren(slot);
    }

    @Override
    public <T extends PresenterWidget<?> & Comparable<T>> SortedSet<T> getSlotsChildren(OrderedSlot<T> slot) {
        return new TreeSet<T>(unsafeGetChildren(slot));
    }

    @Override
    public <T extends PresenterWidget<?>> T getSlotChild(SingleSlot<T> slot) {
        Iterator<T> it = unsafeGetChildren(slot).iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
}

