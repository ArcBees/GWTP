package com.philbeaudoin.gwtp.dispatch.server;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.ServiceException;

public interface Dispatch {

    /**
     * Executes the specified action and returns the appropriate result.
     * 
     * @param <A>
     *            The {@link Action} type.
     * @param <R>
     *            The {@link Result} type.
     * @param action
     *            The {@link Action}.
     * @return The action's result.
     * @throws ActionException
     *             if the action execution failed.
     * @throws ServiceException 
     *             if the execution failed due to a service error.
     */
    <A extends Action<R>, R extends Result> R execute( A action ) throws ActionException, ServiceException;

    /**
     * Undoes a previously executed action.
     * 
     * @param <A>
     *            The {@link Action} type.
     * @param <R>
     *            The {@link Result} type.
     * @param action
     *            The {@link Action} to undo.
     * @param result The result obtained when the action was previously executed.
     * @throws ActionException
     *             if undoing the action failed.
     * @throws ServiceException 
     *             if the execution failed due to a service error.
     */
    <A extends Action<R>, R extends Result> void undo(A action, R result) throws ActionException, ServiceException;
}
