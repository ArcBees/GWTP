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

package com.gwtplatform.dispatch.rpc.client.interceptor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.client.ExecuteCommand;
import com.gwtplatform.dispatch.rpc.client.DispatchCall;
import com.gwtplatform.dispatch.rpc.client.RpcDispatchCallFactory;
import com.gwtplatform.dispatch.rpc.client.RpcDispatchExecuteCall;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * {@code AsyncCallback} implementation wrapping another {@link AsyncCallback} object used by a {@code
 * com.gwtplatform.dispatch.client.interceptor.Interceptor} implementations to delegate the execution
 * result.
 *
 * @param <A> the {@code TypedAction} type.
 * @param <R> the result type for this action.
 */
public class RpcInterceptedAsyncCallback<A extends Action<R>, R extends Result>
        implements AsyncCallback<RpcInterceptor<?, ?>>, ExecuteCommand<A, AsyncCallback<R>> {
    private final RpcDispatchCallFactory dispatchCallFactory;
    private final DispatchCall<A, R> dispatchCall;
    private final A action;
    private final AsyncCallback<R> callback;
    private final DelegatingDispatchRequest dispatchRequest;

    public RpcInterceptedAsyncCallback(
            RpcDispatchCallFactory dispatchCallFactory,
            DispatchCall<A, R> dispatchCall,
            A action,
            AsyncCallback<R> callback,
            DelegatingDispatchRequest dispatchRequest) {
        this.dispatchCallFactory = dispatchCallFactory;
        this.dispatchCall = dispatchCall;
        this.action = action;
        this.callback = callback;
        this.dispatchRequest = dispatchRequest;
    }

    @Override
    public DispatchRequest execute(A action, AsyncCallback<R> resultCallback) {
        if (dispatchRequest.isPending()) {
            RpcDispatchExecuteCall<A, R> newDispatchCall = dispatchCallFactory.create(action, resultCallback);
            newDispatchCall.setIntercepted(true);

            return newDispatchCall.execute();
        } else {
            return null;
        }
    }

    @Override
    public void onSuccess(RpcInterceptor<?, ?> result) {
        handleSuccess(result);
    }

    @Override
    public void onFailure(Throwable caught) {
        handleFailure(caught);
    }

    protected void handleSuccess(RpcInterceptor<?, ?> interceptor) {
        if (!interceptor.canExecute(action)) {
            delegateFailure(interceptor);
        } else if (getDispatchRequest().isPending()) {
            delegateExecute((RpcInterceptor<A, R>) interceptor);
        }
    }

    protected void handleFailure(Throwable caught) {
        dispatchRequest.cancel();

        dispatchCall.onExecuteFailure(caught, null);
    }

    protected void delegateFailure(RpcInterceptor<?, ?> interceptor) {
        InterceptorMismatchException exception =
                new InterceptorMismatchException(action.getClass(), interceptor.getActionType());

        if (dispatchCall.shouldHandleFailure(exception)) {
            handleFailure(exception);
        }
    }

    @SuppressWarnings("unchecked")
    protected void delegateExecute(RpcInterceptor<A, R> interceptor) {
        dispatchRequest.setDelegate(interceptor.execute(action, callback, this));
    }

    protected DelegatingDispatchRequest getDispatchRequest() {
        return dispatchRequest;
    }
}
