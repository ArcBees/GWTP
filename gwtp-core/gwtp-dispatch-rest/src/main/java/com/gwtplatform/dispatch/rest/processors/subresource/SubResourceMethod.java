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

import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.Method;
import com.gwtplatform.dispatch.rest.processors.domain.ResourceType;
import com.gwtplatform.dispatch.rest.processors.resource.Resource;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreTypes.asTypeElement;

public class SubResourceMethod implements ResourceMethod {
    private final Optional<Resource> parentResource;
    private final Optional<SubResource> parentSubResource;

    private final Method method;
    private final EndPointDetails endPointDetails;
    private final SubResource subResource;

    public SubResourceMethod(
            Logger logger,
            Utils utils,
            ResourceType resourceType,
            ExecutableElement element) {
        this.parentResource = asResource(resourceType);
        this.parentSubResource = asSubResource(resourceType);

        ensureOnlyOneParentType(logger, element);

        this.method = new Method(element);
        this.endPointDetails = new EndPointDetails(logger, utils, element, resourceType.getEndPointDetails());
        this.subResource = new SubResource(logger, utils, this, asTypeElement(element.getReturnType()));
    }

    private Optional<Resource> asResource(ResourceType resourceType) {
        return resourceType instanceof Resource
                ? Optional.of((Resource) resourceType)
                : Optional.<Resource>absent();
    }

    private Optional<SubResource> asSubResource(ResourceType resourceType) {
        return resourceType instanceof SubResource
                ? Optional.of((SubResource) resourceType)
                : Optional.<SubResource>absent();
    }

    private void ensureOnlyOneParentType(Logger logger, ExecutableElement element) {
        if (!parentResource.isPresent() && !parentSubResource.isPresent()) {
            logger.error()
                    .context(element)
                    .log("Ambiguous hierarchy for sub resource method. Only Resource or SubResource is allowed.");
            throw new UnableToProcessException();
        }
    }

    @Override
    public Type getParentImpl() {
        return parentResource.isPresent() ? parentResource.get().getImpl() : parentSubResource.get().getImpl();
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
