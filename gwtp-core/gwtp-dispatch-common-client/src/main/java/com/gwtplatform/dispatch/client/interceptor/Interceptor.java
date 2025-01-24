/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.client.interceptor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 * Interceptor implementations base interface.
 *
 * @param <A> action class.
 * @param <R> result class.
 */
public interface Interceptor<A, R> {
    /**
     * Handles the specified action.
     * <p>
     * If the interceptor makes asynchronous calls, it is recommended that you confirm that this request has not been
     * cancelled after returning by calling {@link com.gwtplatform.dispatch.client.DelegatingDispatchRequest#isPending()
     * DelegatingDispatchRequest#isPending()} against the request parameter.
     *
     * @param action The action to execute.
     * @param resultCallback The callback to use to communicate the result of the action. Unless the request is
     * cancelled, you must invoke {@link AsyncCallback#onSuccess(Object)} on this callback once you have obtained the
     * result. If any failure occurs call {@link AsyncCallback#onFailure(Throwable)}.
     * @param executeCommand Call {@link ExecuteCommand#execute(Object, AsyncCallback)} on this object to send the
     * action over to the server. As a parameter you can pass {@code resultCallback} or your custom {@link
     * AsyncCallback} if you want to process the result.
     *
     * @return A {@link DispatchRequest} object. Never return {@code null}, instead return a new {@link
     * com.gwtplatform.dispatch.client.CompletedDispatchRequest CompletedDispatchRequest} if you executed, cancelled or
     * ignored the action.
     */
    DispatchRequest execute(A action, AsyncCallback<R> resultCallback, ExecuteCommand<A, R> executeCommand);

    /**
     * @return The type of action supported by this interceptor.
     */
    Class<A> getActionType();

    /**
     * Ensures this intercepted call can be executed.
     *
     * @param action the action to test against.
     *
     * @return true if this action can be executed, false if not.
     */
    boolean canExecute(TypedAction<?> action);
}
