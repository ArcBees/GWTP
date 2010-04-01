package com.philbeaudoin.gwtp.dispatch.shared;

import java.io.Serializable;

/**
 * An action represents a command sent to the {@link com.philbeaudoin.gwtp.dispatch.server.Dispatch}. It has a
 * specific result type which is returned if the action is successful.
 *
 * @author David Peterson
 * @param <R>
 * The {@link Result} type.
 */
public interface Action<R extends Result> extends Serializable {
}
