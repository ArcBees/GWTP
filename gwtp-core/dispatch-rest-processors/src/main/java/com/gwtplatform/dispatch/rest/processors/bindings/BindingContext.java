/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.processors.bindings;

import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;

public class BindingContext {
    private final TypeDefinition implementer;

    private Optional<TypeDefinition> implemented;
    private Optional<TypeDefinition> scope;
    private boolean eagerSingleton;

    public BindingContext(TypeDefinition implementer) {
        this.implementer = implementer;
        this.implemented = Optional.absent();
        this.scope = Optional.absent();
    }

    public void setImplemented(TypeDefinition implemented) {
        this.implemented = Optional.fromNullable(implemented);
    }

    public void setScope(Class<?> scope) {
        this.scope = Optional.of(new TypeDefinition(scope));
    }

    public void setEagerSingleton(boolean eagerSingleton) {
        this.eagerSingleton = eagerSingleton;
    }

    public TypeDefinition getImplementer() {
        return implementer;
    }

    public Optional<TypeDefinition> getImplemented() {
        return implemented;
    }

    public Optional<TypeDefinition> getScope() {
        return scope;
    }

    public boolean isEagerSingleton() {
        return eagerSingleton;
    }
}
