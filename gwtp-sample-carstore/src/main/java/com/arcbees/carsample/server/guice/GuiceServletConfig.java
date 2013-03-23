package com.arcbees.carsample.server.guice;

import com.arcbees.carsample.shared.BootStrapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        Injector injector = Guice.createInjector(new ServerModule(), new DispatchServletModule());

        BootStrapper bootStrapper = injector.getInstance(BootStrapper.class);
        bootStrapper.init();

        return injector;
    }
}
