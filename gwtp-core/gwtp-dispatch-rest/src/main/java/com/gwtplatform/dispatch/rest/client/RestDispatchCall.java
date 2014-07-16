/**
 * Copyright (c) 2014 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.gwtplatform.dispatch.rest.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.DispatchCall;
import com.gwtplatform.dispatch.client.DispatchHooks;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestCallback;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * A class representing an execute call to be sent to the server over HTTP.
 *
 * @param <A> the {@link RestAction} type.
 * @param <R> the result type for this action.
 */
public class RestDispatchCall<A extends RestAction<R>, R> extends DispatchCall<A, R> {
    private final RestRequestBuilderFactory requestBuilderFactory;
    private final RestResponseDeserializer restResponseDeserializer;

    public RestDispatchCall(ExceptionHandler exceptionHandler,
                            ClientActionHandlerRegistry clientActionHandlerRegistry,
                            SecurityCookieAccessor securityCookieAccessor,
                            RestRequestBuilderFactory requestBuilderFactory,
                            RestResponseDeserializer restResponseDeserializer,
                            DispatchHooks dispatchHooks,
                            A action,
                            AsyncCallback<R> callback) {
        super(exceptionHandler, clientActionHandlerRegistry, securityCookieAccessor, dispatchHooks, action, callback);

        this.requestBuilderFactory = requestBuilderFactory;
        this.restResponseDeserializer = restResponseDeserializer;
    }

    @Override
    protected DispatchRequest doExecute() {
        try {
            RequestBuilder requestBuilder = buildRequest();

            return new GwtHttpDispatchRequest(requestBuilder.send());
        } catch (RequestException | ActionException e) {
            onExecuteFailure(e);
        }

        return new CompletedDispatchRequest();
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
                RestDispatchCall.this.onResponseReceived(response);
            }

            @Override
            public void onError(Request request, Throwable exception) {
                onExecuteFailure(exception);
            }
        };
    }

    private void onResponseReceived(Response response) {
        Response wrappedResponse = new ResponseWrapper(response);

        try {
            R result = restResponseDeserializer.deserialize(getAction(), wrappedResponse);

            onExecuteSuccess(result, wrappedResponse);
        } catch (ActionException e) {
            onExecuteFailure(e, wrappedResponse);
        }
    }

    private RequestBuilder buildRequest() throws ActionException {
        RequestBuilder requestBuilder = requestBuilderFactory.build(getAction(), getSecurityCookie());
        requestBuilder.setCallback(createRequestCallback());

        return requestBuilder;
    }
}
