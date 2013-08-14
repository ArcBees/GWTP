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
import com.gwtplatform.dispatch.client.AbstractDispatchAsync;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;
import com.gwtplatform.dispatch.shared.rest.RestAction;

/**
 * TODO: Documentation.
 */
public class RestDispatchAsync extends AbstractDispatchAsync {
    private static final String ILLEGAL_ACTION_ARGUMENT = "RestDispatchAsync should be used with actions implementing" +
                                                          " " +
                                                          "RestAction.";

    private final RestRequestBuilderFactory requestBuilderFactory;
    private final RestResponseDeserializer restResponseDeserializer;

    @Inject
    RestDispatchAsync(ExceptionHandler exceptionHandler,
                             ClientActionHandlerRegistry clientActionHandlerRegistry,
                             SecurityCookieAccessor securityCookieAccessor,
                             RestRequestBuilderFactory requestBuilderFactory,
                             RestResponseDeserializer responseDeserializer) {
        super(exceptionHandler, securityCookieAccessor, clientActionHandlerRegistry);

        this.requestBuilderFactory = requestBuilderFactory;
        this.restResponseDeserializer = responseDeserializer;
    }

    @Override
    protected <A extends Action<R>, R extends Result> DispatchRequest doExecute(String securityCookie, A action,
                                                                                AsyncCallback<R> callback) {
        if (!(action instanceof RestAction)) {
            throw new IllegalArgumentException(ILLEGAL_ACTION_ARGUMENT);
        }

        RestAction<R> restAction = castRestAction(action);

        try {
            RequestBuilder requestBuilder = requestBuilderFactory.build(restAction, securityCookie);
            requestBuilder.setCallback(createRequestCallback(restAction, callback));

            return new GwtHttpDispatchRequest(requestBuilder.send());
        } catch (RequestException e) {
            onExecuteFailure(action, e, callback);
        } catch (ActionException e) {
            onExecuteFailure(action, e, callback);
        }

        return new CompletedDispatchRequest();
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest undo(A action, R result,
                                                                        AsyncCallback<Void> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected <A extends Action<R>, R extends Result> DispatchRequest doUndo(String securityCookie, A action, R result,
                                                                             AsyncCallback<Void> callback) {
        return null;
    }

    protected <A extends Action<R>, R extends Result> void onExecuteFailure(A action, Throwable caught,
                                                                            Response response,
                                                                            AsyncCallback<R> callback) {
        setResponse(response, callback);

        onExecuteFailure(action, caught, callback);
    }

    protected <A extends Action<R>, R extends Result> void onExecuteSuccess(A action, R result, Response response,
                                                                            AsyncCallback<R> callback) {
        setResponse(response, callback);

        onExecuteSuccess(action, result, callback);
    }

    private <R extends Result> void setResponse(Response response, AsyncCallback<R> callback) {
        if (callback instanceof RestCallback) {
            ((RestCallback) callback).setResponse(response);
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Action<R>, R extends Result> RestAction<R> castRestAction(A action) {
        return (RestAction<R>) action;
    }

    private <R extends Result> RequestCallback createRequestCallback(final RestAction<R> action,
                                                                     final AsyncCallback<R> callback) {
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
