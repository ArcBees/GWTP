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

package com.gwtplatform.dispatch.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerMismatchException;
import com.gwtplatform.dispatch.client.actionhandler.ExecuteCommand;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 * {@code AsyncCallback} implementation wrapping another {@link AsyncCallback} object used by a
 * {@link ClientActionHandler} to delegate the execution result.
 *
 * @param <A> the {@link TypedAction} type.
 * @param <R> the result type for this action.
 */
@Deprecated
public class OldDelegatingAsyncCallback<A extends TypedAction<R>, R> implements AsyncCallback<
        ClientActionHandler<?, ?>>, ExecuteCommand<A, R> {
    private final DispatchCall dispatchCall;
    private final A action;
    private final AsyncCallback<R> callback;
    private final DelegatingDispatchRequest dispatchRequest;

    public OldDelegatingAsyncCallback(DispatchCall dispatchCall,
                                      A action,
                                      AsyncCallback<R> callback,
                                      DelegatingDispatchRequest dispatchRequest) {
        this.dispatchCall = dispatchCall;
        this.action = action;
        this.callback = callback;
        this.dispatchRequest = dispatchRequest;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(ClientActionHandler<?, ?> clientActionHandler) {
        if (clientActionHandler.getActionType() != action.getClass()) {
            delegateFailure(clientActionHandler);
        } else if (dispatchRequest.isPending()) {
            delegateExecute((ClientActionHandler<A, R>) clientActionHandler);
        }
    }

    @Override
    public void onFailure(Throwable caught) {
        dispatchRequest.cancel();

        dispatchCall.onExecuteFailure(caught);
    }

    @Override
    public DispatchRequest execute(A action, AsyncCallback<R> resultCallback) {
        if (dispatchRequest.isPending()) {
            return dispatchCall.processCall();
        } else {
            return null;
        }
    }

    private void delegateFailure(ClientActionHandler<?, ?> clientActionHandler) {
        ClientActionHandlerMismatchException exception =
                new ClientActionHandlerMismatchException(action.getClass(), clientActionHandler.getActionType());

        onFailure(exception);
    }

    private void delegateExecute(ClientActionHandler<A, R> clientActionHandler) {
        dispatchRequest.setDelegate(clientActionHandler.execute(action, callback, this));
    }
}
