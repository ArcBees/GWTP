package com.gwtplatform.dispatch.client.rest;

import com.google.gwt.user.client.rpc.SerializationException;
import com.gwtplatform.dispatch.shared.NoResult;

public class NoResultSerializer implements Serializer<NoResult> {
    @Override
    public NoResult deserialize(String value) throws SerializationException {
        return new NoResult();
    }

    @Override
    public String serialize(NoResult value) throws SerializationException {
        return "";
    }
}
