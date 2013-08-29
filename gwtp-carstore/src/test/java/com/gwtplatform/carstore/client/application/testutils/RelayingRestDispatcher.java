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

package com.gwtplatform.carstore.client.application.testutils;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.TypeLiteral;
import com.gwtplatform.dispatch.rpc.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.rpc.shared.DispatchRequest;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;

/**
 * Class used to replace a real implementation of the @{link RestDispatch}. When executing
 * a request for an action, a predefined result will be sent back immediately.
 * <p/>
 * To assign a result to an action, simply do:
 * <code>dispatcher.when({@link RestAction}).willReturn(<b>result</b>);</code>
 */
public class RelayingRestDispatcher implements RestDispatch {
    private Map<TypeLiteral<? extends RestAction<?>>, Object> registry =
            new HashMap<TypeLiteral<? extends RestAction<?>>, Object>();

    private TypeLiteral<? extends RestAction<?>> currentAction = null;

    /**
     * This method must be used at least once before being able to use relaying
     * dispatcher. It will create an entry inside the registry and wait that the
     * use assign a result to it.
     *
     * @param <A>    The {@link com.gwtplatform.dispatch.rest.shared.RestAction} type.
     * @param action The class definition of the {@link com.gwtplatform.dispatch.rest.shared.RestAction}.
     * @return {@link RelayingRestDispatcher} instance.
     */
    public <A extends RestAction<?>> RelayingRestDispatcher given(TypeLiteral<A> action) {
        registry.put(action, null);

        currentAction = action;

        return this;
    }

    /**
     * Once you've called at least one time {@link #given(Class)}, then calling
     * this function will assign a <b>result</b> to the last {@link com.gwtplatform.dispatch.rest.shared.RestAction} you
     * assigned.
     *
     * @param <R>    The result type.
     * @param result the result to add inside the registry.
     */
    public <R> void willReturn(R result) {
        registry.put(currentAction, result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends RestAction<R>, R> DispatchRequest execute(A action, AsyncCallback<R> callback) {
        assert action instanceof ActionImpl;

        callback.onSuccess((R) registry.get(((ActionImpl) action).getTypeLiteral()));

        return new CompletedDispatchRequest();
    }
}
