/**
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

import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.dispatch.client.ExceptionHandler.Status;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 */
public abstract class DispatchCall<A extends TypedAction<R>, R> {
    private final A action;
    private final AsyncCallback<R> callback;
    private final ClientActionHandlerRegistry clientActionHandlerRegistry;
    private final ExceptionHandler exceptionHandler;
    private final SecurityCookieAccessor securityCookieAccessor;

    private String securityCookie;

    public DispatchCall(ExceptionHandler exceptionHandler,
                        ClientActionHandlerRegistry clientActionHandlerRegistry,
                        SecurityCookieAccessor securityCookieAccessor,
                        A action,
                        AsyncCallback<R> callback) {
        this.action = action;
        this.callback = callback;
        this.exceptionHandler = exceptionHandler;
        this.clientActionHandlerRegistry = clientActionHandlerRegistry;
        this.securityCookieAccessor = securityCookieAccessor;
    }

    public DispatchRequest execute() {
        securityCookie = securityCookieAccessor.getCookieContent();

        IndirectProvider<ClientActionHandler<?, ?>> clientActionHandlerProvider =
                clientActionHandlerRegistry.find(action.getClass());

        if (clientActionHandlerProvider != null) {
            DelegatingDispatchRequest dispatchRequest = new DelegatingDispatchRequest();
            DelegatingAsyncCallback<A, R> delegatingCallback =
                    new DelegatingAsyncCallback<A, R>(this, action, callback, dispatchRequest);

            clientActionHandlerProvider.get(delegatingCallback);

            return dispatchRequest;
        } else {
            return doExecute();
        }
    }

    protected abstract DispatchRequest doExecute();

    protected A getAction() {
        return action;
    }

    protected AsyncCallback<R> getCallback() {
        return callback;
    }

    protected ClientActionHandlerRegistry getClientActionHandlerRegistry() {
        return clientActionHandlerRegistry;
    }

    protected ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    protected SecurityCookieAccessor getSecurityCookieAccessor() {
        return securityCookieAccessor;
    }

    protected String getSecurityCookie() {
        return securityCookie;
    }

    protected void onExecuteSuccess(R result) {
        callback.onSuccess(result);
    }

    protected void onExecuteSuccess(R result, Response response) {
        onExecuteSuccess(result);
    }

    protected void onExecuteFailure(Throwable caught) {
        if (exceptionHandler != null && exceptionHandler.onFailure(caught) == Status.STOP) {
            return;
        }

        callback.onFailure(caught);
    }

    protected void onExecuteFailure(Throwable caught, Response response) {
        onExecuteFailure(caught);
    }
}
