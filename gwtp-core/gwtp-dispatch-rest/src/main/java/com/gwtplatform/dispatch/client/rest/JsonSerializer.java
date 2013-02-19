/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.dispatch.client.rest;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.rpc.SerializationException;

import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;

public abstract class JsonSerializer<T> implements Serializer<T> {
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
            // TODO: Use our own SerializationException
            throw new SerializationException(e);
        }
    }

    @Override
    public String serialize(T value) throws SerializationException {
        return writer.toJson(value);
    }
}
