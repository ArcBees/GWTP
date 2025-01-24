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

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

/**
 * Event fired after navigation has occurred. It will not be fired if navigation is
 * refused through {@link PlaceManager#setOnLeaveConfirmation}, instead
 * {@link NavigationRefusedEvent} will.
 * <p>
 * Instead of registering your presenter towards this event, consider overriding
 * {@link com.gwtplatform.mvp.client.PresenterWidget#onReset()}. From there
 * you can call {@link PlaceManager#getCurrentPlaceRequest()} to get the
 * {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest}.
 *
 * @see NavigationRefusedEvent
 */
public final class NavigationEvent extends GwtEvent<NavigationHandler> {
    private static final Type<NavigationHandler> TYPE = new Type<>();

    private final PlaceRequest request;

    /**
     * Create a navigation event and attach it to a place request. You can pass
     * {@code null} as a request if it's unknown, for example when revealing a
     * default place or an error place.
     *
     * @param request The {@link PlaceRequest}.
     */
    public NavigationEvent(PlaceRequest request) {
        this.request = request;
    }

    /**
     * Fires a {@link NavigationEvent}
     * into a source that has access to an {@link com.google.web.bindery.event.shared.EventBus}.
     *
     * @param source  The source that fires this event ({@link HasHandlers}).
     * @param request The {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest} that was navigated to.
     */
    public static void fire(final HasHandlers source, PlaceRequest request) {
        source.fireEvent(new NavigationEvent(request));
    }

    public static Type<NavigationHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<NavigationHandler> getAssociatedType() {
        return getType();
    }

    /**
     * Access the {@link PlaceRequest} that triggered that navigation event.
     *
     * @return The {@link PlaceRequest} or {@code null} if no place request is
     *         known.
     */
    public PlaceRequest getRequest() {
        return request;
    }

    @Override
    protected void dispatch(NavigationHandler handler) {
        handler.onNavigation(this);
    }
}
