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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.dispatch.client.ExceptionHandler.Status;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.ActionException;
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

    public DispatchCall(A action,
                        AsyncCallback<R> callback,
                        ExceptionHandler exceptionHandler,
                        ClientActionHandlerRegistry clientActionHandlerRegistry,
                        SecurityCookieAccessor securityCookieAccessor) {
        this.action = action;
        this.callback = callback;
        this.exceptionHandler = exceptionHandler;
        this.clientActionHandlerRegistry = clientActionHandlerRegistry;
        this.securityCookieAccessor = securityCookieAccessor;
    }

    public DispatchRequest execute() {
        String securityCookie = securityCookieAccessor.getCookieContent();

        IndirectProvider<ClientActionHandler<?, ?>> clientActionHandlerProvider =
                clientActionHandlerRegistry.find(action.getClass());

        if (clientActionHandlerProvider != null) {
            DelegatingDispatchRequest dispatchRequest = new DelegatingDispatchRequest();
            DelegatingAsyncCallback<A, R> delegatingCallback =
                    new DelegatingAsyncCallback<A, R>(this, action, callback, dispatchRequest, securityCookie);

            clientActionHandlerProvider.get(delegatingCallback);

            return dispatchRequest;
        } else {
            return doExecute(securityCookie);
        }
    }

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

    protected DispatchRequest doExecute(String securityCookie) {
        try {
            RequestBuilder requestBuilder = buildRequest(securityCookie);

            return new GwtHttpDispatchRequest(requestBuilder.send());
        } catch (RequestException e) {
            onExecuteFailure(e);
        } catch (ActionException e) {
            onExecuteFailure(e);
        }

        return new CompletedDispatchRequest();
    }

    protected void onExecuteSuccess(R result, Response response) {
        callback.onSuccess(result);
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

    protected abstract RequestBuilder buildRequest(String securityCookie) throws ActionException;
}
