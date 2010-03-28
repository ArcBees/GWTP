package com.philbeaudoin.gwtp.dispatch.client.secure;

/**
 * Provides access to the session ID.
 * 
 * @author David Peterson
 */
public interface SecureSessionAccessor {
    /**
     * Gets the current session ID.
     * 
     * @return The ID.
     */
    String getSessionId();

    /**
     * Clears the session id, effectively closing the current session.
     * 
     * @return <code>true</code> if successful.
     */
    boolean clearSessionId();
}
