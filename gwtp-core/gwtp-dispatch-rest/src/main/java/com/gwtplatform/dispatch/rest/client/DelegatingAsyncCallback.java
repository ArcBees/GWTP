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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerMismatchException;
import com.gwtplatform.dispatch.client.actionhandler.ExecuteCommand;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;

class DelegatingAsyncCallback<A extends RestAction<R>, R> implements AsyncCallback<ClientActionHandler<?, ?>>,
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
    public void onSuccess(ClientActionHandler<?, ?> clientActionHandler) {
        if (clientActionHandler.getActionType() != action.getClass()) {
            delegateFailure(clientActionHandler);
            return;
        }

        if (dispatchRequest.isPending()) {
            delegateExecute((ClientActionHandler<A, R>) clientActionHandler);
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

    @SuppressWarnings("unchecked")
    private void delegateFailure(ClientActionHandler<?, ?> clientActionHandler) {
        dispatchRequest.cancel();

        Class<? extends RestAction<?>> requestedActionClass = (Class<? extends RestAction<?>>) action.getClass();
        Class<?> supportedActionType = clientActionHandler.getActionType();
        callback.onFailure(new ClientActionHandlerMismatchException(requestedActionClass, supportedActionType));
    }

    private void delegateExecute(ClientActionHandler<A, R> clientActionHandler) {
        dispatchRequest.setDelegate(clientActionHandler.execute(action, callback, this));
    }
}
