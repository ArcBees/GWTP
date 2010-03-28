package com.philbeaudoin.gwtp.dispatch.server;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * This is a subclass of {@link ActionHandlerRegistry} which allows registration
 * of handlers by passing in the handler instance directly.
 * 
 * @author David Peterson
 */
public interface InstanceActionHandlerRegistry extends ActionHandlerRegistry {

  /**
     * Adds the specified handler instance to the registry.
     * 
     * @param handler
     *            The action handler.
     */
    public <A extends Action<R>, R extends Result> void addHandler( ActionHandler<A, R> handler );

    /**
     * Removes the specified handler.
     * 
     * @param handler
     *            The handler.
     * @return <code>true</code> if the handler was previously registered and
     *         was successfully removed.
     */
    public <A extends Action<R>, R extends Result> boolean removeHandler( ActionHandler<A, R> handler );
}
