package com.gwtplatform.dispatch.client.rest;

import java.io.Serializable;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.gwtplatform.dispatch.shared.SimpleResult;

import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;

abstract class SimpleResultJsonSerializer<T extends Serializable> implements Serializer<SimpleResult<T>> {
    private final JsonReader<T> reader;
    private final JsonWriter<T> writer;

    protected SimpleResultJsonSerializer(JsonReader<T> reader, JsonWriter<T> writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public SimpleResult<T> deserialize(String value) throws SerializationException {
        try {
            return new SimpleResult<T>(reader.read(value));
        } catch (JSONException e) {
            //TODO: Use our own SerializationException
            throw new SerializationException(e);
        }
    }

    @Override
    public String serialize(SimpleResult<T> value) throws SerializationException {
        return writer.toJson(value.get());
    }
}
