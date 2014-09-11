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

package com.gwtplatform.mvp.client.proxy;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.presenter.slots.TypeSlot;

/**
 * This event is fired by a {@link com.gwtplatform.mvp.client.Presenter} that desires to reveal itself
 * within its parent. It is typically fired in the {@link com.gwtplatform.mvp.client.Presenter#revealInParent()}
 * method. To reveal a presenter at the root of the application, fire either
 * {@link RevealRootContentEvent}, {@link RevealRootLayoutContentEvent} or
 * {@link RevealRootPopupContentEvent} instead.
 * <p/>
 * This event is handled by {@link Proxy} classes. Upon handling this
 * event, the proxy <b>first</b> sets the content appropriately in the
 * presenter, and then reveals the presenter.
 *
 * @see RevealRootContentEvent
 * @see RevealRootLayoutContentEvent
 * @see RevealRootPopupContentEvent
 */
public final class RevealContentEvent<T extends Presenter<?,?>> extends GwtEvent<RevealContentHandler<?>> {

    /**
     * Fires a {@link RevealContentEvent} with a specific {@link com.google.gwt.event.shared.GwtEvent.Type}
     * into a source that has access to an {@link com.google.web.bindery.event.shared.EventBus}.
     *
     * @param source  The source that fires this event ({@link HasHandlers}).
     * @param type    The specific event {@link com.google.gwt.event.shared.GwtEvent.Type},
     *                usually defined in the parent presenter
     *                and annotated with {@link com.gwtplatform.mvp.client.annotations.ContentSlot}.
     * @param content The {@link Presenter} that wants to set itself as content in his parent.
     */
    public static <T extends Presenter<?,?>> void fire(final HasHandlers source,
            final TypeSlot<T> slot, final T content) {
        source.fireEvent(new RevealContentEvent<T>(slot, content));
    }

    private final T content;

    private final TypeSlot<T> type;

    public RevealContentEvent(TypeSlot<T> type,T content) {
        this.type = type;
        this.content = content;
    }

    @Override
    public TypeSlot<T> getAssociatedType() {
        return type;
    }

    public T getContent() {
        return content;
    }

    @Override
    protected void dispatch(RevealContentHandler<?> handler) {
        handler.onRevealContent(this);
    }

}
