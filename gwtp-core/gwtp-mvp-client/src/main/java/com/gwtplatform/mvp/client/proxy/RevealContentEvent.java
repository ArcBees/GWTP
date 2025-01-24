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
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;

/**
 * This event is fired by a {@link Presenter} that desires to reveal itself within its parent. It is typically fired in
 * the {@link Presenter#revealInParent()} method. To reveal a presenter at the root of the application, fire either
 * {@link RevealRootContentEvent}, {@link RevealRootLayoutContentEvent} or {@link RevealRootPopupContentEvent} instead.
 * <p>
 * This event is handled by {@link Proxy} classes. Upon handling this event, the proxy <b>first</b> sets the content
 * appropriately in the presenter, and then reveals the presenter.
 *
 * @see RevealRootContentEvent
 * @see RevealRootLayoutContentEvent
 * @see RevealRootPopupContentEvent
 */
public final class RevealContentEvent extends GwtEvent<RevealContentHandler<?>> {
    private final Presenter<?, ?> content;
    private final NestedSlot type;

    public RevealContentEvent(
            NestedSlot type,
            Presenter<?, ?> content) {
        this.type = type;
        this.content = content;
    }

    /**
     * Fires a {@link RevealContentEvent} with a specific {@link NestedSlot} into a source that has access to an {@link
     * com.google.web.bindery.event.shared.EventBus EventBus}.
     *
     * @param source The source that fires this event ({@link HasHandlers}).
     * @param type The specific event {@link NestedSlot}, usually defined in the parent presenter.
     * @param content The {@link Presenter} that wants to set itself as content in his parent.
     */
    public static void fire(HasHandlers source, NestedSlot type, Presenter<?, ?> content) {
        source.fireEvent(new RevealContentEvent(type, content));
    }

    @Override
    public NestedSlot getAssociatedType() {
        return type;
    }

    public Presenter<?, ?> getContent() {
        return content;
    }

    @Override
    protected void dispatch(RevealContentHandler<?> handler) {
        handler.onRevealContent(this);
    }
}
