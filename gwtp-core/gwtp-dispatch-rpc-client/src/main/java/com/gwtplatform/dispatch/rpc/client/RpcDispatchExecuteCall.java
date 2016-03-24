/*
 * Copyright 2013 ArcBees Inc.
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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.rpc.client.interceptor.RpcInterceptedAsyncCallback;
import com.gwtplatform.dispatch.rpc.client.interceptor.RpcInterceptor;
import com.gwtplatform.dispatch.rpc.client.interceptor.RpcInterceptorRegistry;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * A class representing an execute call to be sent to the server using RPC.
 *
 * @param <A> the {@link Action} type.
 * @param <R> the {@link Result} type for this action.
 */
public class RpcDispatchExecuteCall<A extends Action<R>, R extends Result>
        extends DispatchCall<A, R> {
    private final RpcDispatchCallFactory dispatchCallFactory;
    private final DispatchServiceAsync dispatchService;
    private final RpcDispatchHooks dispatchHooks;
    private final RpcInterceptorRegistry interceptorRegistry;

    RpcDispatchExecuteCall(
            RpcDispatchCallFactory dispatchCallFactory,
            DispatchServiceAsync dispatchService,
            ExceptionHandler exceptionHandler,
            RpcInterceptorRegistry interceptorRegistry,
            SecurityCookieAccessor securityCookieAccessor,
            RpcDispatchHooks dispatchHooks,
            A action,
            AsyncCallback<R> callback) {
        super(exceptionHandler, securityCookieAccessor, action, callback);

        this.dispatchCallFactory = dispatchCallFactory;
        this.dispatchService = dispatchService;
        this.dispatchHooks = dispatchHooks;
        this.interceptorRegistry = interceptorRegistry;
    }

    @Override
    public DispatchRequest execute() {
        A action = getAction();
        dispatchHooks.onExecute(action, false);

        if (!isIntercepted()) {
            IndirectProvider<RpcInterceptor<?, ?>> interceptorIndirectProvider = interceptorRegistry.find(action);

            if (interceptorIndirectProvider != null) {
                DelegatingDispatchRequest dispatchRequest = new DelegatingDispatchRequest();
                RpcInterceptedAsyncCallback<A, R> delegatingCallback = new RpcInterceptedAsyncCallback<>(
                        dispatchCallFactory, this, action, getCallback(), dispatchRequest);

                interceptorIndirectProvider.get(delegatingCallback);

                return dispatchRequest;
            }
        }

        return processCall();
    }

    @Override
    public void onExecuteSuccess(R result, Response response) {
        getCallback().onSuccess(result);
    }

    @Override
    public void onExecuteFailure(Throwable caught, Response response) {
        if (shouldHandleFailure(caught)) {
            getCallback().onFailure(caught);
        }
    }

    @Override
    protected DispatchRequest processCall() {
        Request request = dispatchService.execute(getSecurityCookie(), getAction(),
                new AsyncCallback<R>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        RpcDispatchExecuteCall.this.onExecuteFailure(caught, null);

                        dispatchHooks.onFailure(getAction(), caught, false);
                    }

                    @Override
                    public void onSuccess(R result) {
                        RpcDispatchExecuteCall.this.onExecuteSuccess(result, null);

                        dispatchHooks.onSuccess(getAction(), result, false);
                    }
                }
        );
        return new GwtHttpDispatchRequest(request);
    }
}
