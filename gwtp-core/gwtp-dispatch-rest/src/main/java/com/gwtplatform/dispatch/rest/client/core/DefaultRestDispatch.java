/*
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

package com.gwtplatform.dispatch.rest.client.core;

import com.gwtplatform.dispatch.rest.client.RestCallback;
import com.gwtplatform.dispatch.rest.client.RestDispatch;
import com.gwtplatform.dispatch.rest.client.filter.RestFilterChain;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

/**
 * The default implementation for {@link RestDispatch}.
 */
public class DefaultRestDispatch implements RestDispatch {
    private final Provider<RestFilterChain> restFilterChainProvider;
    private final DispatchCallFactory callFactory;

    @Inject
    protected DefaultRestDispatch(
            Provider<RestFilterChain> restFilterChainProvider,
            DispatchCallFactory callFactory) {
        this.restFilterChainProvider = restFilterChainProvider;
        this.callFactory = callFactory;
    }

    @Override
    public <A extends RestAction<R>, R> DispatchRequest execute(A action, RestCallback<R> callback) {
        RestFilterChain filterChain = restFilterChainProvider.get();
        return filterChain.doFilter(action, callback, this::doExecute);
    }

    private <A extends RestAction<R>, R> DispatchRequest doExecute(A action, RestCallback<R> resultCallback) {
        RestDispatchCall<?, ?> call = callFactory.create(action, resultCallback);
        return call.execute();
    }
}
