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

package com.gwtplatform.dispatch.client.rest;

import java.util.Arrays;

import com.gwtplatform.dispatch.shared.Action;

public interface ActionMetadataProvider {
    class MetadataKey {
        private final Class<? extends Action> actionClass;
        private final MetadataType metadataType;

        MetadataKey(Class<? extends Action> actionClass, MetadataType metadataType) {
            this.actionClass = actionClass;
            this.metadataType = metadataType;
        }

        public static MetadataKey create(Class<? extends Action> actionClass, MetadataType metadataType) {
            return new MetadataKey(actionClass, metadataType);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[]{actionClass, metadataType});
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof MetadataKey)) {
                return false;
            }

            if (this == o) {
                return true;
            }
            MetadataKey other = (MetadataKey) o;
            return isEquals(actionClass, other.actionClass) && isEquals(metadataType, other.metadataType);
        }

        public boolean isEquals(Object a, Object b) {
            return a == b || (a != null && a.equals(b));
        }
    }

    /**
     * Retrieve metadata for the given action and {@link MetadataType} pair.
     *
     * @param action       The action for which to retrieve metadata.
     * @param metadataType The kind of metadata to retrieve.
     * @return The stored value for the given parameters or <code>null</code> if there is no match.
     */
    Object getValue(Action<?> action, MetadataType metadataType);
}
