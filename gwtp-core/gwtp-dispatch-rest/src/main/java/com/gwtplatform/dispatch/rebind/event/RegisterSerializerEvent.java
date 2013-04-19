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

package com.gwtplatform.dispatch.rebind.event;

import com.gwtplatform.dispatch.client.rest.SerializedType;

/**
 * TODO: Documentation.
 */
public class RegisterSerializerEvent {
    private final String actionClass;
    private final SerializedType serializedType;
    private final String serializerClass;

    public RegisterSerializerEvent(String actionClass, SerializedType serializedType, String serializerClass) {
        this.actionClass = actionClass;
        this.serializedType = serializedType;
        this.serializerClass = serializerClass;
    }

    public String getActionClass() {
        return actionClass;
    }

    public SerializedType getSerializedType() {
        return serializedType;
    }

    public String getSerializerClass() {
        return serializerClass;
    }
}
