/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.filter.impl;

import com.google.gwt.http.client.Response;
import com.gwtplatform.dispatch.client.ExecuteCommand;
import com.gwtplatform.dispatch.rest.client.RestCallback;
import com.gwtplatform.dispatch.rest.client.context.RestContext;
import com.gwtplatform.dispatch.rest.client.filter.RestFilter;
import com.gwtplatform.dispatch.rest.client.filter.RestFilterChain;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * Add this filter to your {@link com.gwtplatform.dispatch.rest.client.filter.RestFilterRegistry RestFilterRegistry} to
 * handle all failed REST requests.
 *
 * @see #handleFailure(Throwable, Response)
 */
public class ExceptionHandlerFilter implements RestFilter {
    public enum Status {
        CONTINUE,
        STOP
    }

    private final Status defaultStatus;
    private final RestContext context;

    public ExceptionHandlerFilter() {
        this(Status.CONTINUE);
    }

    public ExceptionHandlerFilter(Status defaultStatus) {
        this.defaultStatus = defaultStatus;
        this.context = new RestContext.Builder()
                .path("")
                .transcendent(true)
                .anyHttpMethod(true)
                .anyQueryCount(true)
                .build();
    }

    @Override
    public RestContext getRestContext() {
        return context;
    }

    @Override
    public final <R> DispatchRequest filter(
            RestAction<R> action,
            RestCallback<R> callback,
            ExecuteCommand<RestAction<R>, RestCallback<R>> command,
            RestFilterChain chain) {
        RestCallback<R> callbackWrapper = new RestCallback<R>() {
            @Override
            public void onFailure(Throwable throwable, Response response) {
                handleFailure(throwable, response, callback);
            }

            @Override
            public void onSuccess(R result, Response response) {
                callback.onSuccess(result, response);
            }

            @Override
            public void always(Response response) {
                callback.always(response);
            }
        };

        return chain.doFilter(action, callbackWrapper, command);
    }

    private <R> void handleFailure(Throwable throwable, Response response, RestCallback<R> callback) {
        Status status = handleFailure(throwable, response);

        if (status == Status.CONTINUE) {
            callback.onFailure(throwable, response);
        }
    }

    /**
     * Override this method to perform additional work upon failure.
     *
     * @param throwable the exception associated with the request failure.
     * @param response the response associated with the failure. This may be {@code null} if the request never reached
     * the server.
     */
    protected Status handleFailure(Throwable throwable, Response response) {
        return defaultStatus;
    }
}
