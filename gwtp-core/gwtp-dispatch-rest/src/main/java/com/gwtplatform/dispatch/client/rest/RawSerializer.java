package com.gwtplatform.dispatch.client.rest;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * TODO: doc
 */
public class RawSerializer implements Serializer<Serializable> {
    @Override
    public Serializable deserialize(String value) throws SerializationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String serialize(Serializable value) throws SerializationException {
        return value.toString();
    }
}
