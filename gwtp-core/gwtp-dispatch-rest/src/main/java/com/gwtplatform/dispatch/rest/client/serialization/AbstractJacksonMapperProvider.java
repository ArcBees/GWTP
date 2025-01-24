/*
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

import java.util.HashMap;
import java.util.Map;

import com.github.nmorel.gwtjackson.client.ObjectMapper;

import jakarta.inject.Provider;

/**
 * Abstract class providing utility methods to reduce the code generation complexity of {@link JacksonMapperProvider}.
 */
public abstract class AbstractJacksonMapperProvider implements JacksonMapperProvider {
    private final Map<String, Provider<? extends ObjectMapper<?>>> objectMapperProviders =
            new HashMap<String, Provider<? extends ObjectMapper<?>>>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectMapper<T> getMapper(String type) {
        ObjectMapper<?> objectMapper = null;

        if (objectMapperProviders.containsKey(type)) {
            objectMapper = objectMapperProviders.get(type).get();
        }

        return (ObjectMapper<T>) objectMapper;
    }

    @Override
    public boolean hasMapper(String type) {
        return objectMapperProviders.containsKey(type);
    }

    protected void registerProvider(String type, Provider<? extends ObjectMapper<?>> mapperProvider) {
        objectMapperProviders.put(type, mapperProvider);
    }
}
