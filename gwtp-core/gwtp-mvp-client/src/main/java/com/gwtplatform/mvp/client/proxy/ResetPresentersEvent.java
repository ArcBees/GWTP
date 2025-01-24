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

/**
 * This event is fired whenever all visible presenters should
 * be reset. This is fired automatically right after navigating to a
 * new place.
 * <p>
 * The default implementation or {@link com.gwtplatform.mvp.client.RootPresenter} causes
 * {@link com.gwtplatform.mvp.client.PresenterWidget#onReset()}
 * to be called, starting from the top level presenter and going down.
 */
public final class ResetPresentersEvent extends
        GwtEvent<ResetPresentersHandler> {

    private static final Type<ResetPresentersHandler> type = new Type<>();

    /**
     * Fires a {@link ResetPresentersEvent}
     * into a source that has access to an {@link com.google.web.bindery.event.shared.EventBus}.
     *
     * @param source The source that fires this event ({@link HasHandlers}).
     */
    public static void fire(final HasHandlers source) {
        source.fireEvent(new ResetPresentersEvent());
    }

    public static Type<ResetPresentersHandler> getType() {
        return type;
    }

    @Override
    public Type<ResetPresentersHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(ResetPresentersHandler handler) {
        handler.onResetPresenters(this);
    }

}
