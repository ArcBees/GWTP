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

import javax.inject.Inject;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.rest.client.interceptor.RestInterceptorRegistry;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * The default implementation for {@link RestDispatchCallFactory}.
 */
public class DefaultRestDispatchCallFactory implements RestDispatchCallFactory {
    private final ExceptionHandler exceptionHandler;
    private final RestInterceptorRegistry interceptorRegistry;
    private final SecurityCookieAccessor securityCookieAccessor;
    private final RestRequestBuilderFactory requestBuilderFactory;
    private final RestResponseDeserializer restResponseDeserializer;
    private final RestDispatchHooks dispatchHooks;

    @Inject
    DefaultRestDispatchCallFactory(@RestBinding ExceptionHandler exceptionHandler,
                                   @RestBinding SecurityCookieAccessor securityCookieAccessor,
                                   RestInterceptorRegistry interceptorRegistry,
                                   RestRequestBuilderFactory requestBuilderFactory,
                                   RestResponseDeserializer restResponseDeserializer,
                                   RestDispatchHooks dispatchHooks) {
        this.exceptionHandler = exceptionHandler;
        this.interceptorRegistry = interceptorRegistry;
        this.securityCookieAccessor = securityCookieAccessor;
        this.requestBuilderFactory = requestBuilderFactory;
        this.restResponseDeserializer = restResponseDeserializer;
        this.dispatchHooks = dispatchHooks;
    }

    @Override
    public <A extends RestAction<R>, R> RestDispatchCall<A, R> create(A action, AsyncCallback<R> callback) {
        return new RestDispatchCall<A, R>(exceptionHandler, interceptorRegistry, securityCookieAccessor,
                requestBuilderFactory, restResponseDeserializer, dispatchHooks, action, callback);
    }
}
