package com.gwtplatform.carstore.server.rest;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public class RestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CarResource.class);
        bind(ManufacturerResource.class);
        bind(SessionResource.class);
        bind(RatingResource.class);
        bind(JacksonProvider.class).in(Singleton.class);
    }
}
