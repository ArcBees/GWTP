/*
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

package com.gwtplatform.dispatch.rest.client.core;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.rest.client.RestCallback;
import com.gwtplatform.dispatch.rest.client.RestDispatchHooks;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * A class representing an execute call to be sent to the server over HTTP.
 *
 * @param <A> the {@link RestAction} type.
 * @param <R> the result type for this action.
 */
public class RestDispatchCall<A extends RestAction<R>, R>  {
    private final DispatchCallFactory dispatchCallFactory;
    private final ExceptionHandler exceptionHandler;
    private final SecurityCookieAccessor securityCookieAccessor;
    private final RequestBuilderFactory requestBuilderFactory;
    private final CookieManager cookieManager;
    private final ResponseDeserializer responseDeserializer;
    private final RestDispatchHooks dispatchHooks;
    private final A action;
    private final RestCallback<R> callback;

    private boolean intercepted;

    // TODO: Too many dependencies, split the logic
    public RestDispatchCall(
            DispatchCallFactory dispatchCallFactory,
            ExceptionHandler exceptionHandler,
            SecurityCookieAccessor securityCookieAccessor,
            RequestBuilderFactory requestBuilderFactory,
            CookieManager cookieManager,
            ResponseDeserializer responseDeserializer,
            RestDispatchHooks dispatchHooks,
            A action,
            RestCallback<R> callback) {
        this.dispatchCallFactory = dispatchCallFactory;
        this.exceptionHandler = exceptionHandler;
        this.securityCookieAccessor = securityCookieAccessor;
        this.requestBuilderFactory = requestBuilderFactory;
        this.cookieManager = cookieManager;
        this.responseDeserializer = responseDeserializer;
        this.dispatchHooks = dispatchHooks;
        this.action = action;
        this.callback = callback;
    }

    /**
     * Execution entry point. Call this method to execute the {@link RestAction action} wrapped by this instance.
     *
     * @return a {@link DispatchRequest} object.
     */
    public DispatchRequest execute() {
        dispatchHooks.onExecute(action);

        // Execute the request as given
        return processCall();
    }

    /**
     * Override this method to perform additional work when the action execution succeeded.
     *
     * @param result the action result.
     * @param response the action {@link Response}.
     */
    public void onExecuteSuccess(R result, Response response) {
        assignResponse(response);

        callback.onSuccess(result, response);

        dispatchHooks.onSuccess(action, response, result);
    }

    /**
     * Override this method to perform additional work when the action execution failed.
     *
     * @param caught the caught {@link Throwable}.
     * @param response the failure {@link Response}.
     */
    public void onExecuteFailure(Throwable caught, Response response) {
        assignResponse(response);

        if (shouldHandleFailure(caught)) {
            callback.onFailure(caught, response);
        }

        dispatchHooks.onFailure(action, response, caught);
    }

    public void setIntercepted(boolean intercepted) {
        if (this.intercepted && !intercepted) {
            throw new IllegalStateException("Can not overwrite the intercepted state of a DispatchCall.");
        }

        this.intercepted = intercepted;
    }

    public boolean isIntercepted() {
        return intercepted;
    }

    /**
     * Direct execution of a dispatch call without intercepting. Implementations must override this method to perform
     * additional work when {@link #execute()} is called.
     *
     * @return a {@link DispatchRequest} object.
     */
    protected DispatchRequest processCall() {
        try {
            RequestBuilder requestBuilder = buildRequest();
            cookieManager.saveCookiesFromAction(action);

            return new GwtHttpDispatchRequest(requestBuilder.send());
        } catch (RequestException | ActionException e) {
            shouldHandleFailure(e);
        }
        return new CompletedDispatchRequest();
    }

    /**
     * Returns the bound {@link ExceptionHandler}.
     *
     * @return the bound {@link ExceptionHandler}.
     */
    protected ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Returns the bound {@link SecurityCookieAccessor}.
     *
     * @return the bound {@link SecurityCookieAccessor}.
     */
    protected SecurityCookieAccessor getSecurityCookieAccessor() {
        return securityCookieAccessor;
    }

    /**
     * Returns the current security cookie as returned by the bound {@link SecurityCookieAccessor}.
     *
     * @return the current security cookie.
     */
    protected String getSecurityCookie() {
        return securityCookieAccessor.getCookieContent();
    }

    /**
     * Override this method to perform additional work when the action execution failed.
     *
     * @param caught the caught {@link Throwable}.
     */
    public boolean shouldHandleFailure(Throwable caught) {
        return exceptionHandler.onFailure(caught) != ExceptionHandler.Status.STOP;
    }

    private void assignResponse(Response response) {
        callback.always(response);
    }

    private RequestCallback createRequestCallback() {
        return new RequestCallback() {
            private Response response;

            @Override
            public void onResponseReceived(Request request, Response response) {
                this.response = response;
                RestDispatchCall.this.onResponseReceived(response);
            }

            @Override
            public void onError(Request request, Throwable exception) {
                onExecuteFailure(exception, response);
            }
        };
    }

    private void onResponseReceived(Response response) {
        Response wrappedResponse = new ResponseWrapper(response);

        try {
            R result = responseDeserializer.deserialize(action, wrappedResponse);

            onExecuteSuccess(result, wrappedResponse);
        } catch (ActionException e) {
            onExecuteFailure(e, wrappedResponse);
        }
    }

    private RequestBuilder buildRequest() throws ActionException {
        RequestBuilder requestBuilder = requestBuilderFactory.build(action, getSecurityCookie());
        requestBuilder.setCallback(createRequestCallback());

        return requestBuilder;
    }
}
