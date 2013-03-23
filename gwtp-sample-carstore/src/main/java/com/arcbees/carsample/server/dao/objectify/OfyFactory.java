package com.arcbees.carsample.server.dao.objectify;

import static com.arcbees.carsample.server.dao.objectify.OfyService.ofy;

import com.googlecode.objectify.ObjectifyFactory;

public class OfyFactory extends ObjectifyFactory {
    public OfyFactory() {
    }

    @Override
    public Ofy begin() {
        return new Ofy(ofy());
    }
}
