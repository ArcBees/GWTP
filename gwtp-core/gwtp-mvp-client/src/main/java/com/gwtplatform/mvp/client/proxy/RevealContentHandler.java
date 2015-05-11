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
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Command;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;

/**
 * This is the handler class for {@link RevealContentEvent}. It should be used
 * by any {@link Proxy} class of a {@link Presenter} that accepts child
 * presenters. When this handler is triggered, the proxy should <b>first</b> set
 * the content appropriately in the presenter, and then reveal the presenter.
 *
 * @param <T> The Presenter's type.
 */
public class RevealContentHandler<T extends Presenter<?, ?>> implements EventHandler {

    private final EventBus eventBus;
    private final ProxyImpl<T> proxy;

    public RevealContentHandler(final EventBus eventBus,
            final ProxyImpl<T> proxy) {
        this.eventBus = eventBus;
        this.proxy = proxy;
    }

    /**
     * This is the dispatched method. Reveals
     *
     * @param revealContentEvent The event containing the presenter that wants to
     *                           bet set as content.
     */
    public final void onRevealContent(final RevealContentEvent revealContentEvent) {
        proxy.getPresenter(new NotifyingAsyncCallback<T>(eventBus) {

            @Override
            public void success(final T presenter) {
                // Deferring is needed because the event bus enqueues and delays handler
                // registration when events are currently being processed.
                // (see {@link com.google.gwt.event.shared.HandlerManager@addHandler()})
                // So if a presenter registers a handler in its onBind() method and a
                // child fires the event in its onReveal() method, then the event might
                // get lost because the handler is not officially registered yet.
                Scheduler.get().scheduleDeferred(new Command() {
                    @Override
                    public void execute() {
                        presenter.forceReveal();
                        if (revealContentEvent.getAssociatedType() instanceof NestedSlot) {
                            presenter.setInSlot((NestedSlot) revealContentEvent.getAssociatedType(),
                                    revealContentEvent.getContent());
                        } else {
                            presenter.setInSlot(revealContentEvent.getAssociatedType(),
                                    revealContentEvent.getContent());
                        }
                    }
                });
            }
        });
    }

}
