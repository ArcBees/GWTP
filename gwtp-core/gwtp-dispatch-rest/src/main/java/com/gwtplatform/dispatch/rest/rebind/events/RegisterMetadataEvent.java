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

package com.gwtplatform.dispatch.rest.rebind.events;

import com.google.common.eventbus.EventBus;
import com.gwtplatform.dispatch.rest.client.MetadataType;

public class RegisterMetadataEvent {
    private final String actionClass;
    private final MetadataType metadataType;
    private final String metadata;

    RegisterMetadataEvent(
            String actionClass,
            MetadataType metadataType,
            String metadata) {
        this.actionClass = actionClass;
        this.metadataType = metadataType;
        this.metadata = metadata;
    }

    public static void post(EventBus eventBus, String actionClass, MetadataType metadataType, String metadata) {
        RegisterMetadataEvent event = new RegisterMetadataEvent(actionClass, metadataType, metadata);
        eventBus.post(event);
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
}
