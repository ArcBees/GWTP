/**
 * Copyright 2010 ArcBees Inc.
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

package com.gwtplatform.dispatch.server;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.ServiceException;

/**
 * ExecutionContext instances are passed to {@link ActionHandler}s, and allows
 * them to execute sub-actions. These actions can be automatically rolled back
 * if any part of the action handler fails.
 * 
 * @author David Peterson
 */
public interface ExecutionContext {

  /**
   * Executes an action in the current context. If the surrounding execution
   * fails, the action will be rolled back.
   * 
   * @param <A> The {@link Action} type.
   * @param <R> The {@link Result} type.
   * @param action The {@link Action}.
   * @return The {@link Result}.
   * @throws ActionException if the action execution failed.
   * @throws ServiceException if the execution failed due to a service error.
   */
  <A extends Action<R>, R extends Result> R execute(A action)
      throws ActionException, ServiceException;

  /**
   * Undoes an action in the current context. If the surrounding execution
   * fails, the action will be rolled back.
   * 
   * @param <A> The {@link Action} type.
   * @param <R> The {@link Result} type.
   * @param action The {@link Action}.
   * @throws ActionException if the action execution failed.
   * @throws ServiceException if the execution failed due to a service error.
   */
  <A extends Action<R>, R extends Result> void undo(A action, R result)
      throws ActionException, ServiceException;
}
