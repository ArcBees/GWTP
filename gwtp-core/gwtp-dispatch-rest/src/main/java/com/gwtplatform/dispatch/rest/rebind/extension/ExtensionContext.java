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

package com.gwtplatform.dispatch.rest.rebind.extension;

import java.util.Collection;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;

public class ExtensionContext {
    private final ExtensionPoint extensionPoint;
    private final Collection<ClassDefinition> extensionDefinitions;
    @Nullable
    private final Collection<ResourceDefinition> resourceDefinitions;
    @Nullable
    private final ClassDefinition ginModuleDefinition;
    @Nullable
    private final ClassDefinition entryPointDefinition;

    public ExtensionContext(
            ExtensionPoint extensionPoint,
            Collection<ClassDefinition> extensionDefinitions,
            @Nullable Collection<ResourceDefinition> resourceDefinitions,
            @Nullable ClassDefinition ginModuleDefinition,
            @Nullable ClassDefinition entryPointDefinition) {
        this.extensionPoint = extensionPoint;
        this.extensionDefinitions = extensionDefinitions;
        this.resourceDefinitions = resourceDefinitions;
        this.ginModuleDefinition = ginModuleDefinition;
        this.entryPointDefinition = entryPointDefinition;
    }

    public ExtensionPoint getExtensionPoint() {
        return extensionPoint;
    }

    public Collection<ClassDefinition> getExtensionDefinitions() {
        return Lists.newArrayList(extensionDefinitions);
    }

    public Collection<ResourceDefinition> getResourceDefinitions() {
        return resourceDefinitions != null ? Lists.newArrayList(resourceDefinitions) : null;
    }

    public ClassDefinition getGinModuleDefinition() {
        return ginModuleDefinition;
    }

    public ClassDefinition getEntryPointDefinition() {
        return entryPointDefinition;
    }
}
