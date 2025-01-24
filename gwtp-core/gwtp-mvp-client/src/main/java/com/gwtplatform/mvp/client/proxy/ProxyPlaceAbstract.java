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

package com.gwtplatform.mvp.client.proxy;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Command;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.HandlerContainer;
import com.gwtplatform.mvp.client.HandlerContainerImpl;
import com.gwtplatform.mvp.client.HasHandlerContainer;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import jakarta.inject.Inject;

/**
 * A useful mixing class to define a {@link Proxy} that is also a {@link Place}.
 * You can usually inherit from the simpler form {@link ProxyPlace}.
 * <p>
 *
 * @param <P>      The Presenter's type.
 * @param <Proxy_> Type of the associated {@link Proxy}.
 */
public class ProxyPlaceAbstract<P extends Presenter<?, ?>, Proxy_ extends Proxy<P>> implements ProxyPlace<P>,
        HasHandlerContainer {
    /**
     * Hides {@link com.gwtplatform.mvp.client.HandlerContainer#bind()} and
     * {@link com.gwtplatform.mvp.client.HandlerContainer#unbind()} from implementers.
     */
    private class ProxyHandlerContainer extends HandlerContainerImpl {
        private boolean wasBound;

        @Override
        protected void registerHandler(HandlerRegistration handlerRegistration) {
            super.registerHandler(handlerRegistration);

            // Visibility is limited to this package, so registerHandler can only be call by ProxyPlaceAbstract#bind()
            wasBound = true;
        }

        @Override
        public void bind() {
            // Was never bound, so rebinding it does not make sense.
            if (wasBound) {
                super.bind();

                ProxyPlaceAbstract.this.bind(placeManager, eventBus);
            }
        }
    }

    private final ProxyHandlerContainer handlerContainer = new ProxyHandlerContainer();

    private Place place;
    private PlaceManager placeManager;
    private Proxy_ proxy;

    private EventBus eventBus;

    /**
     * Creates a {@link ProxyPlaceAbstract}. That is, the {@link Proxy} of a
     * {@link Presenter} attached to a {@link Place}. This presenter can be
     * invoked by setting a history token that matches its name token in the URL
     * bar.
     */
    public ProxyPlaceAbstract() {
    }

    @Override
    public boolean canReveal() {
        return place.canReveal();
    }

    // /////////////////////
    // Inherited from Proxy

    @Override
    public final boolean equals(Object o) {
        return place.equals(o);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        getEventBus().fireEventFromSource(event, this);
    }

    @Override
    public final EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public String getNameToken() {
        return place.getNameToken();
    }

    // /////////////////////
    // Inherited from Place

    @Override
    public void getPresenter(NotifyingAsyncCallback<P> callback) {
        proxy.getPresenter(callback);
    }

    @Override
    public void getRawPresenter(NotifyingAsyncCallback<Presenter<?, ?>> callback) {
        proxy.getRawPresenter(callback);
    }

    @Override
    public final int hashCode() {
        return place.hashCode();
    }

    @Override
    public boolean matchesRequest(PlaceRequest request) {
        return place.matchesRequest(request);
    }

    @Override
    public HandlerContainer getHandlerContainer() {
        return handlerContainer;
    }

    // /////////////////////
    // Protected methods that can be overridden

    @Override
    public final String toString() {
        return place.toString();
    }

    // /////////////////////
    // Private methods

    protected Place getPlace() {
        return place;
    }

    protected void setPlace(Place place) {
        this.place = place;
    }

    protected PlaceManager getPlaceManager() {
        return placeManager;
    }

    protected void setPlaceManager(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    protected Proxy_ getProxy() {
        return proxy;
    }

    protected void setProxy(Proxy_ proxy) {
        this.proxy = proxy;
    }

    /**
     * Injects the various resources and performs other bindings.
     * <p>
     * Never call directly, it should only be called by GIN. Method injection is
     * used instead of constructor injection, because the latter doesn't work well
     * with GWT generators.
     *
     * @param placeManager The {@link PlaceManager}.
     * @param eventBus     The {@link EventBus}.
     */
    @Inject
    protected void bind(final PlaceManager placeManager, EventBus eventBus) {
        this.placeManager = placeManager;
        this.eventBus = eventBus;

        addRegisteredHandler(PlaceRequestInternalEvent.getType(), new PlaceRequestInternalHandler() {
            @Override
            public void onPlaceRequest(PlaceRequestInternalEvent event) {
                if (event.isHandled()) {
                    return;
                }
                PlaceRequest request = event.getRequest();
                if (matchesRequest(request)) {
                    event.setHandled();
                    if (canReveal()) {
                        handleRequest(request, event.shouldUpdateBrowserHistory());
                    } else {
                        event.setUnauthorized();
                    }
                }
            }
        });
        addRegisteredHandler(GetPlaceTitleEvent.getType(), new GetPlaceTitleHandler() {
            @Override
            public void onGetPlaceTitle(GetPlaceTitleEvent event) {
                if (event.isHandled()) {
                    return;
                }
                PlaceRequest request = event.getRequest();
                if (matchesRequest(request)) {
                    if (canReveal()) {
                        event.setHandled();
                        getPlaceTitle(event);
                    }
                }
            }
        });
    }

    /**
     * Obtains the title for this place and invoke the passed handler when the
     * title is available. By default, places don't have a title and will invoke
     * the handler with {@code null}, but override this method to provide your own
     * title.
     *
     * @param event The {@link GetPlaceTitleEvent} to invoke once the title is
     *              available.
     */
    protected void getPlaceTitle(GetPlaceTitleEvent event) {
        event.getHandler().onSetPlaceTitle(null);
    }

    /**
     * Register an event to be automatically removed when this proxy will be released.
     * You will have to register your event again if you rebind this proxy.
     *
     * @param type    The event type
     * @param handler The handler to register.
     * @param <H>     The handler type
     */
    protected <H> void addRegisteredHandler(GwtEvent.Type<H> type, H handler) {
        HandlerRegistration registration = eventBus.addHandler(type, handler);
        handlerContainer.registerHandler(registration);
    }

    /**
     * Prepares the presenter with the information contained in the current
     * request, then reveals it. Will refuse to reveal the display and do nothing
     * if {@link #canReveal()} returns <code>false</code>.
     *
     * @param request          The request to handle. Can pass <code>null</code> if no
     *                         request is used, in which case the presenter will be directly
     *                         revealed.
     * @param updateBrowserUrl {@code true} If the browser URL should be updated, {@code false}
     *                         otherwise.
     */
    private void handleRequest(final PlaceRequest request, final boolean updateBrowserUrl) {
        proxy.getPresenter(new NotifyingAsyncCallback<P>(eventBus) {

            @Override
            public void success(final P presenter) {
                // Everything should be bound before we prepare the presenter from the
                // request,
                // in case it wants to fire some events. That's why we will do this in a
                // deferred command.
                addDeferredCommand(new Command() {
                    @Override
                    public void execute() {
                        PlaceRequest originalRequest = placeManager.getCurrentPlaceRequest();
                        presenter.prepareFromRequest(request);
                        if (originalRequest == placeManager.getCurrentPlaceRequest()) {
                            // User did not manually update place request in prepareFromRequest, update it here.
                            placeManager.updateHistory(request, updateBrowserUrl);
                        }
                        NavigationEvent.fire(placeManager, request);
                        if (!presenter.useManualReveal()) {
                            // Automatic reveal
                            manualReveal(presenter);
                        }
                    }
                });
            }

            @Override
            protected void failure(final Throwable caught) {
                // Unlock place manager to prevent UI "freeze" caused by LockInteractionEvent
                placeManager.unlock();
            }
        });
    }

    @Override
    public void manualReveal(Presenter<?, ?> presenter) {
        // Reveal only if there are no pending navigation requests
        if (!placeManager.hasPendingNavigation()) {
            if (!presenter.isVisible()) {
                // This will trigger a reset in due time
                presenter.forceReveal();
            } else {
                // We have to do the reset ourselves
                ResetPresentersEvent.fire(this);
            }
        }
        placeManager.unlock();
    }

    @Override
    public void manualRevealFailed() {
        placeManager.unlock();
    }

    /**
     * This method allows unit test to handle deferred command with a mechanism that doesn't
     * require a GWTTestCase.
     *
     * @param command The {@link Command} to defer, see {@link com.google.gwt.core.client.Scheduler.ScheduledCommand}.
     */
    void addDeferredCommand(Command command) {
        Scheduler.get().scheduleDeferred(command);
    }
}
