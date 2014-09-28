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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.client.DispatchCall;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.rest.client.interceptor.RestInterceptedAsyncCallback;
import com.gwtplatform.dispatch.rest.client.interceptor.RestInterceptor;
import com.gwtplatform.dispatch.rest.client.interceptor.RestInterceptorRegistry;
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
    private final RestInterceptorRegistry interceptorRegistry;
    private final RestDispatchHooks dispatchHooks;

    public RestDispatchCall(ExceptionHandler exceptionHandler,
                            RestInterceptorRegistry interceptorRegistry,
                            SecurityCookieAccessor securityCookieAccessor,
                            RestRequestBuilderFactory requestBuilderFactory,
                            RestResponseDeserializer restResponseDeserializer,
                            RestDispatchHooks dispatchHooks,
                            A action,
                            AsyncCallback<R> callback) {
        super(exceptionHandler, securityCookieAccessor, action, callback);

        this.requestBuilderFactory = requestBuilderFactory;
        this.restResponseDeserializer = restResponseDeserializer;
        this.interceptorRegistry = interceptorRegistry;
        this.dispatchHooks = dispatchHooks;
    }

    @Override
    public DispatchRequest execute() {
        final A action = getAction();
        dispatchHooks.onExecute(action);

        IndirectProvider<RestInterceptor> interceptorProvider =
                interceptorRegistry.find(action);

        if (interceptorProvider != null) {
            DelegatingDispatchRequest dispatchRequest = new DelegatingDispatchRequest();
            RestInterceptedAsyncCallback<A, R> delegatingCallback = new RestInterceptedAsyncCallback<A, R>(
                    this, action, getCallback(), dispatchRequest);

            interceptorProvider.get(delegatingCallback);

            return dispatchRequest;
        } else {
            return doExecute();
        }
    }

    @Override
    protected DispatchRequest doExecute() {
        try {
            RequestBuilder requestBuilder = buildRequest();

            return new GwtHttpDispatchRequest(requestBuilder.send());
        } catch (RequestException e) {
            onExecuteFailure(e);
        } catch (ActionException e) {
            onExecuteFailure(e);
        }
        return new CompletedDispatchRequest();
    }

    @Override
    protected void onExecuteSuccess(R result, Response response) {
        assignResponse(response);

        super.onExecuteSuccess(result, response);

        dispatchHooks.onSuccess(getAction(), response, result);
    }

    @Override
    protected void onExecuteFailure(Throwable caught, Response response) {
        assignResponse(response);

        super.onExecuteFailure(caught, response);

        dispatchHooks.onFailure(getAction(), response, caught);
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
