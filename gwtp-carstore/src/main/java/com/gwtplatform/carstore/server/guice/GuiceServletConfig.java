package com.gwtplatform.carstore.server.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.gwtplatform.carstore.shared.BootStrapper;

public class GuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        Injector injector = Guice.createInjector(new ServerModule(), new DispatchServletModule());

        BootStrapper bootStrapper = injector.getInstance(BootStrapper.class);
        bootStrapper.init();

        return injector;
    }
}
