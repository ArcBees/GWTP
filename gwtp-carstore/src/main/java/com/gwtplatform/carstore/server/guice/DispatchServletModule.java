package com.gwtplatform.carstore.server.guice;

import com.google.inject.servlet.ServletModule;

public class DispatchServletModule extends ServletModule {
    @Override
    public void configureServlets() {
        filter("/rest/*").through(GuiceRestEasyFilterDispatcher.class);
    }
}
