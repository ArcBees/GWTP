/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.serialization;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import com.github.nmorel.gwtjackson.client.JsonDeserializationContext;
import com.github.nmorel.gwtjackson.client.JsonSerializationContext;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.github.nmorel.gwtjackson.client.exception.JsonMappingException;

/**
 * JSON implementation of {@link Serialization}. It acts as a facade to <a href="https://github.com/nmorel/gwt-jackson">
 * gwt-jackson</a>.
 */
public class JsonSerialization implements Serialization {
    private static final String VOID = "java.lang.Void";
    // TODO: Consider using com.gwtplatform.dispatch.rest.rebind.utils.ContentType to help with wildcards.
    private static final String CONTENT_TYPE = MediaType.APPLICATION_JSON;
    private static final String CONTENT_TYPE_CHARSET = MediaType.APPLICATION_JSON + "; charset=utf-8";

    private final JacksonMapperProvider jacksonMapperProvider;
    private final JsonSerializationContext.Builder serializationContext;
    private final JsonDeserializationContext.Builder deserializationContext;

    @Inject
    JsonSerialization(JacksonMapperProvider jacksonMapperProvider) {
        this.jacksonMapperProvider = jacksonMapperProvider;

        deserializationContext = JsonDeserializationContext.builder()
                .failOnUnknownProperties(false);
        serializationContext = JsonSerializationContext.builder();
    }

    @Override
    public boolean canSerialize(String type, List<String> contentTypes) {
        return contentTypes.contains(CONTENT_TYPE) &&
                (VOID.equals(type) || jacksonMapperProvider.hasMapper(type));
    }

    @Override
    public boolean canDeserialize(String type, String contentType) {
        return VOID.equals(type)
                || (CONTENT_TYPE.equals(contentType) && jacksonMapperProvider.hasMapper(type));
    }

    @Override
    public <T> SerializedValue serialize(String type, List<String> contentTypes, T o) {
        if (VOID.equals(type) || o == null) {
            return null;
        }

        ObjectMapper<T> mapper = jacksonMapperProvider.getMapper(type);
        String json;
        try {
            json = mapper.write(o, getSerializationContext().build());
        } catch (JsonMappingException e) {
            throw new SerializationException("Unable to serialize request body. An unexpected error occurred.", e);
        }

        return new SerializedValue(CONTENT_TYPE_CHARSET, json);
    }

    @Override
    public <T> T deserialize(String type, String contentType, String json) {
        if (VOID.equals(type) || json == null || json.isEmpty()) {
            return null;
        }

        ObjectMapper<T> mapper = jacksonMapperProvider.getMapper(type);

        try {
            return mapper.read(json, getDeserializationContext().build());
        } catch (JsonMappingException e) {
            throw new SerializationException("Unable to deserialize response. An unexpected error occurred.", e);
        }
    }

    protected JsonSerializationContext.Builder getSerializationContext() {
        return serializationContext;
    }

    protected JsonDeserializationContext.Builder getDeserializationContext() {
        return deserializationContext;
    }
}
