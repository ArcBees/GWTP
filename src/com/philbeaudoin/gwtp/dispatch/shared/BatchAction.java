/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.dispatch.shared;

/**
 * This provides a simple way to send multiple actions to be executed in
 * sequence. If any fail, the rules for the {@link OnException} value provided
 * in the constructor determine the outcome.
 * 
 * @author David Peterson
 */
public abstract class BatchAction implements Action<BatchResult> {

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
