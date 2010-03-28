package com.philbeaudoin.gwtp.dispatch.server.secure;

import javax.servlet.http.HttpServletRequest;

import com.philbeaudoin.gwtp.dispatch.client.secure.SecureDispatchService;

/**
 * Implementors must provide an implementation of this interface and provide it
 * to the {@link SecureDispatchService} implementation so that it can check for
 * valid session ids.
 * 
 * @author David Peterson
 */
public interface SecureSessionValidator {
    boolean isValid( String sessionId, HttpServletRequest req );
}
