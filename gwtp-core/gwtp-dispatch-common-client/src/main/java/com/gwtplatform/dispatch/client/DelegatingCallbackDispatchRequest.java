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

package com.gwtplatform.dispatch.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * An implementation of {@link DispatchRequest} that should be used by
 * {@link com.gwtplatform.dispatch.client.interceptor.Interceptor}s that make asynchronous calls that return a
 * {@link com.google.gwt.http.client.Request Request}.
 * <p>
 * This class also takes a {@link DispatchRequest} and delegate work to this {@link DispatchRequest}.
 * <p>
 * This class is used within
 * com.gwtplatform.dispatch.rpc.client.interceptor.caching.AbstractCachingRpcInterceptor to be able to store
 * inside an {@link java.util.HashMap HashMap} {@link com.gwtplatform.dispatch.client.DefaultCallbackDispatchRequest}
 * while keeping {@link GwtHttpDispatchRequest} nature.
 *
 * @param <R> The type of the {@link AsyncCallback}.
 */
public class DelegatingCallbackDispatchRequest<R> implements CallbackDispatchRequest<R> {
    private final DispatchRequest request;
    private final AsyncCallback<R> callback;

    public DelegatingCallbackDispatchRequest(DispatchRequest request,
                                             AsyncCallback<R> callback) {

        this.request = request;
        this.callback = callback;
    }

    @Override
    public void cancel() {
        request.cancel();
    }

    @Override
    public boolean isPending() {
        return request.isPending();
    }

    @Override
    public void onFailure(Throwable caught) {
        callback.onFailure(caught);
    }

    @Override
    public void onSuccess(R result) {
        callback.onSuccess(result);
    }
}
