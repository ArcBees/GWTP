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
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.presenter.AbstractSlot;
import com.gwtplatform.mvp.client.presenter.PresenterWidget;
import com.gwtplatform.mvp.client.presenter.Slot;
import com.gwtplatform.mvp.client.proxy.ResetPresentersEvent;

/**
 * A presenter that does not have to be a singleton. Pages from your
 * application will usually be singletons and extend the {@link Presenter} class.
 * <p/>
 * Choosing between a {@link Presenter} and {@link GenericPresenterWidget} is a design decision that
 * requires some thought. For example, a {@link GenericPresenterWidget} is useful when
 * you need a custom widget with extensive logic. For example, a chat box that can be instantiated
 * multiple times and needs to communicate with the server would be a good candidate for
 * a {@link GenericPresenterWidget}. The drawback of a {@link GenericPresenterWidget} is that it is managed by its
 * parent presenter, which increases coupling. Therefore, you should use a {@link Presenter} when
 * the parent is not expected to know its child. Moreover, only {@link Presenter} can be attached
 * to name tokens in order to support browser history.
 * <p/>
 * {@link GenericPresenterWidget}s and {@link Presenter}s are organized in a hierarchy.
 * Internally, parent presenters have links to their currently attached children presenters. A
 * parent {@link Presenter} can contain either {@link Presenter}s or {@link GenericPresenterWidget}s,
 * but a {@link GenericPresenterWidget} can only contain {@link GenericPresenterWidget}s.
 * <p/>
 * To reveal a {@link GenericPresenterWidget} you should insert it within a {@link HasSlots slot} of its
 * containing presenter using one of the following methods:
 * <ul>
 * <li>{@link #setInSlot(Object, GenericPresenterWidget)}
 * <li>{@link #setInSlot(Object, GenericPresenterWidget, boolean)}
 * <li>{@link #addToSlot(Object, GenericPresenterWidget)}
 * <li>{@link #addToPopupSlot(GenericPresenterWidget)}
 * <li>{@link #addToPopupSlot(GenericPresenterWidget, boolean)}
 * </ul>
 * Revealing a {@link Presenter} is done differently, refer to the class documentation for more details.
 * <p/>
 * To hide a {@link GenericPresenterWidget} or a {@link Presenter} you can use {@link #setInSlot} to place
 * another presenter in the same slot, or you can call one of the following methods:
 * <ul>
 * <li>{@link #removeFromSlot(Object, GenericPresenterWidget)}
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
 * Revealing or hiding a {@link GenericPresenterWidget} triggers an internal chain of events that result in
 * these lifecycle methods being called. For an example, here is what happens following
 * a call to {@link #setInSlot(Object, GenericPresenterWidget)}:
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
 * chain stops. Otherwise, {@link #onReveal()} is called on the {@link GenericPresenterWidget} that
 * was just added.</li>
 * <li>{@link #onReveal()} is called recursively on that presenter's children
 * (top-down: first the parent, then the children).</li>
 * <li>If {@link #setInSlot(Object, GenericPresenterWidget, boolean)} was called with {@code false}
 * as the third parameter then the process stops. Otherwise, {@link #onReset()} is
 * called on all the currently visible presenters (top-down: first the parent, then
 * the children).</li>
 * </ul>
 *
 * @param <V> The {@link View} type.
 */
public abstract class GenericPresenterWidget<S, V extends View> extends HandlerContainerImpl implements HasHandlers,
        HasSlots<S>, HasPopupSlot<S>, IsWidget {
    private static class HandlerInformation<H extends EventHandler> {
        private final Type<H> type;
        private final H eventHandler;

        private HandlerInformation(Type<H> type, H eventHandler) {
            this.type = type;
            this.eventHandler = eventHandler;
        }
    }

    private static final Slot POPUP_SLOT = new Slot();
    private final EventBus eventBus;
    private final V view;
    private final List<HandlerInformation<? extends EventHandler>> visibleHandlers =
            new ArrayList<HandlerInformation<? extends EventHandler>>();
    private final List<HandlerRegistration> visibleHandlerRegistrations = new ArrayList<HandlerRegistration>();
    private final Set<GenericPresenterWidget<S, ?>> children = new HashSet<GenericPresenterWidget<S, ?>>();

    boolean visible;
    private GenericPresenterWidget<S, ?> parent;
    private Object slot;

    /**
     * Creates a {@link GenericPresenterWidget} that is not necessarily using automatic
     * binding. Automatic binding will only work when instantiating this object using
     * Guice/GIN dependency injection. See
     * {@link HandlerContainerImpl#HandlerContainerImpl(boolean)} for
     * more details on automatic binding.
     *
     * @param autoBind {@code true} to request automatic binding, {@code false} otherwise.
     * @param eventBus The {@link EventBus}.
     * @param view     The {@link View}.
     */
    public GenericPresenterWidget(boolean autoBind, EventBus eventBus, V view) {
        super(autoBind);
        if (view == null) {
            throw new IllegalArgumentException("view cannot be null");
        }
        this.eventBus = eventBus;
        this.view = view;
    }

    /**
     * Creates a {@link GenericPresenterWidget} that uses automatic binding. This will
     * only work when instantiating this object using Guice/GIN dependency injection.
     * See {@link HandlerContainerImpl#HandlerContainerImpl()} for more details on
     * automatic binding.
     *
     * @param eventBus The {@link EventBus}.
     * @param view     The {@link View}.
     */
    public GenericPresenterWidget(EventBus eventBus, V view) {
        this(true, eventBus, view);
    }

    @Override
    public void addToPopupSlot(GenericPresenterWidget<S, ? extends PopupView> child) {
        addToSlot((S) POPUP_SLOT, child);
    }

    @Override
    public void addToPopupSlot(GenericPresenterWidget<S, ? extends PopupView> child, boolean center) {
        addToPopupSlot(child);
    }

    @Override
    public void addToSlot(S slot, GenericPresenterWidget<S, ?> child) {
        if (child == null) {
            throw new IllegalArgumentException("Can't add null to a slot");
        }

        if (child.slot == slot && child.parent == this) {
            return;
        }

        adoptChild(slot, child);

        if (!child.isPopup()) {
            getView().addToSlot(slot, child);
        } else {
            monitorCloseEvent((GenericPresenterWidget<S, ? extends PopupView>) child);
        }
        if (isVisible()) {
            child.internalReveal();
        }
    }

    @Override
    public Widget asWidget() {
        return getView().asWidget();
    }

    @Override
    public void clearSlot(S slot) {
        internalClearSlot(slot);
    }

    private void internalClearSlot(Object slot) {
        Iterator<GenericPresenterWidget<S,?>> it = children.iterator();
        while (it.hasNext()) {
            GenericPresenterWidget<S, ?> child = it.next();
            if (child.slot == slot) {
                it.remove();
                child.orphan();
            }
        }
        getView().setInSlot(slot, null);
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

    /**
     * Makes it possible to access the {@link Widget} object associated with that
     * presenter.
     *
     * @return The Widget associated with that presenter.
     */
    @Deprecated
    public Widget getWidget() {
        return asWidget();
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

        parent.internalRemoveFromSlot(slot, this);
    }

    @Override
    public void removeFromPopupSlot(GenericPresenterWidget<S,? extends PopupView> child) {
        removeFromSlot((S) POPUP_SLOT, child);
    }

    @Override
    public void removeFromSlot(S slot, GenericPresenterWidget<S,?> child) {
        internalRemoveFromSlot(slot, child);
    }

    private void internalRemoveFromSlot(Object slot, GenericPresenterWidget<S,?> child) {
        if (child == null || child.slot != slot) {
            return;
        }

        if (!child.isPopup()) {
            getView().removeFromSlot(slot, child);
        }

        child.orphan();
    }

    @Override
    public void setInSlot(S slot, GenericPresenterWidget<S, ?> child) {
        setInSlot(slot, child, true);
    }

    @Override
    public void setInSlot(S slot, GenericPresenterWidget<S,?> child, boolean performReset) {
       internalSetInSlot(slot, child, performReset);
    }

    protected final void internalSetInSlot(Object slot, GenericPresenterWidget<S,?> child, boolean performReset) {
        if (child == null) {
            internalClearSlot(slot);
            return;
        }

        adoptChild(slot, child);

        Iterator<GenericPresenterWidget<S,?>> it = children.iterator();
        while (it.hasNext()) {
            GenericPresenterWidget<S,?> nextChild = it.next();
            if (nextChild != child && nextChild.slot == slot) {
                it.remove();
                nextChild.orphan();
            }
        }

        getView().setInSlot(slot, child);
        if (isVisible()) {
            child.internalReveal();
            if (performReset) {
                ResetPresentersEvent.fire(this);
            }
        }
    }

    /**
     * Registers an event handler towards the {@link EventBus}.
     * Use this only in the rare situations where you want to manually
     * control when the handler is unregistered, otherwise call
     * {@link #addRegisteredHandler(com.google.gwt.event.shared.GwtEvent.Type, EventHandler)}.
     *
     * @param <H>     The handler type.
     * @param type    See {@link com.google.gwt.event.shared.GwtEvent.Type}.
     * @param handler The handler to register.
     * @return The {@link HandlerRegistration} you should use to unregister the handler.
     */
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
        registerHandler(addHandler(type, handler));
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
     * <p/>
     * <b>Important:</b> Make sure you call your superclass {@link #onHide()} if
     * you override. Also, do not call directly, see {@link GenericPresenterWidget}
     * for more details on lifecycle methods.
     * <p/>
     * You should override this method to dispose of any object
     * created directly or indirectly during the call to {@link #onReveal()}.
     * <p/>
     * This method will not be invoked a multiple times without {@link #onReveal()}
     * being called.
     * <p/>
     * In a presenter hierarchy, this method is called bottom-up: first on the
     * child presenters, then on the parent.
     */
    protected void onHide() {
    }

    /**
     * Lifecycle method called on all visible presenters whenever a
     * presenter is revealed anywhere in the presenter hierarchy.
     * <p/>
     * <b>Important:</b> Make sure you call your superclass {@link #onReset()} if
     * you override. Also, do not call directly, fire a {@link ResetPresentersEvent}
     * to perform a reset manually. See {@link GenericPresenterWidget} for more details on
     * lifecycle methods.
     * <p/>
     * This is one of the most frequently used lifecycle method. This is usually a good
     * place to refresh any information displayed by your presenter.
     * <p/>
     * Note that {@link #onReset()} is not called only when using
     * {@link #addToSlot(Object, GenericPresenterWidget)}, {@link #addToPopupSlot(GenericPresenterWidget)}
     * or #setInSlot(Object, PresenterWidget, boolean)} with {@code false} as the third
     * parameter.
     * <p/>
     * In a presenter hierarchy, this method is called top-down: first on the
     * parent presenters, then on the children.
     */
    protected void onReset() {
    }

    /**
     * Lifecycle method called whenever this presenter is about to be
     * revealed.
     * <p/>
     * <b>Important:</b> Make sure you call your superclass {@link #onReveal()} if
     * you override. Also, do not call directly, see {@link GenericPresenterWidget}
     * for more details on lifecycle methods.
     * <p/>
     * You should override this method to perform any action or initialisation
     * that needs to be done when the presenter is revealed. Any initialisation
     * you perform here should be taken down in {@link #onHide()}.
     * <p/>
     * Information that needs to be updated whenever the user navigates should
     * be refreshed in {@link #onReset()}.
     * <p/>
     * In a presenter hierarchy, this method is called top-down: first on the
     * parent presenters, then on the children.
     */
    protected void onReveal() {
    }

    /**
     * Internal method called to hide a presenter.
     * See {@link GenericPresenterWidget} for ways to hide a presenter.
     */
    void internalHide() {
        if (!isVisible()) {
            return;
        }
        for (GenericPresenterWidget<S,?> child : children) {
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
        for (GenericPresenterWidget<S,?> child : children) {
            child.internalReset();
        }
        if (isPopup()) {
            ((PopupView) getView()).show();
        }
    }

    /**
     * Internal method called to reveal a presenter.
     * See {@link GenericPresenterWidget} and {@link Presenter} for ways to reveal a
     * presenter.
     */
    void internalReveal() {
        if (isVisible()) {
            return;
        }
        onReveal();
        visible = true;

        for (GenericPresenterWidget<S,?> child : children) {
            child.internalReveal();
        }

        if (isPopup()) {
            ((PopupView) getView()).showAndReposition();
        }

        registerVisibleHandlers();
    }

    /**
     * Make a child a child of this presenter.
     * @param slot
     * @param child
     */
    private void adoptChild(Object slot, GenericPresenterWidget<S,?> child) {
        if (child.parent != this) {
            if (child.parent != null) {
                child.parent.children.remove(child);
            }
            child.parent = this;
            children.add(child);
        }
        child.slot = slot;
    }

    private boolean isPopup() {
        return slot == POPUP_SLOT;
    }

    /**
     * Monitors the specified popup presenter so that we know when it
     * is closing. This allows us to make sure it doesn't receive
     * future messages.
     *
     * @param popupPresenter The {@link PresenterWidget} to monitor.
     */
    private void monitorCloseEvent(final GenericPresenterWidget<S,? extends PopupView> popupPresenter) {
        PopupView popupView = popupPresenter.getView();

        popupView.setCloseHandler(new PopupViewCloseHandler() {
            @Override
            public void onClose() {
                removeFromPopupSlot(popupPresenter);
            }
        });
    }

    /**
     * Disconnects a child from its parent.
     */
    private void orphan() {
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

    /**
     * Don't call this method.
     * This is a helper method used internally by GWTP.
     */
    protected final <T extends com.gwtplatform.mvp.client.presenter.PresenterWidget<?>> Set<T>
        unsafeGetChildren(AbstractSlot<T> slot) {
        Set<T> result = new HashSet<T>();
        for (GenericPresenterWidget<S, ?> child: children) {
            if (child.slot == slot) {
                result.add((T) child);
            }
        }
        return result;
    }
}
