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
 * This event is fired whenever the user wants to have access to the title of a
 * place.
 * <p>
 * <b>Important!</b> You should never fire that event directly. Instead, use
 * {@link PlaceManager#getCurrentTitle(SetPlaceTitleHandler)} or
 * {@link PlaceManager#getTitle(int, SetPlaceTitleHandler)}.
 */
public class GetPlaceTitleEvent extends GwtEvent<GetPlaceTitleHandler> {
    private static Type<GetPlaceTitleHandler> TYPE;

    private final SetPlaceTitleHandler handler;
    private final PlaceRequest request;

    /**
     * The handled flag can let others know when the event has been handled.
     * Handlers should call {@link #setHandled()} as soon as they figure they are
     * be responsible for this event. Handlers should not process this event if
     * {@link #isHandled()} return {@code true}.
     */
    private boolean handled;

    public GetPlaceTitleEvent(
            PlaceRequest request,
            SetPlaceTitleHandler handler) {
        this.request = request;
        this.handler = handler;
    }

    /**
     * Fires a {@link GetPlaceTitleEvent}
     * into a source that has access to an {@link com.google.web.bindery.event.shared.EventBus}.
     * <p>
     * <b>Important!</b> You should never fire that event directly. See
     * {@link GetPlaceTitleEvent} for details.
     *
     * @param source  The source that fires this event ({@link HasHandlers}).
     * @param request The {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest} for which to obtain the title.
     * @param handler The {@link SetPlaceTitleHandler} that will be invoked when
     *                the title is obtained.
     */
    public static void fire(HasHandlers source, PlaceRequest request, SetPlaceTitleHandler handler) {
        source.fireEvent(new GetPlaceTitleEvent(request, handler));
    }

    public static Type<GetPlaceTitleHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<GetPlaceTitleHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<GetPlaceTitleHandler> getAssociatedType() {
        return getType();
    }

    public SetPlaceTitleHandler getHandler() {
        return handler;
    }

    public PlaceRequest getRequest() {
        return request;
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

    /**
     * Indicates that the event was handled and that other handlers should not
     * process it.
     */
    public void setHandled() {
        handled = true;
    }

    @Override
    protected void dispatch(GetPlaceTitleHandler handler) {
        handler.onGetPlaceTitle(this);
    }
}
