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

package com.gwtplatform.dispatch.rest.processors.subresource;

import java.util.Collection;

import javax.lang.model.element.ExecutableElement;

import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.details.Method;
import com.gwtplatform.dispatch.rest.processors.resource.Resource;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodUtils;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreTypes.asTypeElement;

public class SubResourceMethod implements ResourceMethod {
    private final Resource parentResource;
    private final Method method;
    private final EndPointDetails endPointDetails;
    private final SubResource subResource;

    public SubResourceMethod(
            Logger logger,
            Utils utils,
            Resource parentResource,
            ExecutableElement element) {
        this.parentResource = parentResource;
        this.method = new ResourceMethodUtils().processMethod(element, parentResource);
        this.endPointDetails = new EndPointDetails(logger, utils, method, parentResource.getEndPointDetails());
        this.subResource = new SubResource(logger, utils, this, asTypeElement(element.getReturnType()));
    }

    @Override
    public Resource getParentResource() {
        return parentResource;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public EndPointDetails getEndPointDetails() {
        return endPointDetails;
    }

    public SubResource getSubResource() {
        return subResource;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(method.getImports())
                .append(subResource.getType().getImports())
                .toList();
    }
}
