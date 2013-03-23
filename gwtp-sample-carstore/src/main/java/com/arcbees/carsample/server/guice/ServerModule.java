package com.arcbees.carsample.server.guice;

import javax.inject.Singleton;

import com.arcbees.carsample.server.DevBootStrapper;
import com.arcbees.carsample.server.authentication.BCryptPasswordSecurity;
import com.arcbees.carsample.server.authentication.PasswordSecurity;
import com.arcbees.carsample.server.dispatch.DispatchModule;
import com.arcbees.carsample.shared.BootStrapper;
import com.gwtplatform.dispatch.server.guice.HandlerModule;

public class ServerModule extends HandlerModule {
    @Override
    protected void configureHandlers() {
        install(new DispatchModule());

        bind(BootStrapper.class).to(DevBootStrapper.class).in(Singleton.class);
        bind(PasswordSecurity.class).to(BCryptPasswordSecurity.class).in(Singleton.class);
    }
}
