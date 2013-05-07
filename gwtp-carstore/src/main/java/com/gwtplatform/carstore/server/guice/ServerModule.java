package com.gwtplatform.carstore.server.guice;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.gwtplatform.carstore.server.DevBootStrapper;
import com.gwtplatform.carstore.server.authentication.BCryptPasswordSecurity;
import com.gwtplatform.carstore.server.authentication.PasswordSecurity;
import com.gwtplatform.carstore.server.rest.RestModule;

public class ServerModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new RestModule());

        bind(PasswordSecurity.class).to(BCryptPasswordSecurity.class).in(Singleton.class);
        bind(DevBootStrapper.class).asEagerSingleton();
    }
}
