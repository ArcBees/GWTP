/**
 * Copyright 2010 Philippe Beaudoin
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

package com.philbeaudoin.gwtp.dispatch.server;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.philbeaudoin.gwtp.dispatch.server.actionHandler.ActionHandler;
import com.philbeaudoin.gwtp.dispatch.server.actionHandler.ActionHandlerRegistry;
import com.philbeaudoin.gwtp.dispatch.server.actionHandler.ActionResult;
import com.philbeaudoin.gwtp.dispatch.server.sessionValidator.ActionValidator;
import com.philbeaudoin.gwtp.dispatch.server.sessionValidator.SessionValidatorRegistry;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;
import com.philbeaudoin.gwtp.dispatch.shared.UnsupportedActionException;

/**
 * Implementation of {@link Dispatch} interface. This is the core of
 * {@link Dispatch} server implementation.
 * 
 * @author Christian Goudreau
 * @author David Peterson
 */
@Singleton
public class DispatchImpl implements Dispatch {
  private static class DefaultExecutionContext implements ExecutionContext {
    private final DispatchImpl dispatch;
    private final List<ActionResult<?, ?>> actionResults;

    private DefaultExecutionContext(DispatchImpl dispatch) {
      this.dispatch = dispatch;
      this.actionResults = new java.util.ArrayList<ActionResult<?, ?>>();
    }

    @Override
    public <A extends Action<R>, R extends Result> R execute(A action)
    throws ActionException, ServiceException {
      R result = dispatch.doExecute(action, this);
      actionResults.add(new ActionResult<A, R>(action, result, true));
      return result;
    }

    @Override
    public <A extends Action<R>, R extends Result> void undo(A action, R result)
    throws ActionException, ServiceException {
      dispatch.doExecute(action, this);
      actionResults.add(new ActionResult<A, R>(action, result, false));
    }

    /**
     * Rolls back all logged executed actions.
     * 
     * @throws ActionException
     *           If there is an action exception while rolling back.
     * @throws ServiceException
     *           If there is a low level problem while rolling back.
     */
    private void rollback() throws ActionException, ServiceException {
      DefaultExecutionContext ctx = new DefaultExecutionContext(dispatch);
      for (int i = actionResults.size() - 1; i >= 0; i--) {
        ActionResult<?, ?> actionResult = actionResults.get(i);
        rollback(actionResult, ctx);
      }
    }

    private <A extends Action<R>, R extends Result> void rollback(
        ActionResult<A, R> actionResult, ExecutionContext ctx)
    throws ActionException, ServiceException {
      if (actionResult.isExecuted())
        dispatch
        .doUndo(actionResult.getAction(), actionResult.getResult(), ctx);
      else
        dispatch.doExecute(actionResult.getAction(), ctx);
    }
  };

  private final ActionHandlerRegistry handlerRegistry;
  private final SessionValidatorRegistry sessionValidatorRegistry;

  @Inject
  public DispatchImpl(ActionHandlerRegistry handlerRegistry, SessionValidatorRegistry secureSessionValidatorRegistry) {
    this.handlerRegistry = handlerRegistry;
    this.sessionValidatorRegistry = secureSessionValidatorRegistry;
  }

  @Override
  public <A extends Action<R>, R extends Result> R execute(A action)
  throws ActionException, ServiceException {
    DefaultExecutionContext ctx = new DefaultExecutionContext(this);
    try {
      return doExecute(action, ctx);
    } catch (ActionException e) {
      ctx.rollback();
      throw e;
    } catch (ServiceException e) {
      ctx.rollback();
      throw e;
    }
  }

  @Override
  public <A extends Action<R>, R extends Result> void undo(A action, R result)
  throws ActionException, ServiceException {
    DefaultExecutionContext ctx = new DefaultExecutionContext(this);
    try {
      doUndo(action, result, ctx);
    } catch (ActionException e) {
      ctx.rollback();
      throw e;
    } catch (ServiceException e) {
      ctx.rollback();
      throw e;
    }
  }

  /**
   * Every single action will be executed by this function and validated by the
   * {@link ActionValidator}.
   * 
   * @param <A>
   *          Type of associated {@link Action} type.
   * @param <R>
   *          Type of associated {@link Result} type.
   * @param action
   *          The {@link Action} to execute
   * @param ctx
   *          The {@link ExecutionContext} associated with the {@link Action}
   * @return The {@link Result} to the client
   * @throws ActionException
   * @throws ServiceException
   */
  private <A extends Action<R>, R extends Result> R doExecute(A action,
      ExecutionContext ctx) throws ActionException, ServiceException {
    ActionHandler<A, R> handler = findHandler(action);

    ActionValidator actionValidator = findActionValidator(action);

    try {
      if (actionValidator.isValid())
        return handler.execute(action, ctx);
      else
        throw new ActionException("Insufficient rights"); // TODO This should be a more specific exception
    } catch (ActionException e) {
      throw e;
    } catch( Exception e ) {
      String newMessage = "Service exception executing action \"" + action.getClass().getSimpleName() + "\"";
      if( e.getMessage() != null )
        newMessage += ": " + e.getMessage();
      ServiceException rethrown = new ServiceException( newMessage ); 
      rethrown.initCause(e);
      throw rethrown;
    }
  }

  private <A extends Action<R>, R extends Result> void doUndo(A action,
      R result, ExecutionContext ctx) throws ActionException, ServiceException {

    ActionValidator secureSessionValidator = findActionValidator(action);

    ActionHandler<A, R> handler = findHandler(action);
    try {
      if (secureSessionValidator.isValid())
        handler.undo(action, result, ctx);
      else
        throw new ActionException("Insufficient rights"); // TODO This should be a more specific exception
    } catch (ActionException e) {
      throw e;
    } catch (Exception cause) {
      throw new ServiceException(cause);
    }
  }

  private <A extends Action<R>, R extends Result> ActionHandler<A, R> findHandler(
      A action) throws UnsupportedActionException {
    ActionHandler<A, R> handler = handlerRegistry.findHandler(action);
    if (handler == null)
      throw new UnsupportedActionException(action);

    return handler;
  }

  private <A extends Action<R>, R extends Result> ActionValidator findActionValidator(
      A action) throws UnsupportedActionException {
    ActionValidator secureSessionValidator = sessionValidatorRegistry.findSecureSessionValidator(action);

    if (secureSessionValidator == null)
      throw new UnsupportedActionException(action);

    return secureSessionValidator;
  }
}