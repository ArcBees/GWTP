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

package com.gwtplatform.dispatch.rest.rebind.subresource;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.gwtplatform.dispatch.rest.rebind.resource.MethodContext;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceContext;

public class SubResourceContext extends ResourceContext {
    private final MethodContext methodContext;
    private final SubResourceMethodDefinition methodDefinition;

    public SubResourceContext(
            JClassType resourceType,
            MethodContext methodContext,
            SubResourceMethodDefinition methodDefinition) {
        super(resourceType);

        this.methodContext = methodContext;
        this.methodDefinition = methodDefinition;
    }

    public MethodContext getMethodContext() {
        return methodContext;
    }

    public SubResourceMethodDefinition getMethodDefinition() {
        return methodDefinition;
    }
}
