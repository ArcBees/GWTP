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

package com.gwtplatform.dispatch.rest.client;

import com.google.common.base.Objects;
import com.gwtplatform.dispatch.rest.shared.MetadataType;
import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * An implementation of this class is generated at compile-time to provide additional information about generated
 * {@link RestAction}s. Possible metadata is enumerated in {@link MetadataType}.
 */
public interface ActionMetadataProvider {
    /**
     * A key composed of {@link RestAction} and a {@link MetadataType}.
     */
    class MetadataKey {
        private final Class<? extends RestAction> actionClass;
        private final MetadataType metadataType;

        MetadataKey(Class<? extends RestAction> actionClass,
                    MetadataType metadataType) {
            this.actionClass = actionClass;
            this.metadataType = metadataType;
        }

        public static MetadataKey create(Class<? extends RestAction> actionClass, MetadataType metadataType) {
            return new MetadataKey(actionClass, metadataType);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(actionClass, metadataType);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || !(o instanceof MetadataKey)) {
                return false;
            }

            MetadataKey other = (MetadataKey) o;
            return Objects.equal(actionClass, other.actionClass) && Objects.equal(metadataType, other.metadataType);
        }
    }

    /**
     * Retrieve metadata for the given action and {@link MetadataType} pair.
     *
     * @param action       The action for which to retrieve metadata.
     * @param metadataType The kind of metadata to retrieve.
     * @return The stored value for the given parameters or <code>null</code> if there is no match.
     */
    Object getValue(RestAction<?> action, MetadataType metadataType);
}
