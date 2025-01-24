/*
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

import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Instances of this interface will handle specific types of {@link Action} classes.
 * <p>
 * <b>Important!</b> Your action handlers must be thread safe since they will be bound as singletons. For details,
 * see <a href="http://code.google.com/p/google-guice/wiki/Scopes#Scopes_and_Concurrency">
 * http://code.google.com/p/google-guice/wiki/Scopes#Scopes_and_Concurrency</a>.
 *
 * @param <A> The type of the action extending {@link Action}.
 * @param <R> The type of the result extending {@link Result}.
 */
public interface ActionHandler<A extends Action<R>, R extends Result> {
    /**
     * Handles the specified action. If you want to build a compound action that can rollback automatically upon
     * failure, call {@link ExecutionContext#execute(Action)}. See
     * <a href="http://code.google.com/p/gwt-dispatch/wiki/CompoundActions">here</a> for details.
     *
     * @param action  The action.
     * @param context The {@link ExecutionContext}.
     * @return The {@link Result}.
     * @throws ActionException if there is a problem performing the specified action.
     */
    R execute(A action, ExecutionContext context) throws ActionException;

    /**
     * @return The type of {@link Action} supported by this handler.
     */
    Class<A> getActionType();

    /**
     * Undoes the specified action. If you want to build a compound action that can rollback automatically upon failure,
     * call {@link ExecutionContext#undo(Action, Result)}. See
     * <a href="http://code.google.com/p/gwt-dispatch/wiki/CompoundActions">here</a> for details.
     *
     * @param action  The action.
     * @param result  The result of the action.
     * @param context The {@link ExecutionContext}.
     * @throws ActionException if there is a problem performing the specified action.
     */
    void undo(A action, R result, ExecutionContext context) throws ActionException;
}
