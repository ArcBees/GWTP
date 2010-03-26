package com.philbeaudoin.gwtp.dispatch.client;

/**
 * A default implementation of {@link ExceptionHandler} which does not
 * do anything.
 *
 * @author David Peterson
 */
public class DefaultExceptionHandler implements ExceptionHandler {

    public DefaultExceptionHandler() {
    }

    /**
     * Always returns {@link Status#CONTINUE}.
     *
     * @param e The exception.
     * @return The status.
     */
    public Status onFailure( Throwable e ) {
        return Status.CONTINUE;
    }
}
