/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.interceptor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.client.ExecuteCommand;
import com.gwtplatform.dispatch.rest.client.RestCallback;
import com.gwtplatform.dispatch.rest.client.core.DispatchCallFactory;
import com.gwtplatform.dispatch.rest.client.core.RestDispatchCall;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * {@code AsyncCallback} implementation wrapping another {@link AsyncCallback} object used by a {@link
 * com.gwtplatform.dispatch.client.interceptor.Interceptor Interceptor} implementations to delegate the execution
 * result.
 *
 * @param <A> the {@link TypedAction} type.
 * @param <R> the result type for this action.
 */
public class RestInterceptedAsyncCallback<A extends RestAction<R>, R>
        implements ExecuteCommand<A, RestCallback<R>> {
    private final DispatchCallFactory dispatchCallFactory;
    private final RestDispatchCall<A, R> dispatchCall;
    private final A action;
    private final RestCallback<R> callback;
    private final DelegatingDispatchRequest dispatchRequest;

    public RestInterceptedAsyncCallback(
            DispatchCallFactory dispatchCallFactory,
            RestDispatchCall<A, R> dispatchCall,
            A action,
            RestCallback<R> callback,
            DelegatingDispatchRequest dispatchRequest) {
        this.dispatchCallFactory = dispatchCallFactory;
        this.dispatchCall = dispatchCall;
        this.action = action;
        this.callback = callback;
        this.dispatchRequest = dispatchRequest;
    }

    @Override
    public DispatchRequest execute(A action, RestCallback<R> resultCallback) {
        if (dispatchRequest.isPending()) {
            RestDispatchCall<A, R> newDispatchCall = dispatchCallFactory.create(action, resultCallback);
            newDispatchCall.setIntercepted(true);

            return newDispatchCall.execute();
        } else {
            return null;
        }
    }

    public AsyncCallback<RestInterceptor> asAsyncCallback() {
        return new AsyncCallback<RestInterceptor>() {
            @Override
            public void onFailure(Throwable caught) {
                handleFailure(caught);
            }

            @Override
            public void onSuccess(RestInterceptor result) {
                handleSuccess(result);
            }
        };
    }

    protected void handleSuccess(RestInterceptor interceptor) {
        if (dispatchRequest.isPending()) {
            delegateExecute(interceptor);
        }
    }

    protected void handleFailure(Throwable caught) {
        dispatchRequest.cancel();

        dispatchCall.onExecuteFailure(caught, null);
    }

    protected void delegateExecute(RestInterceptor interceptor) {
        dispatchRequest.setDelegate(interceptor.execute(action, callback, this));
    }
}
