package com.philbeaudoin.gwtp.dispatch.server;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;

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
     * @param <A>
     *            The {@link Action} type.
     * @param <R>
     *            The {@link Result} type.
     * @param action
     *            The {@link Action}.
     * @return The {@link Result}.
     * @throws ActionException
     *             if the action execution failed.
     * @throws ServiceException 
     *             if the execution failed due to a service error.
     */
    <A extends Action<R>, R extends Result> R execute( A action ) throws ActionException, ServiceException;

    /**
     * Undoes an action in the current context. If the surrounding execution
     * fails, the action will be rolled back.
     * 
     * @param <A>
     *            The {@link Action} type.
     * @param <R>
     *            The {@link Result} type.
     * @param action
     *            The {@link Action}.
     * @return The {@link Result}.
     * @throws ActionException
     *             if the action execution failed.
     * @throws ServiceException 
     *             if the execution failed due to a service error.
     */
    <A extends Action<R>, R extends Result> void undo(A action, R result) throws ActionException, ServiceException;
}
