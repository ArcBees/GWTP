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

public abstract class HooksFilter implements RestFilter {
    private final RestContext context;

    protected HooksFilter() {
        this(new RestContext.Builder()
                .path("")
                .transcendent(true)
                .anyHttpMethod(true)
                .anyQueryCount(true)
                .build());
    }

    protected HooksFilter(RestContext context) {
        this.context = context;
    }

    @Override
    public final RestContext getRestContext() {
        return context;
    }

    @Override
    public final <R> DispatchRequest filter(RestAction<R> action, RestCallback<R> callback,
            ExecuteCommand<RestAction<R>, RestCallback<R>> command, RestFilterChain chain) {
        ExecuteCommand<RestAction<R>, RestCallback<R>> commandWrapper = wrapCommand(command);

        return chain.doFilter(action, callback, commandWrapper);
    }

    private <R> ExecuteCommand<RestAction<R>, RestCallback<R>> wrapCommand(
            ExecuteCommand<RestAction<R>, RestCallback<R>> command) {
        return (newAction, newCallback) -> {
            onExecute(newAction);

            RestCallback<R> callbackWrapper = wrapCallback(newAction);

            return command.execute(newAction, callbackWrapper);
        };
    }

    private <R> RestCallback<R> wrapCallback(final RestAction<R> newAction) {
        return new RestCallback<R>() {
            @Override
            public void always(Response response) {
                onResponse(newAction, response);
            }

            @Override
            public void onSuccess(R result, Response response) {
                HooksFilter.this.onSuccess(newAction, response, result);
            }

            @Override
            public void onFailure(Throwable throwable, Response response) {
                HooksFilter.this.onFailure(newAction, throwable, response);
            }
        };
    }

    protected <R> void onExecute(RestAction<R> action) {
    }

    protected <R> void onResponse(RestAction<R> action, Response response) {
    }

    protected <R> void onSuccess(RestAction<R> action, Response response, R result) {
    }

    protected <R> void onFailure(RestAction<R> action, Throwable throwable, Response response) {
    }
}
