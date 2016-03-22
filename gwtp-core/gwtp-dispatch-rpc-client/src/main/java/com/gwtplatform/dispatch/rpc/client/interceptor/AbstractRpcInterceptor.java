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

package com.gwtplatform.dispatch.rpc.client.interceptor;

import com.gwtplatform.dispatch.rpc.shared.Action;

/**
 * Simple abstract super-class for {@link RpcInterceptor} implementations that forces the action class to be passed in
 * as a constructor to the interceptor.
 *
 * @param <A> The action type.
 * @param <R> The result type.
 */
public abstract class AbstractRpcInterceptor<A, R> implements RpcInterceptor<A, R> {
    private final Class<A> actionType;

    protected AbstractRpcInterceptor(Class<A> actionType) {
        this.actionType = actionType;
    }

    @Override
    public boolean canExecute(Action<?> action) {
        return getActionType().equals(action.getClass());
    }

    @Override
    public Class<A> getActionType() {
        return actionType;
    }
}
