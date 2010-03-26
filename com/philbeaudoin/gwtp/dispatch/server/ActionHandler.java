package com.philbeaudoin.gwtp.dispatch.server;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * Instances of this interface will handle specific types of {@link Action}
 * classes.
 * 
 * @author David Peterson
 */
public interface ActionHandler<A extends Action<R>, R extends Result> {

    /**
     * @return The type of {@link Action} supported by this handler.
     */
    Class<A> getActionType();

    /**
     * Handles the specified action. If you want to build a
     * compound action that can rollback automatically upon 
     * failure, call {@link ExecutionContext#execute(Action)}.
     * See <a href="http://code.google.com/p/gwt-dispatch/wiki/CompoundActions">here</a> for details. 
     * 
     * @param <T>
     *            The Result type.
     * @param action
     *            The action.
     * @param context
     *            The {@link ExecutionContext}.
     * @return The {@link Result}.
     * @throws ActionException
     *             if there is a problem performing the specified action.
     */
    R execute( A action, ExecutionContext context ) throws ActionException;

    /**
     * Undoes the specified action. If you want to build a
     * compound action that can rollback automatically upon 
     * failure, call {@link ExecutionContext#undo(Action, Result)}.
     * See <a href="http://code.google.com/p/gwt-dispatch/wiki/CompoundActions">here</a> for details. 
     * 
     * @param action
     *            The action.
     * @param result
     *            The result of the action.
     * @param context
     *            The {@link ExecutionContext}.
     * @throws ActionException
     *             if there is a problem performing the specified action.
     */
    void undo( A action, R result, ExecutionContext context ) throws ActionException;
}
