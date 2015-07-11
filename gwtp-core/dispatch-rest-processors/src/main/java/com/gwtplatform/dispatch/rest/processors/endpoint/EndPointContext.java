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

package com.gwtplatform.dispatch.rest.processors.endpoint;

import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.Method;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodContext;

public class EndPointContext {
    private final ResourceMethodContext resourceMethodContext;
    private final Method method;
    private final EndPointDetails endPointDetails;

    public EndPointContext(
            ResourceMethodContext resourceMethodContext,
            Method method,
            EndPointDetails endPointDetails) {
        this.resourceMethodContext = resourceMethodContext;
        this.method = method;
        this.endPointDetails = endPointDetails;
    }

    public EndPointDetails getEndPointDetails() {
        return endPointDetails;
    }

    public Method getMethod() {
        return method;
    }

    public ResourceMethodContext getResourceMethodContext() {
        return resourceMethodContext;
    }

    @Override
    public String toString() {
        return resourceMethodContext.toString();
    }
}
