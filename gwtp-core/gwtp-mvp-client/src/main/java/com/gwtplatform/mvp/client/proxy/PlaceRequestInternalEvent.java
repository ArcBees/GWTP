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
 * This event is fired by the {@link PlaceManager} whenever a new place is
 * requested, either by history navigation or directly.
 * <p>
 * <b>Important!</b> You should never fire that event directly. Instead, build a
 * {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest} and pass it to one of the following methods:
 * <ul>
 * <li>{@link PlaceManager#revealPlace(com.gwtplatform.mvp.shared.proxy.PlaceRequest)}</li>
 * <li>{@link PlaceManager#revealRelativePlace(com.gwtplatform.mvp.shared.proxy.PlaceRequest)}</li>
 * <li>{@link PlaceManager#revealRelativePlace(com.gwtplatform.mvp.shared.proxy.PlaceRequest, int)}</li>
 * </ul>
 */
class PlaceRequestInternalEvent extends GwtEvent<PlaceRequestInternalHandler> {
    private static Type<PlaceRequestInternalHandler> TYPE;

    private final PlaceRequest request;
    private final boolean updateBrowserHistory;

    /**
     * The handled flag can let others know when the event has been handled.
     * Handlers should call {@link setHandled()} as soon as they figure they are
     * be responsible for this event. Handlers should not process this event if
     * {@link isHandled()} return {@code true}.
     */
    private boolean handled;
    private boolean authorized = true;

    PlaceRequestInternalEvent(
            PlaceRequest request,
            boolean updateBrowserHistory) {
        this.request = request;
        this.updateBrowserHistory = updateBrowserHistory;
    }

    /**
     * Fires a {@link PlaceRequestInternalEvent}
     * into a source that has access to an {@com.google.web.bindery.event.shared.EventBus}.
     * <p>
     * <b>Important!</b> You should not fire that event directly, see
     * {@link PlaceRequestInternalEvent} for more details.
     *
     * @param source               The source that fires this event ({@link HasHandlers}).
     * @param request              The request.
     * @param updateBrowserHistory {@code true} If the browser URL should be updated, {@code false}
     *                             otherwise.
     */
    public static void fire(HasHandlers source, PlaceRequest request, boolean updateBrowserHistory) {
        source.fireEvent(new PlaceRequestInternalEvent(request, updateBrowserHistory));
    }

    public static Type<PlaceRequestInternalHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<>();
        }
        return TYPE;
    }

    @Override
    public Type<PlaceRequestInternalHandler> getAssociatedType() {
        return getType();
    }

    public PlaceRequest getRequest() {
        return request;
    }

    /**
     * Checks if the user was authorized to see the page.
     *
     * @return {@code true} if the user was authorized. {@code false} otherwise.
     */
    public boolean isAuthorized() {
        return authorized;
    }

    /**
     * Checks if the event was handled. If it was, then it should not be processed
     * further.
     *
     * @return {@code true} if the event was handled. {@code false} otherwise.
     */
    public boolean isHandled() {
        return handled;
    }

    public boolean shouldUpdateBrowserHistory() {
        return updateBrowserHistory;
    }

    /**
     * Indicates that the event was handled and that other handlers should not
     * process it.
     */
    public void setHandled() {
        handled = true;
    }

    /**
     * Indicates that the event was handled but that the user was not authorized
     * to view the request page.
     */
    public void setUnauthorized() {
        authorized = false;
    }

    @Override
    protected void dispatch(PlaceRequestInternalHandler handler) {
        handler.onPlaceRequest(this);
    }
}
