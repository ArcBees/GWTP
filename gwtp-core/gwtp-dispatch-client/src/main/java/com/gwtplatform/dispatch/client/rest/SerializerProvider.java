package com.gwtplatform.dispatch.client.rest;

import java.io.Serializable;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationException;

public class SerializerProvider {
    @Inject
    public SerializerProvider() {
    }

    // throws SerializerNotFoundException?
    public <T extends Serializable> Serializer<T> getSerializer(String serializerId) throws SerializationException {
        //TODO: Retrieve the annotated serializer
        return new Serializer<T>() {
            @Override
            public T deserialize(String value) throws SerializationException {
                return null;
            }

            @Override
            public String serialize(Serializable value) throws SerializationException {
                return null;
            }
        };
    }
}
