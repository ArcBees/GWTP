/**
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

import javax.inject.Inject;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * The default implementation for {@link RpcDispatchCallFactory}.
 */
public class DefaultRpcDispatchCallFactory implements RpcDispatchCallFactory {
    private final DispatchServiceAsync dispatchService;
    private final ExceptionHandler exceptionHandler;
    private final ClientActionHandlerRegistry clientActionHandlerRegistry;
    private final SecurityCookieAccessor securityCookieAccessor;

    @Inject
    DefaultRpcDispatchCallFactory(
            DispatchServiceAsync dispatchService,
            @RpcBinding ExceptionHandler exceptionHandler,
            @RpcBinding ClientActionHandlerRegistry clientActionHandlerRegistry,
            @RpcBinding SecurityCookieAccessor securityCookieAccessor) {
        this.dispatchService = dispatchService;
        this.exceptionHandler = exceptionHandler;
        this.clientActionHandlerRegistry = clientActionHandlerRegistry;
        this.securityCookieAccessor = securityCookieAccessor;
    }

    @Override
    public <A extends Action<R>, R extends Result> RpcDispatchExecuteCall<A, R> create(A action,
            AsyncCallback<R> callback) {
        return new RpcDispatchExecuteCall<A, R>(dispatchService, exceptionHandler, clientActionHandlerRegistry,
                securityCookieAccessor, action, callback);
    }

    @Override
    public <A extends Action<R>, R extends Result> RpcDispatchUndoCall<A, R> create(A action,
            R result, AsyncCallback<Void> callback) {
        return new RpcDispatchUndoCall<A, R>(dispatchService, exceptionHandler, clientActionHandlerRegistry,
                securityCookieAccessor, action, result, callback);
    }
}
