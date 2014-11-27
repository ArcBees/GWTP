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

package com.gwtplatform.dispatch.rest.rebind2.subresource;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceContext;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceMethodContext;

public class SubResourceContext extends ResourceContext {
    private final ResourceMethodContext methodContext;
    private final SubResourceMethodDefinition methodDefinition;

    public SubResourceContext(
            JClassType resourceType,
            ResourceMethodContext methodContext,
            SubResourceMethodDefinition methodDefinition) {
        super(resourceType);

        this.methodContext = methodContext;
        this.methodDefinition = methodDefinition;
    }

    public ResourceMethodContext getMethodContext() {
        return methodContext;
    }

    public SubResourceMethodDefinition getMethodDefinition() {
        return methodDefinition;
    }
}
