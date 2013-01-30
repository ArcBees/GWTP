package com.gwtplatform.dispatch.client.rest;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * TODO: doc
 */
public interface Serializer<T extends Serializable> {
    T deserialize(String value) throws SerializationException;

    String serialize(Serializable value) throws SerializationException;
}
