package com.gwtplatform.carstore.server.rest;

import javax.inject.Singleton;

import com.gwtplatform.dispatch.server.guice.HandlerModule;

public class RestModule extends HandlerModule {
    @Override
    protected void configureHandlers() {
        bind(CarResource.class);
        bind(ManufacturerResource.class);
        bind(SessionResource.class);
        bind(RatingResource.class);
        bind(JacksonProvider.class).in(Singleton.class);
    }
}
