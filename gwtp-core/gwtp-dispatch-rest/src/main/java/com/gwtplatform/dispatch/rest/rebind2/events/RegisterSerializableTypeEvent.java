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

package com.gwtplatform.dispatch.rest.rebind2.events;

import com.google.common.eventbus.EventBus;
import com.google.gwt.core.ext.typeinfo.JType;

public class RegisterSerializableTypeEvent {
    private final JType type;

    RegisterSerializableTypeEvent(
            JType type) {
        this.type = type;
    }

    public static void post(EventBus eventBus, JType type) {
        RegisterSerializableTypeEvent event = new RegisterSerializableTypeEvent(type);
        eventBus.post(event);
    }

    public JType getType() {
        return type;
    }
}
