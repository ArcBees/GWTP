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

package com.gwtplatform.dispatch.client.rest;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.client.rest.actionhandler.ClientRestActionHandler;
import com.gwtplatform.dispatch.client.rest.actionhandler.ExecuteCommand;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.rest.RestAction;

class DelegatingAsyncCallback<A extends RestAction<R>, R> implements AsyncCallback<ClientRestActionHandler<?, ?>>,
                                                                     ExecuteCommand<A, R> {
    private final RestDispatchAsync restDispatchAsync;
    private final DelegatingDispatchRequest dispatchRequest;
    private final A action;
    private final AsyncCallback<R> callback;
    private final String securityCookie;

    DelegatingAsyncCallback(RestDispatchAsync restDispatchAsync,
                            A action,
                            AsyncCallback<R> callback,
                            DelegatingDispatchRequest dispatchRequest,
                            String securityCookie) {
        this.restDispatchAsync = restDispatchAsync;
        this.dispatchRequest = dispatchRequest;
        this.action = action;
        this.callback = callback;
        this.securityCookie = securityCookie;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(ClientRestActionHandler<?, ?> clientActionHandler) {
        if (clientActionHandler.getActionType() != action.getClass()) {
            delegateFailure(clientActionHandler);
            return;
        }

        if (dispatchRequest.isPending()) {
            delegateExecute((ClientRestActionHandler<A, R>) clientActionHandler);
        }
    }

    @Override
    public void onFailure(Throwable caught) {
        dispatchRequest.cancel();
        callback.onFailure(caught);
    }

    @Override
    public DispatchRequest execute(A action, AsyncCallback<R> resultCallback) {
        if (dispatchRequest.isPending()) {
            return restDispatchAsync.doExecute(securityCookie, action, resultCallback);
        } else {
            return null;
        }
    }

    private void delegateFailure(ClientRestActionHandler<?, ?> clientActionHandler) {
        dispatchRequest.cancel();
//        callback.onFailure(
//                new ClientRestActionHandlerMismatchException(action.getClass(), clientActionHandler.getActionType()));
    }

    private void delegateExecute(ClientRestActionHandler<A, R> clientActionHandler) {
        dispatchRequest.setDelegate(clientActionHandler.execute(action, callback, this));
    }
}
