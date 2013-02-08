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

import com.gwtplatform.dispatch.shared.Action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSerializerProvider implements SerializerProvider {
    // TODO: Use a more efficient way to store serializers
    private final Map<SerializerKey, Serializer> serializers = new HashMap<SerializerKey, Serializer>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Serializer<T> getSerializer(Class<? extends Action> actionClass,
            SerializedType serializedType) {
        return (Serializer<T>) serializers.get(new SerializerKey(actionClass, serializedType));
    }

    protected void registerSerializer(Class<? extends Action> actionClass, SerializedType serializedType,
            Serializer<?> serializer) {
        serializers.put(new SerializerKey(actionClass, serializedType), serializer);
    }
}
