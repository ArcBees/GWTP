package com.philbeaudoin.gwtp.dispatch.server.appengine;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.philbeaudoin.gwtp.dispatch.server.secure.SecureSessionValidator;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Validates the client session id with the provided request.
 * 
 * @author David Peterson
 * @author David Chandler
 * 
 */
public class AppEngineSecureSessionValidator implements SecureSessionValidator {

    public AppEngineSecureSessionValidator() {
    }

    public boolean isValid( String clientSessionId, HttpServletRequest req ) {

        String serverName = req.getServerName();
        User user = UserServiceFactory.getUserService().getCurrentUser();
        if ( user != null ) {
            // User is logged in, now try to match session tokens
            // to prevent CSRF
            String sessionId = "";
            Cookie[] cookies = req.getCookies();
            for ( Cookie cookie : cookies ) {
                if ( cookie.getName().equals( "ACSID" ) ) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
            // Skip check on localhost so we can test in AppEngine
            // local dev env
            if ( ( "localhost".equals( serverName ) ) || ( sessionId.equals( clientSessionId ) ) ) {
                return true;
            }
        }
        return false;
    }
}
