package com.philbeaudoin.gwtp.dispatch.client.gin;

import com.philbeaudoin.gwtp.dispatch.client.appengine.AppEngineSecureSessionAccessor;
import com.philbeaudoin.gwtp.dispatch.client.secure.SecureSessionAccessor;

import com.google.gwt.inject.client.AbstractGinModule;

/**
 * Configures the application to use Google App Engine security for
 * authentication.
 * 
 * @author David Peterson
 */
public class AppEngineSecurityModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind( SecureSessionAccessor.class ).to( AppEngineSecureSessionAccessor.class );
    }
}
