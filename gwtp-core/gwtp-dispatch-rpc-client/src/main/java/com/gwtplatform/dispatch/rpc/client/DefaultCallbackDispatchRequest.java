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

package com.gwtplatform.dispatch.rpc.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An implementation of {@link CallbackDispatchRequest} that should be used by
 * {@code com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler}s that make asynchronous calls that do not
 * return a {@link com.google.gwt.http.client.Request}.
 * <p>
 * {@link #isPending()} will return true until either {@link #onSuccess(Object)} or {@link #onFailure} is called.
 * <p>
 * Calling {@link #cancel()} will prevent the {@link #onSuccess(Object)} and {@link #onFailure(Throwable)} from being
 * forwarded to the code that requested the action handler be executed/undone.
 *
 * @param <R> The type of the {@link AsyncCallback}.
 */

public class DefaultCallbackDispatchRequest<R> implements CallbackDispatchRequest<R> {
    private final AsyncCallback<R> callback;

    private boolean pending;

    /**
     * Construct a {@link DefaultCallbackDispatchRequest}. See the class documentation for details.
     *
     * @param callback The {@code resultCallback} parameter passed to
     *                 {@code ClientActionHandler#execute()}
     *                 or the callback parameter passed to
     *                 {@code ClientActionHandler#undo()}
     */
    public DefaultCallbackDispatchRequest(AsyncCallback<R> callback) {
        this.callback = callback;
        this.pending = true;
    }

    @Override
    public void cancel() {
        pending = false;
    }

    @Override
    public boolean isPending() {
        return pending;
    }

    @Override
    public void onFailure(Throwable caught) {
        if (pending) {
            pending = false;
            callback.onFailure(caught);
        }
    }

    @Override
    public void onSuccess(R result) {
        if (pending) {
            pending = false;
            callback.onSuccess(result);
        }
    }
}
