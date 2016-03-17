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
import com.gwtplatform.dispatch.client.DelegatingAsyncCallback;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.client.DispatchCall;
import com.gwtplatform.dispatch.rpc.client.RpcDispatchCallFactory;
import com.gwtplatform.dispatch.rpc.client.RpcDispatchExecuteCall;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 * {@code AsyncCallback} implementation wrapping another {@link AsyncCallback} object used by a {@link
 * com.gwtplatform.dispatch.client.interceptor.Interceptor Interceptor} implementations to delegate the execution
 * result.
 *
 * @param <A> the {@link TypedAction} type.
 * @param <R> the result type for this action.
 */
public class RpcInterceptedAsyncCallback<A extends Action<R>, R extends Result>
        extends DelegatingAsyncCallback<A, R, RpcInterceptor<?, ?>, AsyncCallback<R>>
        implements AsyncCallback<RpcInterceptor<?, ?>> {
    private final RpcDispatchCallFactory dispatchCallFactory;

    public RpcInterceptedAsyncCallback(
            RpcDispatchCallFactory dispatchCallFactory,
            DispatchCall<A, R, AsyncCallback<R>> dispatchCall,
            A action,
            AsyncCallback<R> callback,
            DelegatingDispatchRequest dispatchRequest) {
        super(dispatchCall, action, callback, dispatchRequest);

        this.dispatchCallFactory = dispatchCallFactory;
    }

    @Override
    public DispatchRequest execute(A action, AsyncCallback<R> resultCallback) {
        if (getDispatchRequest().isPending()) {
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
}
