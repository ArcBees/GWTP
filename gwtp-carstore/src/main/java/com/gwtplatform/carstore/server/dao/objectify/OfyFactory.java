package com.gwtplatform.carstore.server.dao.objectify;

import com.googlecode.objectify.ObjectifyFactory;

import static com.gwtplatform.carstore.server.dao.objectify.OfyService.ofy;

public class OfyFactory extends ObjectifyFactory {
    public OfyFactory() {
    }

    @Override
    public Ofy begin() {
        return new Ofy(ofy());
    }
}
