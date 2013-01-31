package com.gwtplatform.dispatch.client.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSerializerProvider implements SerializerProvider {
    private final Map<String, Serializer> serializers = new HashMap<String, Serializer>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> Serializer<T> getSerializer(String serializerId) {
        return (Serializer<T>) serializers.get(serializerId);
    }

    protected void registerSerializer(String serializerId, Serializer<?> serializer) {
        serializers.put(serializerId, serializer);
    }
}
