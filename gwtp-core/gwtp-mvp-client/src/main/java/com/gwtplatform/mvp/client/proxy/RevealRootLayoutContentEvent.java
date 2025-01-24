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
import com.gwtplatform.mvp.client.Presenter;

/**
 * This event is fired by a {@link Presenter} that desires to reveal itself
 * at the root of the application. It is typically fired in the {@link Presenter#revealInParent()}
 * method.
 * <p>
 * This type of content
 * is constrained to lie within the browser window, and to resize with it. You
 * will be responsible for adding your own scrollbars as content overflow,
 * usually via {@link com.google.gwt.user.client.ui.ScrollPanel}.
 *
 * @see RevealContentEvent
 * @see RevealRootContentEvent
 * @see RevealRootPopupContentEvent
 */
public final class RevealRootLayoutContentEvent extends GwtEvent<RevealRootLayoutContentHandler> {
    private static final Type<RevealRootLayoutContentHandler> TYPE = new Type<RevealRootLayoutContentHandler>();

    private final Presenter<?, ?> content;

    public RevealRootLayoutContentEvent(Presenter<?, ?> content) {
        this.content = content;
    }

    /**
     * Fires a {@link RevealRootLayoutContentEvent}
     * into a source that has access to an {@link com.google.web.bindery.event.shared.EventBus}.
     *
     * @param source  The source that fires this event ({@link HasHandlers}).
     * @param content The {@link Presenter} that wants to set itself as root content.
     */
    public static void fire(final HasHandlers source, final Presenter<?, ?> content) {
        source.fireEvent(new RevealRootLayoutContentEvent(content));
    }

    public static Type<RevealRootLayoutContentHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<RevealRootLayoutContentHandler> getAssociatedType() {
        return getType();
    }

    public Presenter<?, ?> getContent() {
        return content;
    }

    @Override
    protected void dispatch(RevealRootLayoutContentHandler handler) {
        handler.onRevealRootLayoutContent(this);
    }
}
