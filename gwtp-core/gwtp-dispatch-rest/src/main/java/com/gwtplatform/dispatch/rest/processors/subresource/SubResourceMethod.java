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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.lang.model.element.ExecutableElement;

import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.Method;
import com.gwtplatform.dispatch.rest.processors.domain.ResourceType;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreTypes.asTypeElement;

public class SubResourceMethod implements ResourceMethod {
    private final ResourceType parent;
    private final Method method;
    private final EndPointDetails endPointDetails;
    private final SubResource subResource;

    public SubResourceMethod(
            Logger logger,
            Utils utils,
            ResourceType parent,
            ExecutableElement element) {
        this.parent = parent;

        this.method = new Method(element);
        this.endPointDetails = new EndPointDetails(logger, utils, element, parent.getEndPointDetails());
        this.subResource = new SubResource(logger, utils, this, asTypeElement(element.getReturnType()));
    }

    @Override
    public ResourceType getParent() {
        return parent;
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
        List<String> imports = new ArrayList<>(method.getImports());
        imports.addAll(subResource.getImports());

        return imports;
    }
}
