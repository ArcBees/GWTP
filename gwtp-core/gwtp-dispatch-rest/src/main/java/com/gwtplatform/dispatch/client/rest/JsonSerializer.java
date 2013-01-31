package com.gwtplatform.dispatch.client.rest;

import java.io.Serializable;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.rpc.SerializationException;

import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;

public abstract class JsonSerializer<T extends Serializable> implements Serializer<T> {
    private final JsonReader<T> reader;
    private final JsonWriter<T> writer;

    protected JsonSerializer(JsonReader<T> reader, JsonWriter<T> writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public T deserialize(String value) throws SerializationException {
        try {
            return reader.read(value);
        } catch (JSONException e) {
            //TODO: Use our own SerializationException
            throw new SerializationException(e);
        }
    }

    @Override
    public String serialize(T value) throws SerializationException {
        return writer.toJson(value);
    }
}
