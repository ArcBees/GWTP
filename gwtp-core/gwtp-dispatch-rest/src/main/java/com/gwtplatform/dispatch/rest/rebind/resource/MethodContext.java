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

package com.gwtplatform.dispatch.rest.rebind.resource;

import com.google.gwt.core.ext.typeinfo.JMethod;

public class MethodContext {
    private final ResourceDefinition resourceDefinition;
    private final ResourceContext resourceContext;
    private final JMethod method;

    public MethodContext(
            ResourceDefinition resourceDefinition,
            ResourceContext resourceContext,
            JMethod method) {
        this.resourceDefinition = resourceDefinition;
        this.resourceContext = resourceContext;
        this.method = method;
    }

    public ResourceDefinition getResourceDefinition() {
        return resourceDefinition;
    }

    public ResourceContext getResourceContext() {
        return resourceContext;
    }

    public JMethod getMethod() {
        return method;
    }
}
