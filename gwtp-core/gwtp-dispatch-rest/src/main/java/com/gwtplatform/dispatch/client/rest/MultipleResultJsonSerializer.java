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
            // TODO: Use our own SerializationException
            throw new SerializationException(e);
        }
    }

    @Override
    public String serialize(MultipleResult<T> value) throws SerializationException {
        return writer.toJson(value.get());
    }
}
