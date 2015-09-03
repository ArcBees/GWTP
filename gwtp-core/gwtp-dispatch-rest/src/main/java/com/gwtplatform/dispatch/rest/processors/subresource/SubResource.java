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
import java.util.List;

import javax.lang.model.element.TypeElement;

import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.details.Variable;
import com.gwtplatform.dispatch.rest.processors.endpoint.EndPointUtils;
import com.gwtplatform.dispatch.rest.processors.endpoint.IsEndPoint;
import com.gwtplatform.dispatch.rest.processors.resource.Resource;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceUtils;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

public class SubResource implements Resource, IsEndPoint {
    private final Type type;
    private final Type subResourceType;
    private final List<Variable> fields;
    private final List<ResourceMethod> methods;
    private final EndPointDetails endPointDetails;

    public SubResource(
            Logger logger,
            Utils utils,
            SubResourceMethod method,
            TypeElement element) {
        ResourceUtils resourceUtils = new ResourceUtils(logger, utils);
        TypeElement resourceType = resourceUtils.asResourceTypeElement(element);

        this.subResourceType = new Type(resourceType);
        this.type = processImplType(method);
        this.endPointDetails = new EndPointDetails(logger, utils, element, method.getEndPointDetails());
        this.fields = new EndPointUtils().processFields(method);
        this.methods = resourceUtils.processMethods(resourceType, this);
    }

    private Type processImplType(SubResourceMethod method) {
        Type parentImpl = method.getParentResource().getType();

        // TODO: NameFactory?
        String simpleName = parentImpl.getSimpleName() + "_" + subResourceType.getSimpleName();
        return new Type(subResourceType.getPackageName(), simpleName);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Type getResourceType() {
        return subResourceType;
    }

    @Override
    public EndPointDetails getEndPointDetails() {
        return endPointDetails;
    }

    @Override
    public List<Variable> getFields() {
        return fields;
    }

    @Override
    public List<ResourceMethod> getMethods() {
        return methods;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(methods)
                .transformAndConcat(HasImports.EXTRACT_IMPORTS_FUNCTION)
                .append(subResourceType.getImports())
                .toList();
    }
}
