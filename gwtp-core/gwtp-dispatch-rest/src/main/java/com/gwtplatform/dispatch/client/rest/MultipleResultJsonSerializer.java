package com.gwtplatform.dispatch.client.rest;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.gwtplatform.dispatch.shared.MultipleResult;

import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;

abstract class MultipleResultJsonSerializer<T extends Serializable> implements Serializer<MultipleResult<T>> {
    private final JsonReader<List<T>> reader;
    private final JsonWriter<List<T>> writer;

    protected MultipleResultJsonSerializer(JsonReader<List<T>> reader, JsonWriter<List<T>> writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public MultipleResult<T> deserialize(String value) throws SerializationException {
        try {
            return new MultipleResult<T>(reader.read(value));
        } catch (JSONException e) {
            //TODO: Use our own SerializationException
            throw new SerializationException(e);
        }
    }

    @Override
    public String serialize(MultipleResult<T> value) throws SerializationException {
        return writer.toJson(value.get());
    }
}
