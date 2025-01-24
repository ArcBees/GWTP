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

package com.gwtplatform.dispatch.rpc.server;

import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.rpc.shared.ServiceException;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * The base class of the synchronous dispatcher service with an arbitrary action type. The server-side implementation is
 * {@code com.gwtplatform.dispatch.rpc.server.guice.DispatchImpl} and the async client-side version is
 * {@link com.gwtplatform.dispatch.rpc.shared.DispatchAsync}.
 * <p>
 * This class is closely related to {@link com.gwtplatform.dispatch.rpc.shared.DispatchService}. In fact, this class
 * wouldn't be needed, but we use it to workaround a GWT limitation described in
 * {@link com.gwtplatform.dispatch.rpc.shared.DispatchAsync}.
 */
public interface Dispatch {
    /**
     * Executes the specified action and returns the appropriate result.
     *
     * @param <A>    The {@link Action} type.
     * @param <R>    The {@link Result} type.
     * @param action The {@link Action}.
     * @return The action's result.
     * @throws ActionException  if the action execution failed.
     * @throws ServiceException if the execution failed due to a service error.
     */
    <A extends Action<R>, R extends Result> R execute(A action) throws ActionException, ServiceException;

    /**
     * Undoes a previously executed action.
     *
     * @param <A>    The {@link Action} type.
     * @param <R>    The {@link Result} type.
     * @param action The {@link Action} to undo.
     * @param result The result obtained when the action was previously executed.
     * @throws ActionException  if undoing the action failed.
     * @throws ServiceException if the execution failed due to a service error.
     */
    <A extends Action<R>, R extends Result> void undo(A action, R result) throws ActionException, ServiceException;
}
