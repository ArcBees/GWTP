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

package com.gwtplatform.dispatch.rest.rebind.serialization;

import com.google.common.base.Objects;
import com.gwtplatform.dispatch.rest.client.MetadataType;

public class RegisterMetadataHolder {
    private final String actionClass;
    private final MetadataType metadataType;
    private final String metadata;

    public RegisterMetadataHolder(
            String actionClass,
            MetadataType metadataType,
            String metadata) {
        this.actionClass = actionClass;
        this.metadataType = metadataType;
        this.metadata = metadata;
    }

    public String getActionClass() {
        return actionClass;
    }

    public MetadataType getMetadataType() {
        return metadataType;
    }

    public String getMetadata() {
        return metadata;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(actionClass, metadataType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        RegisterMetadataHolder other = (RegisterMetadataHolder) obj;
        return Objects.equal(this.actionClass, other.actionClass)
               && Objects.equal(this.metadataType, other.metadataType);
    }

    @Override
    public String toString() {
        return "(" + actionClass + ", " + metadataType.name() + ", " + metadata + ")";
    }
}
