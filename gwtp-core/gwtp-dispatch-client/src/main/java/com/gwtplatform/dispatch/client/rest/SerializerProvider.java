package com.gwtplatform.dispatch.client.rest;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.SerializationException;

public interface SerializerProvider {
    <T extends Serializable> Serializer getSerializer(String serializerId);
}
