package com.arcbees.carsample.client.security;

import javax.inject.Singleton;

import com.google.gwt.inject.client.AbstractGinModule;

public class SecurityModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(LoggedInGatekeeper.class).in(Singleton.class);
        bind(CurrentUser.class).asEagerSingleton();
    }
}
