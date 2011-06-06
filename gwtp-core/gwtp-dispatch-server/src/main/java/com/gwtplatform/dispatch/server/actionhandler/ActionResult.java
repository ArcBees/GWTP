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

package com.gwtplatform.dispatch.server.actionhandler;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * This contains both the original {@link Action} and the {@link Result} of that
 * action. It also indicates if this action was executed (
 * {@link ActionHandler#execute(Action, com.gwtplatform.dispatch.server.ExecutionContext)}) or undone (
 * {@link ActionHandler#undo(Action, Result, com.gwtplatform.dispatch.server.ExecutionContext)}).
 *
 * @author David Peterson
 *
 * @param <A> The action type.
 * @param <R> The result type.
 */
public class ActionResult<A extends Action<R>, R extends Result> {
  private final A action;

  private final boolean executed;

  private final R result;

  /**
   * Creates a new action/result pair. The {@code executed} field indicates if
   * this action was executed via
   * {@link ActionHandler#execute(Action, com.gwtplatform.dispatch.server.ExecutionContext)} or undone via
   * {@link ActionHandler#undo(Action, Result, com.gwtplatform.dispatch.server.ExecutionContext)}.
   *
   * @param action The {@link Action}.
   * @param result The {@link Result}.
   * @param executed {@code true} if the action was executed, {@code false} if
   *          it was undoed.
   */
  public ActionResult(A action, R result, boolean executed) {
    this.action = action;
    this.result = result;
    this.executed = executed;
  }

  public A getAction() {
    return action;
  }

  public R getResult() {
    return result;
  }

  /**
   * Checks wheter this action was executed via
   * {@link ActionHandler#execute(Action, com.gwtplatform.dispatch.server.ExecutionContext)} or undone via
   * {@link ActionHandler#undo(Action, Result, com.gwtplatform.dispatch.server.ExecutionContext)}.
   *
   * @return {@code true} if the action was executed, {@code false} if it was
   *         undoed.
   */
  public boolean isExecuted() {
    return executed;
  }

}
