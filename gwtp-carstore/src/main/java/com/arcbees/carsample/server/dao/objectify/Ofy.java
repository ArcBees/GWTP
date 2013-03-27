/*
 */

package com.arcbees.carsample.server.dao.objectify;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.util.cmd.ObjectifyWrapper;

public class Ofy extends ObjectifyWrapper<Ofy, OfyFactory> {
    public Ofy(Objectify base) {
        super(base);
    }

    public <T> LoadType<T> query(Class<T> clazz) {
        return load().type(clazz);
    }

    public <T> T get(Key<T> key){
        return load().key(key).get();
    }

    public <T> T get(Class<T> clazz, long id) {
        return load().type(clazz).id(id).get();
    }
}
