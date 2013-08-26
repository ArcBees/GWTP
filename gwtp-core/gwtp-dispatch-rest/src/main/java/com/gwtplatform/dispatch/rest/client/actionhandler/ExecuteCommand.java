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

package com.gwtplatform.dispatch.rest.client.actionhandler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rpc.shared.DispatchRequest;

/**
 * The interface that {@link ClientRestActionHandler}s use to send the action to execute to the server over HTTP.
 *
 * @param <A> The {@link com.gwtplatform.dispatch.rest.shared.RestAction} type.
 * @param <R> The result type.
 */
public interface ExecuteCommand<A extends RestAction<R>, R> {
    /**
     * Execute an action.
     *
     * @param action         The action to execute.
     * @param resultCallback A callback that will be invoked once the action has been executed, successfully or not.
     * @return A {@link com.gwtplatform.dispatch.rpc.shared.DispatchRequest} representing the HTTP request, it
     *         should never be {@code null}.
     */
    DispatchRequest execute(A action, AsyncCallback<R> resultCallback);
}
