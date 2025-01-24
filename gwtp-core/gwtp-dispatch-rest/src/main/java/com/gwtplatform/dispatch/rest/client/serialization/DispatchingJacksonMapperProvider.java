/*
 * Copyright 2016 ArcBees Inc.
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

import java.util.Set;

import com.github.nmorel.gwtjackson.client.ObjectMapper;

import jakarta.inject.Inject;

public class DispatchingJacksonMapperProvider implements JacksonMapperProvider {
    private final Set<JacksonMapperProvider> providers;

    @Inject
    DispatchingJacksonMapperProvider(Set<JacksonMapperProvider> providers) {
        this.providers = providers;
    }

    @Override
    public boolean hasMapper(String type) {
        return findProvider(type) != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectMapper<T> getMapper(String type) {
        JacksonMapperProvider provider = findProvider(type);
        return provider == null ? null : (ObjectMapper<T>) provider.getMapper(type);
    }

    private JacksonMapperProvider findProvider(String type) {
        for (JacksonMapperProvider provider : providers) {
            if (provider.hasMapper(type)) {
                return provider;
            }
        }

        return null;
    }
}
