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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.proxy.ResetPresentersEvent;

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
public abstract class PresenterWidget<V extends View> extends
HandlerContainerImpl implements HasHandlers, HasSlots, HasPopupSlot, IsWidget {
    private static class HandlerInformation<H extends EventHandler> {
        private final Type<H> type;
        private final H eventHandler;

        private HandlerInformation(Type<H> type, H eventHandler) {
            this.type = type;
            this.eventHandler = eventHandler;
        }
    }

    private static Object POPUP_SLOT = new Object();

    boolean visible;

    private EventBus eventBus;
    private V view;
    private boolean isPopup;

    /**
     * This map makes it possible to keep a list of all the active children in
     * every slot managed by this {@link PresenterWidget}. A slot is identified by an
     * opaque object. A single slot can have many children.
     */
    private final Map<Object, List<PresenterWidget<?>>>
    activeChildren = new HashMap<Object, List<PresenterWidget<?>>>();

    /**
     * The parent presenter, in order to make sure this widget is only ever in one
     * parent.
     */
    private PresenterWidget<?> currentParentPresenter;

    private final List<HandlerInformation<? extends EventHandler>> visibleHandlers =
            new ArrayList<HandlerInformation<? extends EventHandler>>();

    private final List<HandlerRegistration> visibleHandlerRegistrations = new ArrayList<HandlerRegistration>();

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

    @Override
    public void addToSlot(Object slot, PresenterWidget<?> content) {
        if (content == null) {
            return;
        }

        content.setIsPopup(slot == POPUP_SLOT);

        content.reparent(this);

        List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);
        if (slotChildren != null) {
            slotChildren.add(content);
        } else {
            slotChildren = new ArrayList<PresenterWidget<?>>(1);
            slotChildren.add(content);
            activeChildren.put(slot, slotChildren);
        }
        if (!content.isPopup()) {
            getView().addToSlot(slot, content);
        } else {
            monitorCloseEvent((PresenterWidget<? extends PopupView>) content);
        }
        if (isVisible()) {
            // This presenter is visible, its time to call onReveal
            // on the newly added child (and recursively on this child children)
            if (!content.isVisible()) {
                content.internalReveal();
            }
        }
    }

    @Override
    public Widget asWidget() {
        return getView() == null ? null : getView().asWidget();
    }

    @Override
    public void clearSlot(Object slot) {
        List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);
        if (slotChildren != null) {
            // This presenter is visible, its time to call onHide
            // on the children to be removed (and recursively on their children)
            if (isVisible()) {
                for (PresenterWidget<?> activeChild : slotChildren) {
                    activeChild.internalHide();
                }
            }
            slotChildren.clear();
        }
        getView().setInSlot(slot, null);
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
     * You must ensure that the presenter has a parent before calling this method.
     */
    public void removeFromParentSlot() {
        assert currentParentPresenter != null : "Tried to remove a presenter that has no parent from its parent";
        for (Entry<Object, List<PresenterWidget<?>>> entry: currentParentPresenter.activeChildren.entrySet()) {
            if (entry.getValue().contains(this)) {
                currentParentPresenter.removeFromSlot(entry.getKey(), this);
                return;
            }
        }
    }

    @Override
    public void removeFromPopupSlot(PresenterWidget<? extends PopupView> child) {
        removeFromSlot(POPUP_SLOT, child);
    }

    @Override
    public void removeFromSlot(Object slot, PresenterWidget<?> content) {
        if (content == null) {
            return;
        }

        content.reparent(null);

        List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);
        if (slotChildren != null) {
            // This presenter is visible, its time to call onHide
            // on the child to be removed (and recursively on itschildren)
            if (isVisible()) {
                content.internalHide();
            }
            slotChildren.remove(content);
        }
        getView().removeFromSlot(slot, content);
    }

    @Override
    public void setInSlot(Object slot, PresenterWidget<?> content) {
        setInSlot(slot, content, true);
    }

    @Override
    public void setInSlot(Object slot, PresenterWidget<?> content, boolean performReset) {
        if (content == null) {
            // Assumes the user wants to clear the slot content.
            clearSlot(slot);
            return;
        }

        content.reparent(this);

        List<PresenterWidget<?>> slotChildren = activeChildren.get(slot);

        if (slotChildren != null) {
            if (slotChildren.size() == 1 && slotChildren.get(0) == content) {
                // The slot contains the right content, nothing to do
                return;
            }

            if (isVisible()) {
                // We are visible, make sure the content that we're removing
                // is being notified as hidden
                for (PresenterWidget<?> activeChild : slotChildren) {
                    activeChild.internalHide();
                }
            }
            slotChildren.clear();
            slotChildren.add(content);
        } else {
            slotChildren = new ArrayList<PresenterWidget<?>>(1);
            slotChildren.add(content);
            activeChildren.put(slot, slotChildren);
        }

        // Set the content in the view
        getView().setInSlot(slot, content);
        if (isVisible()) {
            // This presenter is visible, its time to call onReveal
            // on the newly added child (and recursively on this child children)
            if (!content.isVisible()) {
                content.internalReveal();
            }
            if (performReset) {
                // And to reset everything if needed
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
    protected EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Lifecycle method called whenever this presenter is about to be
     * hidden.
     * <p/>
     * <b>Important:</b> Make sure you call your superclass {@link #onHide()} if
     * you override. Also, do not call directly, see {@link PresenterWidget}
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
     * to perform a reset manually. See {@link PresenterWidget} for more details on
     * lifecycle methods.
     * <p/>
     * This is one of the most frequently used lifecycle method. This is usually a good
     * place to refresh any information displayed by your presenter.
     * <p/>
     * Note that {@link #onReset()} is not called only when using
     * {@link #addToSlot(Object, PresenterWidget)}, {@link #addToPopupSlot(PresenterWidget)}
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
     * you override. Also, do not call directly, see {@link PresenterWidget}
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
     * See {@link PresenterWidget} for ways to hide a presenter.
     */
    void internalHide() {
        assert isVisible() : "internalHide() called on a hidden presenter!";
        for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
            for (PresenterWidget<?> activeChild : slotChildren) {
                if (activeChild.isVisible()) {
                    activeChild.internalHide();
                }
            }
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
        onReset();
        for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
            for (PresenterWidget<?> activeChild : slotChildren) {
                activeChild.internalReset();
            }
        }
        if (isPopup() && isVisible()) {
            ((PopupView) getView()).show();
        }
    }

    /**
     * Internal method called to reveal a presenter.
     * See {@link PresenterWidget} and {@link Presenter} for ways to reveal a
     * presenter.
     */
    void internalReveal() {
        assert !isVisible() : "internalReveal() called on a visible presenter!";
        onReveal();
        visible = true;

        for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
            for (PresenterWidget<?> activeChild : slotChildren) {
                activeChild.internalReveal();
            }
        }

        if (isPopup()) {
            ((PopupView) getView()).showAndReposition();
        }

        registerVisibleHandlers();
    }

    /**
     * Detaches this presenter from its current parent and attaches it
     * to a new parent.
     *
     * @param newParent The new parent {@link PresenterWidget}.
     */
    void reparent(PresenterWidget<?> newParent) {
        if (currentParentPresenter != null && currentParentPresenter != newParent) {
            currentParentPresenter.detach(this);
        }

        currentParentPresenter = newParent;
    }

    /**
     * Called by a child {@link PresenterWidget} when it wants to detach itself
     * from this parent.
     *
     * @param childPresenter The {@link PresenterWidget}. It should be a child of this presenter.
     */
    private void detach(PresenterWidget<?> childPresenter) {
        for (List<PresenterWidget<?>> slotChildren : activeChildren.values()) {
            slotChildren.remove(childPresenter);
        }
    }

    private boolean isPopup() {
        return isPopup;
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
                removeFromPopupSlot(popupPresenter);
            }
        });
    }

    private <H extends EventHandler> void registerVisibleHandler(HandlerInformation<H> handlerInformation) {
        HandlerRegistration handlerRegistration =
                addHandler(handlerInformation.type, handlerInformation.eventHandler);
        visibleHandlerRegistrations.add(handlerRegistration);
    }

    private void registerVisibleHandlers() {
        for (HandlerInformation<? extends EventHandler> handlerInformation : visibleHandlers) {
            registerVisibleHandler(handlerInformation);
        }
    }

    private void setIsPopup(boolean popup) {
        this.isPopup = popup;
    }

    private void unregisterVisibleHandlers() {
        for (HandlerRegistration handlerRegistration : visibleHandlerRegistrations) {
            handlerRegistration.removeHandler();
        }

        visibleHandlerRegistrations.clear();
    }

}
