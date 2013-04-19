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

import java.util.Arrays;

import com.gwtplatform.dispatch.shared.Action;

/**
 * TODO: Documentation.
 */
public interface SerializerProvider {
    /**
     * TODO: Documentation.
     */
    static class SerializerKey {
        private final Class<? extends Action> actionClass;
        private final SerializedType serializedType;

        SerializerKey(Class<? extends Action> actionClass, SerializedType serializedType) {
            this.actionClass = actionClass;
            this.serializedType = serializedType;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[]{actionClass, serializedType});
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof SerializerKey)) {
                return false;
            }

            if (this == o) {
                return true;
            }
            SerializerKey other = (SerializerKey) o;
            return isEquals(actionClass, other.actionClass) && isEquals(serializedType, other.serializedType);
        }

        public boolean isEquals(Object a, Object b) {
            return a == b || (a != null && a.equals(b));
        }
    }

    <T> Serializer<T> getSerializer(Class<? extends Action> actionClass,
            SerializedType serializedType);
}
