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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.DispatchCall;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.rpc.client.interceptor.RpcInterceptorRegistry;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * A class representing an undo call to be sent to the server using RPC.
 *
 * @param <A> the {@link Action} type.
 * @param <R> the {@link Result} type for this action.
 */
public class RpcDispatchUndoCall<A extends Action<R>, R extends Result> extends DispatchCall<A, R> {
    private static class AsyncCallbackWrapper<R extends Result> implements AsyncCallback<R> {
        private final AsyncCallback<?> wrapped;

        AsyncCallbackWrapper(AsyncCallback<?> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void onFailure(Throwable caught) {
            wrapped.onFailure(caught);
        }

        @Override
        public void onSuccess(R result) {
            wrapped.onSuccess(null);
        }
    }

    private final DispatchServiceAsync dispatchService;
    private final RpcDispatchHooks dispatchHooks;
    private final RpcInterceptorRegistry interceptorRegistry;
    private final R result;

    RpcDispatchUndoCall(DispatchServiceAsync dispatchService,
                        ExceptionHandler exceptionHandler,
                        RpcInterceptorRegistry interceptorRegistry,
                        SecurityCookieAccessor securityCookieAccessor,
                        RpcDispatchHooks dispatchHooks,
                        A action,
                        R result,
                        AsyncCallback<Void> callback) {
        super(exceptionHandler, securityCookieAccessor, action, new AsyncCallbackWrapper<R>(callback));

        this.dispatchService = dispatchService;
        this.dispatchHooks = dispatchHooks;
        this.interceptorRegistry = interceptorRegistry;
        this.result = result;
    }

    @Override
    public DispatchRequest execute() {
        dispatchHooks.onExecute(getAction(), true);

        // TODO: are undo calls interceptable?

        return doExecute();
    }

    @Override
    protected DispatchRequest doExecute() {
        return new GwtHttpDispatchRequest(dispatchService.undo(getSecurityCookie(),
            getAction(), result, new AsyncCallback<Void>() {
                public void onFailure(Throwable caught) {
                    RpcDispatchUndoCall.this.onExecuteFailure(caught);

                    dispatchHooks.onFailure(getAction(), caught, true);
                }

                @SuppressWarnings("unchecked")
                public void onSuccess(Void nothing) {
                    RpcDispatchUndoCall.this.onExecuteSuccess((R) result);

                    dispatchHooks.onSuccess(getAction(), result, true);
                }
            }
        ));
    }
}
