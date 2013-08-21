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

package com.gwtplatform.dispatch.client.rest;

import javax.inject.Inject;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.ExceptionHandler.Status;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.client.rest.actionhandler.ClientRestActionHandler;
import com.gwtplatform.dispatch.client.rest.actionhandler.ClientRestActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;
import com.gwtplatform.dispatch.shared.rest.RestAction;
import com.gwtplatform.dispatch.shared.rest.RestCallback;
import com.gwtplatform.dispatch.shared.rest.RestDispatch;

/**
 * TODO: Documentation.
 */
public class RestDispatchAsync implements RestDispatch {
    private final RestRequestBuilderFactory requestBuilderFactory;
    private final RestResponseDeserializer restResponseDeserializer;
    private final ClientRestActionHandlerRegistry clientActionHandlerRegistry;
    private final ExceptionHandler exceptionHandler;
    private final SecurityCookieAccessor securityCookieAccessor;

    @Inject
    RestDispatchAsync(ExceptionHandler exceptionHandler,
                      ClientRestActionHandlerRegistry clientActionHandlerRegistry,
                      SecurityCookieAccessor securityCookieAccessor,
                      RestRequestBuilderFactory requestBuilderFactory,
                      RestResponseDeserializer responseDeserializer) {
        this.requestBuilderFactory = requestBuilderFactory;
        this.restResponseDeserializer = responseDeserializer;
        this.exceptionHandler = exceptionHandler;
        this.clientActionHandlerRegistry = clientActionHandlerRegistry;
        this.securityCookieAccessor = securityCookieAccessor;
    }

    @Override
    public <A extends RestAction<R>, R> DispatchRequest execute(A action, AsyncCallback<R> callback) {
        String securityCookie = securityCookieAccessor.getCookieContent();

        IndirectProvider<ClientRestActionHandler<?, ?>> clientActionHandlerProvider =
                clientActionHandlerRegistry.find(action.getClass());

        if (clientActionHandlerProvider != null) {
            DelegatingDispatchRequest dispatchRequest = new DelegatingDispatchRequest();
            DelegatingAsyncCallback<A, R> delegatingCallback =
                    new DelegatingAsyncCallback<A, R>(this, action, callback, dispatchRequest, securityCookie);

            clientActionHandlerProvider.get(delegatingCallback);

            return dispatchRequest;
        } else {
            return doExecute(securityCookie, action, callback);
        }
    }

    protected <A extends RestAction<R>, R> void onExecuteSuccess(A action, R result, Response response,
                                                                 AsyncCallback<R> callback) {
        setResponse(response, callback);

        callback.onSuccess(result);
    }

    protected <A extends RestAction<R>, R> void onExecuteFailure(A action, Throwable caught,
                                                                 AsyncCallback<R> callback) {
        if (exceptionHandler != null && exceptionHandler.onFailure(caught) == Status.STOP) {
            return;
        }

        callback.onFailure(caught);
    }

    <A extends RestAction<R>, R> DispatchRequest doExecute(String securityCookie, A action, AsyncCallback<R> callback) {
        try {
            RequestBuilder requestBuilder = requestBuilderFactory.build(action, securityCookie);
            requestBuilder.setCallback(createRequestCallback(action, callback));

            return new GwtHttpDispatchRequest(requestBuilder.send());
        } catch (RequestException e) {
            onExecuteFailure(action, e, callback);
        } catch (ActionException e) {
            onExecuteFailure(action, e, callback);
        }

        return new CompletedDispatchRequest();
    }

    private <A extends RestAction<R>, R> void onExecuteFailure(A action, Throwable caught, Response response,
                                                               AsyncCallback<R> callback) {
        setResponse(response, callback);

        onExecuteFailure(action, caught, callback);
    }

    private <R> void setResponse(Response response, AsyncCallback<R> callback) {
        if (callback instanceof RestCallback) {
            ((RestCallback) callback).setResponse(response);
        }
    }

    private <R> RequestCallback createRequestCallback(final RestAction<R> action, final AsyncCallback<R> callback) {
        return new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                try {
                    R result = restResponseDeserializer.deserialize(action, response);

                    onExecuteSuccess(action, result, response, callback);
                } catch (ActionException e) {
                    onExecuteFailure(action, e, response, callback);
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                onExecuteFailure(action, exception, callback);
            }
        };
    }
}
