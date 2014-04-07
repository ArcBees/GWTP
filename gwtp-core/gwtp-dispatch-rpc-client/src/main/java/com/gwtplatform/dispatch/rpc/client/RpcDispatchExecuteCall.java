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
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
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
public class RpcDispatchExecuteCall<A extends Action<R>, R extends Result> extends DispatchCall<A, R> {
    private final DispatchServiceAsync dispatchService;

    RpcDispatchExecuteCall(
            DispatchServiceAsync dispatchService,
            ExceptionHandler exceptionHandler,
            ClientActionHandlerRegistry clientActionHandlerRegistry,
            SecurityCookieAccessor securityCookieAccessor,
            A action,
            AsyncCallback<R> callback) {
        super(exceptionHandler, clientActionHandlerRegistry, securityCookieAccessor, action, callback);

        this.dispatchService = dispatchService;
    }

    @Override
    protected DispatchRequest doExecute() {
        return new GwtHttpDispatchRequest(dispatchService.execute(getSecurityCookie(), getAction(),
                new AsyncCallback<Result>() {
                    public void onFailure(Throwable caught) {
                        RpcDispatchExecuteCall.this.onExecuteFailure(caught);
                    }

                    @SuppressWarnings("unchecked")
                    public void onSuccess(Result result) {
                        RpcDispatchExecuteCall.this.onExecuteSuccess((R) result);
                    }
                }
        ));
    }
}
