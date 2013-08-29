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

import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * Simple abstract super-class for {@link ClientRestActionHandler} implementations that
 * forces the {@link com.gwtplatform.dispatch.rest.shared.RestAction} class to be passed in as a constructor to the
 * handler.
 *
 * @param <A> The {@link com.gwtplatform.dispatch.rest.shared.RestAction} type.
 * @param <R> The result type.
 */
public abstract class AbstractClientRestActionHandler<A extends RestAction<R>, R>
        implements ClientRestActionHandler<A, R> {
    private final Class<A> actionType;

    protected AbstractClientRestActionHandler(Class<A> actionType) {
        this.actionType = actionType;
    }

    public Class<A> getActionType() {
        return actionType;
    }
}
