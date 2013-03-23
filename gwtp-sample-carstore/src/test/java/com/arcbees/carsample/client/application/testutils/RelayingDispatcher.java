package com.arcbees.carsample.client.application.testutils;

import java.util.HashMap;
import java.util.Map;

import com.arcbees.carsample.shared.dispatch.NoResults;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Class used to replace a real implementation of the Dispatcher. When executing
 * a request for an action, a predefined result will be sent back immediately.
 * 
 * To assign a result to an action, simply do: dispatcher.when({@link Action}
 * ).willReturn({@link Result});
 * 
 * @author Christian Goudreau
 */
public class RelayingDispatcher implements DispatchAsync {
    private Map<Class<? extends Action<?>>, Result> registry = new HashMap<Class<? extends Action<?>>, Result>();

    private Class<? extends Action<?>> currentAction = null;

    /**
     * This method must be used at least once before being able to use relaying
     * dispatcher. It will create an entry inside the registry and wait that the
     * use assign a result to it.
     * 
     * @param <A>
     *            The {@link Action} type.
     * @param action
     *            The class definition of the {@link Action}.
     * @return {@link RelayingDispatcher} instance.
     */
    public <A extends Action<?>> RelayingDispatcher given(Class<A> action) {
        registry.put(action, new NoResults());

        currentAction = action;

        return this;
    }

    /**
     * Once you've called at least one time {@link #given(Class)}, then calling
     * this function will assign a {@link Result} to the last {@link Action} you
     * assigned.
     * 
     * @param <R>
     *            The {@link Result} type.
     * @param result
     *            the {@link Result} to add inside the registry.
     */
    public <R extends Result> void willReturn(R result) {
        registry.put(currentAction, result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest execute(A action, AsyncCallback<R> callback) {
        callback.onSuccess((R) registry.get(action.getClass()));

        return new CompletedDispatchRequest();
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest undo(A action, R result, AsyncCallback<Void> callback) {
        callback.onSuccess(null);

        return new CompletedDispatchRequest();
    }
}
