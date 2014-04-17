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

package com.gwtplatform.dispatch.server;

import java.util.List;

import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.server.actionhandler.ActionResult;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorInstance;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.ServiceException;
import com.gwtplatform.dispatch.shared.UnsupportedActionException;

/**
 * This is the server-side implementation of the {@link Dispatch} service with an arbitrary action type, for which the
 * client-side async service is {@link com.gwtplatform.dispatch.shared.DispatchAsync}.
 * <p/>
 * This class is closely related to {@link com.gwtplatform.dispatch.server.guice.DispatchServiceImpl}.
 * In fact, this class wouldn't be needed, but we use it
 * to workaround a GWT limitation described in {@link com.gwtplatform.dispatch.shared.DispatchAsync}.
 *
 * @see com.gwtplatform.dispatch.shared.DispatchAsync
 * @see com.gwtplatform.dispatch.server.Dispatch
 * @see com.gwtplatform.dispatch.server.guice.DispatchImpl
 * @see com.gwtplatform.dispatch.shared.DispatchService
 * @see com.gwtplatform.dispatch.shared.DispatchServiceAsync
 * @see com.gwtplatform.dispatch.server.guice.DispatchServiceImpl
 *
 * @deprecated Please use {@link com.gwtplatform.dispatch.rpc.server.AbstractDispatchImpl}.
 */
@Deprecated
public abstract class AbstractDispatchImpl implements Dispatch {

    private static class DefaultExecutionContext implements ExecutionContext {

        private final List<ActionResult<?, ?>> actionResults;
        private final AbstractDispatchImpl dispatch;

        private DefaultExecutionContext(AbstractDispatchImpl dispatch) {
            this.dispatch = dispatch;
            this.actionResults = new java.util.ArrayList<ActionResult<?, ?>>();
        }

        @Override
        public <A extends Action<R>, R extends Result> R execute(A action) throws ActionException, ServiceException {
            R result = dispatch.doExecute(action, this);
            actionResults.add(new ActionResult<A, R>(action, result, true));
            return result;
        }

        @Override
        public <A extends Action<R>, R extends Result> void undo(A action, R result) throws ActionException,
                ServiceException {
            dispatch.doExecute(action, this);
            actionResults.add(new ActionResult<A, R>(action, result, false));
        }

        /**
         * Rolls back all logged executed actions.
         *
         * @throws com.gwtplatform.dispatch.shared.ActionException  If there is an action exception while rolling back.
         * @throws com.gwtplatform.dispatch.shared.ServiceException If there is a low level problem while rolling back.
         */
        private void rollback() throws ActionException, ServiceException {
            DefaultExecutionContext ctx = new DefaultExecutionContext(dispatch);
            for (int i = actionResults.size() - 1; i >= 0; i--) {
                ActionResult<?, ?> actionResult = actionResults.get(i);
                rollback(actionResult, ctx);
            }
        }

        private <A extends Action<R>, R extends Result> void rollback(ActionResult<A, R> actionResult,
                ExecutionContext ctx) throws ActionException,
                ServiceException {
            if (actionResult.isExecuted()) {
                dispatch.doUndo(actionResult.getAction(), actionResult.getResult(), ctx);
            } else {
                dispatch.doExecute(actionResult.getAction(), ctx);
            }
        }
    }

    private static final String actionValidatorMessage = " couldn't allow access to action : ";

    private final ActionHandlerValidatorRegistry actionHandlerValidatorRegistry;

    protected AbstractDispatchImpl(ActionHandlerValidatorRegistry actionHandlerValidatorRegistry) {
        this.actionHandlerValidatorRegistry = actionHandlerValidatorRegistry;
    }

    @Override
    public <A extends Action<R>, R extends Result> R execute(A action) throws ActionException, ServiceException {
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
    public <A extends Action<R>, R extends Result> void undo(A action, R result) throws ActionException,
            ServiceException {
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
     * Every single action will be executed by this function and validated by the {@link ActionValidator}.
     *
     * @param <A>    Type of associated {@link com.gwtplatform.dispatch.shared.Action} type.
     * @param <R>    Type of associated {@link com.gwtplatform.dispatch.shared.Result} type.
     * @param action The {@link com.gwtplatform.dispatch.shared.Action} to execute
     * @param ctx    The {@link ExecutionContext} associated with the {@link com.gwtplatform.dispatch.shared.Action}
     * @return The {@link com.gwtplatform.dispatch.shared.Result} to the client
     * @throws com.gwtplatform.dispatch.shared.ActionException
     * @throws com.gwtplatform.dispatch.shared.ServiceException
     */
    private <A extends Action<R>, R extends Result> R doExecute(A action,
            ExecutionContext ctx) throws ActionException, ServiceException {
        ActionHandler<A, R> handler = findHandler(action);

        ActionValidator actionValidator = findActionValidator(action);

        try {
            if (actionValidator.isValid(action)) {
                return handler.execute(action, ctx);
            } else {
                throw new ServiceException(actionValidator.getClass().getName() + actionValidatorMessage + action
                        .getClass().getName());
            }
        } catch (ActionException e) {
            throw e;
        } catch (Exception e) {
            String newMessage = "Service exception executing action \"" + action.getClass().getSimpleName() + "\", " +
                    "" + e.toString();
            throw new ServiceException(newMessage, e);
        }
    }

    private <A extends Action<R>, R extends Result> void doUndo(A action, R result,
            ExecutionContext ctx) throws ActionException, ServiceException {

        ActionValidator actionValidator = findActionValidator(action);

        ActionHandler<A, R> handler = findHandler(action);
        try {
            if (actionValidator.isValid(action)) {
                handler.undo(action, result, ctx);
            } else {
                throw new ServiceException(actionValidator.getClass().getName() + actionValidatorMessage + action
                        .getClass().getName());
            }
        } catch (ActionException e) {
            throw e;
        } catch (Exception cause) {
            throw new ServiceException(cause);
        }
    }

    private <A extends Action<R>, R extends Result> ActionValidator findActionValidator(A action) throws
            UnsupportedActionException {
        ActionHandlerValidatorInstance handlerValidator =
                actionHandlerValidatorRegistry.findActionHandlerValidator(action);
        if (handlerValidator == null) {
            throw new UnsupportedActionException(action);
        }

        return handlerValidator.getActionValidator();
    }

    @SuppressWarnings("unchecked")
    private <A extends Action<R>, R extends Result> ActionHandler<A, R> findHandler(A action)
            throws UnsupportedActionException {
        ActionHandlerValidatorInstance handlerValidator =
                actionHandlerValidatorRegistry.findActionHandlerValidator(action);

        if (handlerValidator == null) {
            throw new UnsupportedActionException(action);
        }

        return (ActionHandler<A, R>) handlerValidator.getActionHandler();
    }
}
