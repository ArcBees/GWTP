package com.philbeaudoin.gwtp.dispatch.server;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * This contains both the original {@link Action} and the {@link Result} of that
 * action. It also indicates if this action was executed ({@link ActionHandler#execute(Action, ExecutionContext)})
 * or undoed ({@link ActionHandler#undo(Action, Result, ExecutionContext)}). 
 * 
 * @author David Peterson
 * 
 * @param <A>
 *            The action type.
 * @param <R>
 *            The result type.
 */
public class ActionResult<A extends Action<R>, R extends Result> {
  private final A action;

  private final R result;

  private final boolean executed;

  /**
   * Creates a new action/result pair. The {@code executed} field indicates if this action 
   * was executed via {@link ActionHandler#execute(Action, ExecutionContext)}
   * or undoed via {@link ActionHandler#undo(Action, Result, ExecutionContext)}.
   * 
   * @param action  The {@link Action}.
   * @param result  The {@link Result}.
   * @param executed {@code true} if the action was executed, {@code false} if it was undoed.
   */
  public ActionResult( A action, R result, boolean executed ) {
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
   * Checks wheter this action 
   * was executed via {@link ActionHandler#execute(Action, ExecutionContext)}
   * or undoed via {@link ActionHandler#undo(Action, Result, ExecutionContext)}.
   * 
   * @return {@code true} if the action was executed, {@code false} if it was undoed.
   */
  public boolean isExecuted() {
    return executed;
  }
  
}
