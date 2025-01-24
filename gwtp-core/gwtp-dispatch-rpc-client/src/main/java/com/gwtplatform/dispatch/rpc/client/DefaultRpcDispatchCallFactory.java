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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.rpc.client.interceptor.RpcInterceptorRegistry;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

import jakarta.inject.Inject;

/**
 * The default implementation for {@link RpcDispatchCallFactory}.
 */
public class DefaultRpcDispatchCallFactory implements RpcDispatchCallFactory {
    private final DispatchServiceAsync dispatchService;
    private final ExceptionHandler exceptionHandler;
    private final RpcInterceptorRegistry interceptorRegistry;
    private final SecurityCookieAccessor securityCookieAccessor;
    private final RpcDispatchHooks dispatchHooks;

    @Inject
    DefaultRpcDispatchCallFactory(
            DispatchServiceAsync dispatchService,
            @RpcBinding ExceptionHandler exceptionHandler,
            @RpcBinding SecurityCookieAccessor securityCookieAccessor,
            RpcInterceptorRegistry interceptorRegistry,
            RpcDispatchHooks dispatchHooks) {
        this.dispatchService = dispatchService;
        this.exceptionHandler = exceptionHandler;
        this.interceptorRegistry = interceptorRegistry;
        this.securityCookieAccessor = securityCookieAccessor;
        this.dispatchHooks = dispatchHooks;
    }

    @Override
    public <A extends Action<R>, R extends Result> RpcDispatchExecuteCall<A, R> create(A action,
            AsyncCallback<R> callback) {
        return new RpcDispatchExecuteCall<>(this, dispatchService, exceptionHandler, interceptorRegistry,
                securityCookieAccessor, dispatchHooks, action, callback);
    }

    @Override
    public <A extends Action<R>, R extends Result> RpcDispatchUndoCall<A, R> create(A action,
            R result, AsyncCallback<Void> callback) {
        return new RpcDispatchUndoCall<>(dispatchService, exceptionHandler, securityCookieAccessor, dispatchHooks,
                action, result, callback);
    }
}
