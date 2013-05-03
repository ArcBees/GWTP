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
    private final RestRequestBuilderFactory requestBuilderFactory;
    private final RestResponseDeserializer restResponseDeserializer;

    public RestDispatchAsync(ExceptionHandler exceptionHandler,
            SecurityCookieAccessor securityCookieAccessor,
            ClientActionHandlerRegistry clientActionHandlerRegistry,
            SerializerProvider serializerProvider,
            String applicationPath,
            String securityHeaderName) {
        super(exceptionHandler, securityCookieAccessor, clientActionHandlerRegistry);

        requestBuilderFactory = new RestRequestBuilderFactory(serializerProvider, applicationPath, securityHeaderName);
        restResponseDeserializer = new RestResponseDeserializer(serializerProvider);
    }

    @Override
    protected <A extends Action<R>, R extends Result> DispatchRequest doExecute(String securityCookie, A action,
            final AsyncCallback<R> callback) {
        if (!(action instanceof RestAction)) {
            throw new IllegalArgumentException("RestDispatchAsync should be used with actions implementing " +
                    "RestAction.");
        }

        final RestAction<R> restAction = castRestAction(action);

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
                    R deserializedResponse = restResponseDeserializer.deserialize(action, response);

                    onExecuteSuccess(action, deserializedResponse, callback);
                } catch (ActionException e) {
                    onExecuteFailure(action, e, callback);
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                onExecuteFailure(action, exception, callback);
            }
        };
    }
}
