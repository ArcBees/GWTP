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
import com.gwtplatform.dispatch.client.interceptor.ExecuteCommand;
import com.gwtplatform.dispatch.client.interceptor.Interceptor;
import com.gwtplatform.dispatch.client.interceptor.InterceptorMismatchException;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 * {@code AsyncCallback} implementation wrapping another {@link AsyncCallback} object used by a
 * {@link com.gwtplatform.dispatch.client.interceptor.Interceptor} implementations to delegate the
 * execution result.
 *
 * @param <A> the {@link TypedAction} type.
 * @param <R> the result type for this action.
 * @param <T> the interceptor type used.
 */
public abstract class DelegatingAsyncCallback<A extends TypedAction<R>, R, T extends Interceptor> implements
        AsyncCallback<T>, ExecuteCommand<A, R> {
    private final DispatchCall dispatchCall;
    private final A action;
    private final AsyncCallback<R> callback;
    private final DelegatingDispatchRequest dispatchRequest;

    public DelegatingAsyncCallback(DispatchCall dispatchCall,
                                   A action,
                                   AsyncCallback<R> callback,
                                   DelegatingDispatchRequest dispatchRequest) {
        this.dispatchCall = dispatchCall;
        this.action = action;
        this.callback = callback;
        this.dispatchRequest = dispatchRequest;
    }

    @Override
    public void onSuccess(T interceptor) {
        if (!interceptor.canExecute(getAction())) {
            delegateFailure(interceptor);
        } else if (getDispatchRequest().isPending()) {
            delegateExecute(interceptor);
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
            return dispatchCall.doExecute();
        } else {
            return null;
        }
    }

    protected void delegateFailure(Interceptor interceptor) {
        InterceptorMismatchException exception =
                new InterceptorMismatchException(action.getClass(), interceptor.getActionType());

        onFailure(exception);
    }

    @SuppressWarnings("unchecked")
    protected void delegateExecute(Interceptor interceptor) {
        dispatchRequest.setDelegate(interceptor.execute(action, callback, this));
    }

    public DelegatingDispatchRequest getDispatchRequest() {
        return dispatchRequest;
    }

    public TypedAction getAction() {
        return action;
    }
}
