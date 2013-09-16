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

package com.gwtplatform.dispatch.rest.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.DispatchCall;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestCallback;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

public class RestDispatchCall<A extends RestAction<R>, R> extends DispatchCall<A, R> {
    private final RestRequestBuilderFactory requestBuilderFactory;
    private final RestResponseDeserializer restResponseDeserializer;

    public RestDispatchCall(ExceptionHandler exceptionHandler,
                            ClientActionHandlerRegistry clientActionHandlerRegistry,
                            SecurityCookieAccessor securityCookieAccessor,
                            RestRequestBuilderFactory requestBuilderFactory,
                            RestResponseDeserializer restResponseDeserializer,
                            A action,
                            AsyncCallback<R> callback) {
        super(action, callback, exceptionHandler, clientActionHandlerRegistry, securityCookieAccessor);

        this.requestBuilderFactory = requestBuilderFactory;
        this.restResponseDeserializer = restResponseDeserializer;
    }

    @Override
    protected RequestBuilder buildRequest(String securityCookie) throws ActionException {
        RequestBuilder requestBuilder = requestBuilderFactory.build(getAction(), securityCookie);
        requestBuilder.setCallback(createRequestCallback());

        return requestBuilder;
    }

    @Override
    protected void onExecuteSuccess(R result, Response response) {
        assignResponse(response);

        super.onExecuteSuccess(result, response);
    }

    @Override
    protected void onExecuteFailure(Throwable caught, Response response) {
        assignResponse(response);

        super.onExecuteFailure(caught, response);
    }

    private void assignResponse(Response response) {
        if (getCallback() instanceof RestCallback) {
            ((RestCallback) getCallback()).setResponse(response);
        }
    }

    private RequestCallback createRequestCallback() {
        return new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                try {
                    R result = restResponseDeserializer.deserialize(getAction(), response);

                    onExecuteSuccess(result, response);
                } catch (ActionException e) {
                    onExecuteFailure(e, response);
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                onExecuteFailure(exception);
            }
        };
    }
}
