package com.gwtplatform.carstore.server.guice;

import javax.inject.Singleton;

import com.gwtplatform.carstore.server.DevBootStrapper;
import com.gwtplatform.carstore.server.authentication.BCryptPasswordSecurity;
import com.gwtplatform.carstore.server.authentication.PasswordSecurity;
import com.gwtplatform.carstore.server.dispatch.DispatchModule;
import com.gwtplatform.dispatch.server.guice.HandlerModule;

public class ServerModule extends HandlerModule {
    @Override
    protected void configureHandlers() {
        install(new DispatchModule());

        bind(PasswordSecurity.class).to(BCryptPasswordSecurity.class).in(Singleton.class);
        bind(DevBootStrapper.class).asEagerSingleton();
    }
}
