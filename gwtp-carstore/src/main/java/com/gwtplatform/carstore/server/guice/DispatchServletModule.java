package com.gwtplatform.carstore.server.guice;

import com.google.inject.servlet.ServletModule;
import com.gwtplatform.carstore.server.servlet.ExampleServlet;

public class DispatchServletModule extends ServletModule {
    @Override
    public void configureServlets() {
        filter("/rest/*").through(GuiceRestEasyFilterDispatcher.class);
        serve("/exampleServlet").with(ExampleServlet.class);
    }
}
