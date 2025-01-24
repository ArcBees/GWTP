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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.rest.client.RestDispatch;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;

import jakarta.inject.Inject;

/**
 * The default implementation for {@link com.gwtplatform.dispatch.rest.client.RestDispatch}.
 */
public class DefaultRestDispatch implements RestDispatch {
    private final DispatchCallFactory callFactory;

    @Inject
    protected DefaultRestDispatch(
            DispatchCallFactory callFactory) {
        this.callFactory = callFactory;
    }

    @Override
    public <A extends RestAction<R>, R> DispatchRequest execute(A action, AsyncCallback<R> callback) {
        RestDispatchCall<A, R> call = callFactory.create(action, callback);

        return call.execute();
    }
}
