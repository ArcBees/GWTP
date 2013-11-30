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

import javax.inject.Inject;

import com.github.nmorel.gwtjackson.client.ObjectMapper;

public class JsonSerialization implements Serialization {
    private static final String VOID = "java.lang.Void";

    private final JacksonMapperProvider jacksonMapperProvider;

    @Inject
    JsonSerialization(JacksonMapperProvider jacksonMapperProvider) {
        this.jacksonMapperProvider = jacksonMapperProvider;
    }

    @Override
    public boolean canSerialize(String type) {
        return VOID.equals(type) || jacksonMapperProvider.hasMapper(type);
    }

    @Override
    public boolean canDeserialize(String type) {
        return jacksonMapperProvider.hasMapper(type);
    }

    @Override
    public <T> String serialize(T o, String type) {
        if (VOID.equals(type)) {
            return null;
        }

        ObjectMapper<T> mapper = jacksonMapperProvider.getMapper(type);
        return mapper.write(o);
    }

    @Override
    public <T> T deserialize(String json, String type) {
        if (VOID.equals(type)) {
            return null;
        }

        ObjectMapper<T> mapper = jacksonMapperProvider.getMapper(type);
        return mapper.read(json);
    }
}
