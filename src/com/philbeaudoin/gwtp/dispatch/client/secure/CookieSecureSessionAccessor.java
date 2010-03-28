package com.philbeaudoin.gwtp.dispatch.client.secure;

import com.google.gwt.user.client.Cookies;

public class CookieSecureSessionAccessor implements SecureSessionAccessor {

    private String cookieName;

    public CookieSecureSessionAccessor( String cookieName ) {
        this.cookieName = cookieName;
    }

    public boolean clearSessionId() {
        if ( Cookies.getCookie( cookieName ) != null ) {
            Cookies.removeCookie( cookieName );
        }
        return false;
    }

    public String getSessionId() {
        return Cookies.getCookie( cookieName );
    }

}
