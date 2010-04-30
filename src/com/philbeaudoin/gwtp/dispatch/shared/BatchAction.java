/**
 * This provides a simple way to send multiple actions to be executed in
 * sequence. If any fail, the rules for the {@link OnException} value provided
 * in the constructor determine the outcome.
 * 
 * @author David Peterson
 */

package com.philbeaudoin.gwtp.dispatch.shared;

public abstract class BatchAction implements Action<BatchResult> {

  private static final long serialVersionUID = 9035469137825018000L;

  public enum OnException {
    /**
     * If specified, the batch will continue if an action fails. The
     * matching {@link Result} in the {@link BatchResult#getResults()} will
     * be <code>null</code>.
     */
    CONTINUE,
    /**
     * If specified, the batch will stop processing and roll back any
     * executed actions from the batch, and throw the exception.
     */
    ROLLBACK;
  }

  private OnException onException;

  private Action<?>[] actions;

  /**
   * Used for serialization only.
   */
  BatchAction() {
  }

  /**
   * Constructs a new batch action, which will attempt to execute the provided
   * list of actions in order. If there is a failure, it will follow the rules
   * specified by <code>onException</code>.
   * 
   * @param onException
   *            If there is an exception, specify the behaviour.
   * @param actions
   *            The list of actions to execute.
   */
  public BatchAction( OnException onException, Action<?>... actions ) {
    this.onException = onException;
    this.actions = actions;
  }

  /**
   * The expected behaviour if any of the sub-actions throw an exception.
   * 
   * @return The exception handling behaviour.
   */
  public OnException getOnException() {
    return onException;
  }

  /**
   * The list of actions to execute.
   * 
   * @return The actions.
   */
  public Action<?>[] getActions() {
    return actions;
  }
}
