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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.presenter.slots.IsSingleSlot;
import com.gwtplatform.mvp.client.presenter.slots.IsSlot;
import com.gwtplatform.mvp.client.presenter.slots.LegacySlotConvertor;
import com.gwtplatform.mvp.client.presenter.slots.MultiSlot;
import com.gwtplatform.mvp.client.presenter.slots.OrderedSlot;
import com.gwtplatform.mvp.client.presenter.slots.PopupSlot;
import com.gwtplatform.mvp.client.presenter.slots.RemovableSlot;
import com.gwtplatform.mvp.client.presenter.slots.Slot;
import com.gwtplatform.mvp.client.proxy.ResetPresentersEvent;

/**
 * A presenter that does not have to be a singleton. Pages from your
 * application will usually be singletons and extend the {@link Presenter} class.
 * <p>
 * Choosing between a {@link Presenter} and {@link PresenterWidget} is a design decision that
 * requires some thought. For example, a {@link PresenterWidget} is useful when
 * you need a custom widget with extensive logic. For example, a chat box that can be instantiated
 * multiple times and needs to communicate with the server would be a good candidate for
 * a {@link PresenterWidget}. The drawback of a {@link PresenterWidget} is that it is managed by its
 * parent presenter, which increases coupling. Therefore, you should use a {@link Presenter} when
 * the parent is not expected to know its child. Moreover, only {@link Presenter} can be attached
 * to name tokens in order to support browser history.
 * <p>
 * {@link PresenterWidget}s and {@link Presenter}s are organized in a hierarchy.
 * Internally, parent presenters have links to their currently attached children presenters. A
 * parent {@link Presenter} can contain either {@link Presenter}s or {@link PresenterWidget}s,
 * but a {@link PresenterWidget} can only contain {@link PresenterWidget}s.
 * <p>
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
 * <p>
 * To hide a {@link PresenterWidget} or a {@link Presenter} you can use {@link #setInSlot} to place
 * another presenter in the same slot, or you can call one of the following methods:
 * <ul>
 * <li>{@link #removeFromSlot(Object, PresenterWidget)}
 * <li>{@link #clearSlot(Object)}
 * <li>{@link PopupView#hide()} if the presenter is a popup or a dialog box.
 * </ul>
 * Hide a {@link Presenter} using these methods, but
 * <p>
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
 * <li>If a presenter already occupies this slot it is removed.
 * <ul><li>If the presenter owning the slot is currently visible then
 * {@link #onHide()} is called on the removed presenter and, recursively,
 * on its children (bottom-up: first the children, then the parent)</li>
 * <li>If the parent is not visible and is a {@link Presenter}, it asks to be
 * set in one of its parent slot by firing a
 * {@link com.gwtplatform.mvp.client.proxy.RevealContentEvent RevealContentEvent}.
 * For more details, see the documentation for {@link Presenter}.</li>
 * </ul></li>
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
public abstract class PresenterWidget<V extends View> extends HandlerContainerImpl implements HasHandlers, HasSlots,
        HasPopupSlot, IsWidget {
    private static class HandlerInformation<H extends EventHandler> {
        private final Type<H> type;
        private final H eventHandler;

        private HandlerInformation(Type<H> type, H eventHandler) {
            this.type = type;
            this.eventHandler = eventHandler;
        }
    }

    private static final PopupSlot<PresenterWidget<? extends PopupView>>
            POPUP_SLOT = new PopupSlot<PresenterWidget<? extends PopupView>>();

    // Package-private because in JDK 7 you can no longer access private members of the same type.
    // http://bugs.java.com/view_bug.do?bug_id=6904536
    PresenterWidget<?> parent;
    IsSlot<?> slot;
    boolean visible;

    private final EventBus eventBus;
    private final V view;
    private final List<HandlerInformation<? extends EventHandler>>
            visibleHandlers = new ArrayList<HandlerInformation<? extends EventHandler>>();
    private final List<HandlerRegistration> visibleHandlerRegistrations = new ArrayList<HandlerRegistration>();
    private final Set<PresenterWidget<?>> children = new HashSet<PresenterWidget<?>>();

    /**
     * Creates a {@link PresenterWidget} that is not necessarily using automatic
     * binding. Automatic binding will only work when instantiating this object using
     * Guice/GIN dependency injection. See
     * {@link HandlerContainerImpl#HandlerContainerImpl(boolean)} for
     * more details on automatic binding.
     *
     * @param autoBind {@code true} to request automatic binding, {@code false} otherwise.
     * @param eventBus The {@link EventBus}.
     * @param view     The {@link View}.
     */
    public PresenterWidget(boolean autoBind, EventBus eventBus, V view) {
        super(autoBind);

        assert view != null : "presenter view cannot be null";
        this.eventBus = eventBus;
        this.view = view;
    }

    /**
     * Creates a {@link PresenterWidget} that uses automatic binding. This will
     * only work when instantiating this object using Guice/GIN dependency injection.
     * See {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on
     * automatic binding.
     *
     * @param eventBus The {@link EventBus}.
     * @param view     The {@link View}.
     */
    public PresenterWidget(EventBus eventBus, V view) {
        this(true, eventBus, view);
    }

    @Override
    public void addToPopupSlot(PresenterWidget<? extends PopupView> child) {
        addToSlot(POPUP_SLOT, child);
    }

    @Override
    public void addToPopupSlot(PresenterWidget<? extends PopupView> child, boolean center) {
        addToPopupSlot(child);
    }

    /**
     * This method adds some content in a specific slot of the {@link Presenter}.
     * The attached {@link View} should manage this slot when its
     * {@link View#addToSlot(Object, com.google.gwt.user.client.ui.Widget)} is called.
     * <p>
     * Contrary to the {@link #setInSlot} method, no
     * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} is fired,
     * so {@link PresenterWidget#onReset()} is not invoked.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot An opaque object identifying which slot this content is being
     * added into.
     * @param content The content, a {@link PresenterWidget}. Passing {@code null}
     * will not add anything.
     * @deprecated since 1.5. Use {@link #addToSlot(MultiSlot, PresenterWidget)} instead.
     */
    @Override
    @Deprecated
    public void addToSlot(Object slot, PresenterWidget<?> content) {
        addToSlot(LegacySlotConvertor.convert(slot), content);
    }

    /**
     * This method adds some content in a specific slot of the {@link Presenter}.
     * The attached {@link View} should manage this slot when its
     * {@link View#addToSlot(Object, com.google.gwt.user.client.ui.Widget)} is called.
     * <p>
     * Contrary to the {@link #setInSlot(IsSlot, PresenterWidget)} method, no
     * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} is fired,
     * so {@link PresenterWidget#onReset()} is not invoked.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot The slot into which the content is being added.
     * @param child The content, a {@link PresenterWidget}. Passing {@code null}
     * will not add anything.
     */
    @Override
    public <T extends PresenterWidget<?>> void addToSlot(MultiSlot<T> slot, T child) {
        assert child != null : "cannot add null to a slot";

        if (child.slot == slot && child.parent == this) {
            return;
        }

        adoptChild(slot, child);

        if (!child.isPopup()) {
            getView().addToSlot(slot.getRawSlot(), child);
        }
        if (isVisible()) {
            child.internalReveal();
        }
    }

    @Override
    public Widget asWidget() {
        return getView().asWidget();
    }

    /**
    * This method clears the content in a specific slot. No
    * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} is fired.
    * The attached {@link View} should manage this slot when its
    * {@link View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} is called. It should also clear
    * the slot when the {@link View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} method is
    * called with {@code null} as a parameter.
    * <p>
    * For more details on slots, see {@link HasSlots}.
    *
    * @param slot An opaque object identifying which slot to clear.
    * @deprecated since 1.5. Use {@link #clearSlot(RemovableSlot)} instead.
    */
    @Override
    @Deprecated
    public void clearSlot(Object slot) {
        clearSlot(LegacySlotConvertor.convert(slot));
    }

    /**
     * This method clears the content in a specific slot. No
     * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} is fired.
     * The attached {@link View} should manage this slot when its
     * {@link View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} is called. It should also clear
     * the slot when the {@link View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} method is
     * called with {@code null} as a parameter.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot Specific slot to clear.
     */
    @Override
    public void clearSlot(RemovableSlot<?> slot) {
        internalClearSlot(slot, null);
        getView().setInSlot(slot.getRawSlot(), null);
    }

    private void internalClearSlot(IsSlot<?> slot, PresenterWidget<?> dontRemove) {
        // use new set to prevent concurrent modification
        for (PresenterWidget<?> child: new HashSet<PresenterWidget<?>>(children)) {
            if (child.slot == slot && !child.equals(dontRemove)) {
                child.orphan();
            }
        }
    }

    /**
     * PresenterWidgets may only be equal to the same instance.
     * To ensure this contract you may not override this method.
     * @param obj - the object to compare
     * @return whether the obj is the same instance as this presenter.
     */
    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        fireEvent((Event<?>) event);
    }

   /**
    * Fires the given event to the handlers listening to the event's type.
    * <p>
    * Any exceptions thrown by handlers will be bundled into a
    * {@link com.google.gwt.event.shared.UmbrellaException UmbrellaException} and then re-thrown after all handlers have
    * completed. An exception thrown by a handler will not prevent other handlers
    * from executing.
    *
    * @param event the event
    */
    public void fireEvent(Event<?> event) {
        getEventBus().fireEventFromSource(event, this);
    }

    /**
     * Returns the {@link View} for the current presenter.
     *
     * @return The view.
     */
    public V getView() {
        return view;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Verifies if the presenter is currently visible on the screen. A presenter
     * should be visible if it successfully revealed itself and was not hidden
     * later.
     *
     * @return {@code true} if the presenter is visible, {@code false} otherwise.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Removes this presenter from its parent.
     * If this presenter has no parent, this method does nothing.
     */
    public void removeFromParentSlot() {
        if (parent == null) {
            return;
        }

        parent.rawRemoveFromSlot(slot, this);
    }

    @Override
    public void removeFromPopupSlot(PresenterWidget<? extends PopupView> child) {
        removeFromSlot(POPUP_SLOT, child);
    }

    /**
    * This method removes some content in a specific slot of the
    * {@link Presenter}. No
    * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} is fired.
    * The attached {@link View} should manage this slot when its
    * {@link View#removeFromSlot(Object, com.google.gwt.user.client.ui.Widget)} is called.
    * <p>
    * For more details on slots, see {@link HasSlots}.
    *
    * @param slot An opaque object identifying which slot this content is being
    * removed from.
    * @param content The content, a {@link PresenterWidget}. Passing {@code null}
    * will not remove anything.
    * @deprecated since 1.5. Use {@link #removeFromSlot(RemovableSlot, PresenterWidget)} instead.
    */
    @Override
    @Deprecated
    public void removeFromSlot(Object slot, PresenterWidget<?> content) {
        removeFromSlot(LegacySlotConvertor.convert(slot), content);
    }

    /**
     * This method removes some content in a specific slot of the
     * {@link Presenter}. No
     * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} is fired.
     * The attached {@link View} should manage this slot when its
     * {@link View#removeFromSlot(Object, com.google.gwt.user.client.ui.Widget)} is called.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot  The slot for which the content is being removed.
     * @param child The content, a {@link PresenterWidget}. Passing {@code null}
     * will not remove anything.
     */
    @Override
    public <T extends PresenterWidget<?>> void removeFromSlot(RemovableSlot<T> slot, T child) {
        rawRemoveFromSlot(slot, child);
    }

    private void rawRemoveFromSlot(IsSlot<?> slot, PresenterWidget<?> child) {
        if (child == null || child.slot != slot) {
            return;
        }

        if (!child.isPopup()) {
            getView().removeFromSlot(slot.getRawSlot(), child);
        }

        child.orphan();
    }

    /**
    * This method sets some content in a specific slot of the {@link Presenter}.
    * A {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} will be fired
    * after the top-most visible presenter is revealed, resulting in a call to
    * {@link PresenterWidget#onReset()}.
    * <p>
    * For more details on slots, see {@link HasSlots}.
    *
    * @param slot An opaque object identifying which slot this content is being
    * set into. The attached view should know what to do with this slot.
    * @param content The content, a {@link PresenterWidget}. Passing {@code null}
    * will clear the slot.
    * @deprecated since 1.5. Use {@link #setInSlot(IsSlot, PresenterWidget)} instead.
    */
    @Override
    @Deprecated
    public void setInSlot(Object slot, PresenterWidget<?> content) {
        setInSlot(slot, content, true);
    }

    /**
    * This method sets some content in a specific slot of the {@link Presenter}.
    * The attached {@link View} should manage this slot when its
    * {@link View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} is called. It should also clear the
    * slot when the {@code setInSlot} method is called with {@code null} as a
    * parameter.
    * <p>
    * For more details on slots, see {@link HasSlots}.
    *
    * @param slot An opaque object identifying which slot this content is being
    * set into.
    * @param content The content, a {@link PresenterWidget}. Passing {@code null}
    * will clear the slot.
    * @param performReset Pass {@code true} if you want a
    * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} to be fired
    * after the content has been added and this presenter is visible, pass
    * {@code false} otherwise.
    * @deprecated since 1.5. Use {@link #setInSlot(IsSlot, PresenterWidget, boolean)} instead.
    */
    @Override
    @Deprecated
    public void setInSlot(Object slot, PresenterWidget<?> content, boolean performReset) {
        setInSlot(LegacySlotConvertor.convert(slot), content, performReset);
    }

    /**
     * This method sets some content in a specific slot of the {@link Presenter}.
     * The attached {@link View} should manage this slot when its
     * {@link View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} is called. It should also clear the
     * slot when the {@code setInSlot} method is called with {@code null} as a
     * parameter.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot  The slot for which the content is being set.
     * @param child The content, a {@link PresenterWidget}. Passing {@code null}
     * will clear the slot.
     */
    @Override
    public <T extends PresenterWidget<?>> void setInSlot(IsSlot<T> slot, T child) {
        setInSlot(slot, child, true);
    }

    /**
     * This method sets some content in a specific slot of the {@link Presenter}.
     * The attached {@link View} should manage this slot when its
     * {@link View#setInSlot(Object, com.google.gwt.user.client.ui.Widget)} is called. It should also clear the
     * slot when the {@code setInSlot} method is called with {@code null} as a
     * parameter.
     * <p>
     * For more details on slots, see {@link HasSlots}.
     *
     * @param slot  The slot for which the content is being set.
     * @param child The content, a {@link PresenterWidget}. Passing {@code null}
     * will clear the slot.
     * @param performReset Pass {@code true} if you want a
     * {@link com.gwtplatform.mvp.client.proxy.ResetPresentersEvent} to be fired
     * after the content has been added and this presenter is visible, pass
     * {@code false} otherwise.
     */
    @Override
    public <T extends PresenterWidget<?>> void setInSlot(IsSlot<T> slot, T child, boolean performReset) {
        if (child == null) {
            clearSlot((RemovableSlot<?>) slot);
            return;
        }

        adoptChild(slot, child);

        internalClearSlot(slot, child);

        if (!child.isPopup()) {
            getView().setInSlot(slot.getRawSlot(), child);
        }

        if (isVisible()) {
            child.internalReveal();
            if (performReset) {
                ResetPresentersEvent.fire(this);
            }
        }
    }

    @Override
    public <T extends PresenterWidget<?>> T getChild(IsSingleSlot<T> slot) {
        Iterator<T> it = getSlotChildren(slot).iterator();
        return it.hasNext() ? it.next() : null;
    }

    @Override
    public <T extends PresenterWidget<?>> Set<T> getChildren(Slot<T> slot) {
        return getSlotChildren(slot);
    }

    @Override
    public <T extends PresenterWidget<?> & Comparable<T>> List<T> getChildren(OrderedSlot<T> slot) {
        List<T> result = new ArrayList<T>(getSlotChildren(slot));
        Collections.sort(result);
        return result;
    }

    /**
     * Registers an event handler towards the {@link EventBus}.
     * Use this only in the rare situations where you want to manually
     * control when the handler is unregistered, otherwise call
     * {@link #addRegisteredHandler(com.google.gwt.event.shared.GwtEvent.Type, EventHandler)}.
     *
     * @deprecated since 1.5.
     *
     * @param <H>     The handler type.
     * @param type    See {@link com.google.gwt.event.shared.GwtEvent.Type}.
     * @param handler The handler to register.
     * @return The {@link HandlerRegistration} you should use to unregister the handler.
     */
    @Deprecated
    protected <H extends EventHandler> HandlerRegistration addHandler(Type<H> type, H handler) {
        return getEventBus().addHandler(type, handler);
    }

    /**
     * Registers an event handler towards the {@link EventBus} and
     * registers it to be automatically removed when {@link #unbind()}
     * is called. This is usually the desired behavior, but if you
     * want to unregister handlers manually use {@link #addHandler}
     * instead.
     *
     * @param <H>     The handler type.
     * @param type    See {@link com.google.gwt.event.shared.GwtEvent.Type}.
     * @param handler The handler to register.
     * @see #addHandler(com.google.gwt.event.shared.GwtEvent.Type, EventHandler)
     */
    protected <H extends EventHandler> void addRegisteredHandler(Type<H> type, H handler) {
        registerHandler(getEventBus().addHandler(type, handler));
    }

    /**
     * Registers an event handler towards the {@link EventBus} and
     * registers it to be only active when the presenter is visible
     * is called.
     *
     * @param <H>     The handler type.
     * @param type    See {@link com.google.gwt.event.shared.GwtEvent.Type}.
     * @param handler The handler to register.
     * @see #addRegisteredHandler(com.google.gwt.event.shared.GwtEvent.Type, com.google.gwt.event.shared.EventHandler)
     * @see #addHandler(com.google.gwt.event.shared.GwtEvent.Type, EventHandler)
     */
    protected <H extends EventHandler> void addVisibleHandler(Type<H> type, H handler) {
        HandlerInformation<H> handlerInformation = new HandlerInformation<H>(type, handler);
        visibleHandlers.add(handlerInformation);

        if (visible) {
            registerVisibleHandler(handlerInformation);
        }
    }

   /**
     * Registers a handler so that it is automatically removed when
     * {@link #onHide()} is called. This provides an easy way to track event
     * handler registrations.
     *
     * @param handlerRegistration The registration of handler to track.
     */
    protected void registerVisibleHandler(HandlerRegistration handlerRegistration) {
        visibleHandlerRegistrations.add(handlerRegistration);
    }

    /**
     * Access the {@link EventBus} object associated with that presenter.
     * You should not usually use this method to interact with the event bus.
     * Instead call {@link #fireEvent}, {@link #addRegisteredHandler} or
     * {@link #addHandler}.
     *
     * @return The EventBus associated with that presenter.
     */
    protected final EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Lifecycle method called whenever this presenter is about to be
     * hidden.
     * <p>
     * <b>Important:</b> Make sure you call your superclass {@link #onHide()} if
     * you override. Also, do not call directly, see {@link PresenterWidget}
     * for more details on lifecycle methods.
     * <p>
     * You should override this method to dispose of any object
     * created directly or indirectly during the call to {@link #onReveal()}.
     * <p>
     * This method will not be invoked a multiple times without {@link #onReveal()}
     * being called.
     * <p>
     * In a presenter hierarchy, this method is called bottom-up: first on the
     * child presenters, then on the parent.
     */
    protected void onHide() {
    }

    /**
     * Lifecycle method called on all visible presenters whenever a
     * presenter is revealed anywhere in the presenter hierarchy.
     * <p>
     * <b>Important:</b> Make sure you call your superclass {@link #onReset()} if
     * you override. Also, do not call directly, fire a {@link ResetPresentersEvent}
     * to perform a reset manually. See {@link PresenterWidget} for more details on
     * lifecycle methods.
     * <p>
     * This is one of the most frequently used lifecycle method. This is usually a good
     * place to refresh any information displayed by your presenter.
     * <p>
     * Note that {@link #onReset()} is not called only when using
     * {@link #addToSlot(Object, PresenterWidget)}, {@link #addToPopupSlot(PresenterWidget)}
     * or #setInSlot(Object, PresenterWidget, boolean)} with {@code false} as the third
     * parameter.
     * <p>
     * In a presenter hierarchy, this method is called top-down: first on the
     * parent presenters, then on the children.
     */
    protected void onReset() {
    }

    /**
     * Lifecycle method called whenever this presenter is about to be
     * revealed.
     * <p>
     * <b>Important:</b> Make sure you call your superclass {@link #onReveal()} if
     * you override. Also, do not call directly, see {@link PresenterWidget}
     * for more details on lifecycle methods.
     * <p>
     * You should override this method to perform any action or initialisation
     * that needs to be done when the presenter is revealed. Any initialisation
     * you perform here should be taken down in {@link #onHide()}.
     * <p>
     * Information that needs to be updated whenever the user navigates should
     * be refreshed in {@link #onReset()}.
     * <p>
     * In a presenter hierarchy, this method is called top-down: first on the
     * parent presenters, then on the children.
     */
    protected void onReveal() {
    }

    /**
     * Internal method called to hide a presenter.
     * See {@link PresenterWidget} for ways to hide a presenter.
     */
    void internalHide() {
        if (!isVisible()) {
            return;
        }
        for (PresenterWidget<?> child : children) {
            child.internalHide();
        }

        if (isPopup()) {
            ((PopupView) this.getView()).setCloseHandler(null);
            ((PopupView) this.getView()).hide();
        }

        unregisterVisibleHandlers();

        visible = false;

        onHide();
    }

    /**
     * Internal method called to reset a presenter. Instead of using that method,
     * fire a {@link ResetPresentersEvent} to perform a reset manually.
     */
    void internalReset() {
        if (!isVisible()) {
            return;
        }
        onReset();
        // use new set to prevent concurrent modification
        for (PresenterWidget<?> child: new HashSet<PresenterWidget<?>>(children)) {
            child.internalReset();
        }
        if (isPopup()) {
            ((PopupView) getView()).show();
        }
    }

    /**
     * Internal method called to reveal a presenter.
     * See {@link PresenterWidget} and {@link Presenter} for ways to reveal a
     * presenter.
     */
    @SuppressWarnings("unchecked")
    void internalReveal() {
        if (isVisible()) {
            return;
        }
        onReveal();
        visible = true;

        // use new set to prevent concurrent modification
        for (PresenterWidget<?> child: new HashSet<PresenterWidget<?>>(children)) {
            child.internalReveal();
        }

        if (isPopup()) {
            monitorCloseEvent((PresenterWidget<? extends PopupView>) this);
            ((PopupView) getView()).showAndReposition();
        }

        registerVisibleHandlers();
    }

    /**
     * Make a child a child of this presenter.
     * @param slot
     * @param child
     */
    private <T extends PresenterWidget<?>> void adoptChild(IsSlot<T> slot, PresenterWidget<?> child) {
        if (child.parent != this) {
            if (child.parent != null) {
                if (!child.slot.isRemovable()) {
                    throw new IllegalArgumentException("Cannot move a child of a permanent slot to another slot");
                }
                child.parent.children.remove(child);
            }
            child.parent = this;
            children.add(child);
        }
        child.slot = slot;
    }

    boolean isPopup() {
        return slot != null && slot.isPopup();
    }

    /**
     * Monitors the specified popup presenter so that we know when it
     * is closing. This allows us to make sure it doesn't receive
     * future messages.
     *
     * @param popupPresenter The {@link PresenterWidget} to monitor.
     */
    private void monitorCloseEvent(final PresenterWidget<? extends PopupView> popupPresenter) {
        PopupView popupView = popupPresenter.getView();

        popupView.setCloseHandler(new PopupViewCloseHandler() {
            @Override
            public void onClose() {
                popupPresenter.removeFromParentSlot();
            }
        });
    }

    /**
     * Disconnects a child from its parent.
     */
    private void orphan() {
        if (slot == null) {
            return;
        }
        if (!slot.isRemovable()) {
            throw new IllegalArgumentException("Cannot remove a child from a permanent slot");
        }
        if (parent != null) {
            internalHide();

            parent.children.remove(this);
            parent = null;
        }
        slot = null;
    }

    private <H extends EventHandler> void registerVisibleHandler(HandlerInformation<H> handlerInformation) {
        HandlerRegistration handlerRegistration = addHandler(handlerInformation.type, handlerInformation.eventHandler);
        visibleHandlerRegistrations.add(handlerRegistration);
    }

    private void registerVisibleHandlers() {
        for (HandlerInformation<? extends EventHandler> handlerInformation : visibleHandlers) {
            registerVisibleHandler(handlerInformation);
        }
    }

    private void unregisterVisibleHandlers() {
        for (HandlerRegistration handlerRegistration : visibleHandlerRegistrations) {
            handlerRegistration.removeHandler();
        }

        visibleHandlerRegistrations.clear();
    }

    @SuppressWarnings("unchecked")
    private <T extends PresenterWidget<?>> Set<T> getSlotChildren(IsSlot<T> slot) {
        Set<T> result = new HashSet<T>();
        for (PresenterWidget<?> child : children) {
            if (child.slot == slot) {
                result.add((T) child);
            }
        }
        return result;
    }
}
