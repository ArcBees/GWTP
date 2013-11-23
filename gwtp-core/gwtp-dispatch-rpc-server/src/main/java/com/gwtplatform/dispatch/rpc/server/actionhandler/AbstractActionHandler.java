/**
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

package com.gwtplatform.dispatch.rpc.server.actionhandler;

import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;

/**
 * Simple abstract super-class for {@link ActionHandler} implementations that
 * forces the {@link Action} class to be passed in as a constructor to the
 * handler. It's arguable if this is any simpler than just implementing the
 * {@link ActionHandler} and its {@link #getActionType()} directly.
 *
 * @param <A> The {@link Action} type.
 * @param <R> The {@link com.gwtplatform.dispatch.rpc.shared.Result} type.
 */
public abstract class AbstractActionHandler<A extends Action<R>, R extends Result>
        implements ActionHandler<A, R> {

    private final Class<A> actionType;

    public AbstractActionHandler(Class<A> actionType) {
        this.actionType = actionType;
    }

    public Class<A> getActionType() {
        return actionType;
    }

}
