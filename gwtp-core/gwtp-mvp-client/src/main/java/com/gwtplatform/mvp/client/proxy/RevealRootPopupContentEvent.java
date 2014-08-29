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
import com.gwtplatform.mvp.client.GenericPresenterWidget;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

/**
 * This event is fired when a {@link com.gwtplatform.mvp.client.PresenterWidget}
 * wants to reveal itself as a popup at the root of the application. It is
 * typically fired by the {@link com.gwtplatform.mvp.client.PresenterWidget}'s
 * parent.<br/>
 * Use this type of event to reveal popup content that should get added at the
 * root of the presenter hierarchy.
 * 
 * @see RevealContentEvent
 * @see RevealRootContentEvent
 * @see RevealRootLayoutContentEvent
 */
public final class RevealRootPopupContentEvent extends
        GwtEvent<RevealRootPopupContentHandler> {

    private static final Type<RevealRootPopupContentHandler> TYPE = new Type<RevealRootPopupContentHandler>();
    private final GenericPresenterWidget<Object, ? extends PopupView> content;

    public RevealRootPopupContentEvent(
           GenericPresenterWidget<Object, ? extends PopupView> content) {
        this.content = content;
    }

    @Deprecated
    public RevealRootPopupContentEvent(
           GenericPresenterWidget<Object, ? extends PopupView> content, boolean center) {
        this(content);
    }

    /**
     * Fires a {@link RevealRootPopupContentEvent} into a source that has access
     * to an {@link com.google.web.bindery.event.shared.EventBus}.
     * 
     * @param source
     *            The source that fires this event ({@link HasHandlers}).
     * @param content
     *            The {@link PresenterWidget} with a {@link PopupView} that
     *            wants to set itself as root content.
     */
    public static void fire(final HasHandlers source,
            final GenericPresenterWidget<Object, ? extends PopupView> content) {
        source.fireEvent(new RevealRootPopupContentEvent(content));
    }

    /**
     * Fires a {@link RevealRootPopupContentEvent} into a source that has access
     * to an {@link com.google.web.bindery.event.shared.EventBus}.
     * 
     * @param source
     *            The source that fires this event ({@link HasHandlers}).
     * @param content
     *            The {@link PresenterWidget} with a {@link PopupView} that
     *            wants to set itself as root content.
     * @param center
     *            Does nothing.
     */
    @Deprecated
    public static void fire(final HasHandlers source,
            final GenericPresenterWidget<Object, ? extends PopupView> content,
            final boolean center) {
        fire(source, content);
    }

    public static Type<RevealRootPopupContentHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<RevealRootPopupContentHandler> getAssociatedType() {
        return getType();
    }

    public GenericPresenterWidget<Object, ? extends PopupView> getContent() {
        return content;
    }

    @Deprecated
    public boolean isCentered() {
        return true;
    }

    @Override
    protected void dispatch(RevealRootPopupContentHandler handler) {
        handler.onRevealRootPopupContent(this);
    }
}
