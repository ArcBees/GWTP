package com.philbeaudoin.gwtp.dispatch.client;

/**
 * Implementations of this interface can be added to a {@link DispatchAsync} implementation
 * to intercept exceptions which return from further up the chain.
 *
 * @author David Peterson
 */
public interface ExceptionHandler {

    public enum Status {
        STOP, CONTINUE
    }

    /**
     * This method is called when an exception occurs. Return {@link Status#STOP}
     * to indicate that the exception has been handled and further processing should
     * not occur. Return {@link Status#CONTINUE} to indicate that further processing
     * should occur.
     *
     * @param e The exception.
     * @return The status after execution.
     */
    Status onFailure( Throwable e );
}
