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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.rest.client.RestCallback;
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
public class RestDispatchCall<A extends RestAction<R>, R> {
    private final SecurityCookieAccessor securityCookieAccessor;
    private final RequestBuilderFactory requestBuilderFactory;
    private final CookieManager cookieManager;
    private final A action;
    private final RestRequestCallback<A, R> callback;

    public RestDispatchCall(
            SecurityCookieAccessor securityCookieAccessor,
            RequestBuilderFactory requestBuilderFactory,
            CookieManager cookieManager,
            ResponseDeserializer responseDeserializer,
            A action,
            RestCallback<R> callback) {
        this.securityCookieAccessor = securityCookieAccessor;
        this.requestBuilderFactory = requestBuilderFactory;
        this.cookieManager = cookieManager;
        this.action = action;
        this.callback = new RestRequestCallback<>(responseDeserializer, action, callback);
    }

    /**
     * Execution entry point. Call this method to execute the {@link RestAction action} wrapped by this instance.
     *
     * @return a {@link DispatchRequest} object.
     */
    public DispatchRequest execute() {
        try {
            RequestBuilder requestBuilder = createRequest();
            cookieManager.saveCookiesFromAction(action);

            return new GwtHttpDispatchRequest(requestBuilder.send());
        } catch (RequestException | ActionException e) {
            callback.onFailure(e);
        }
        return new CompletedDispatchRequest();
    }

    private RequestBuilder createRequest() throws ActionException {
        String securityToken = securityCookieAccessor.getCookieContent();
        RequestBuilder requestBuilder = requestBuilderFactory.build(action, securityToken);
        requestBuilder.setCallback(callback);

        return requestBuilder;
    }
}
