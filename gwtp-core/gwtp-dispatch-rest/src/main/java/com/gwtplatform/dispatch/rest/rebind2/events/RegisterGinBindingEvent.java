/**
 * Copyright 2014 ArcBees Inc.
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

import com.gwtplatform.dispatch.rest.rebind2.utils.ClassDefinition;

public class RegisterGinBindingEvent {
    private final ClassDefinition definition;
    private final ClassDefinition implementation;
    private final boolean isSingleton;

    public RegisterGinBindingEvent(
            ClassDefinition definition,
            ClassDefinition implementation) {
        this(definition, implementation, false);
    }

    public RegisterGinBindingEvent(
            ClassDefinition definition,
            ClassDefinition implementation,
            boolean isSingleton) {
        this.definition = definition;
        this.implementation = implementation;
        this.isSingleton = isSingleton;
    }

    public ClassDefinition getDefinition() {
        return definition;
    }

    public ClassDefinition getImplementation() {
        return implementation;
    }

    public boolean isSingleton() {
        return isSingleton;
    }
}
