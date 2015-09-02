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

package com.gwtplatform.dispatch.rest.processors.resource;

import java.util.Collection;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.Resource;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

public class RootResource implements Resource {
    private static final String NAME_SUFFIX = "Impl";

    private final Type type;
    private final Type resourceType;
    private final EndPointDetails endPointDetails;
    private final List<ResourceMethod> methods;

    public RootResource(
            Logger logger,
            Utils utils,
            Element element) {
        ResourceUtils resourceUtils = new ResourceUtils(logger, utils);
        TypeElement resourceElement = resourceUtils.asResourceTypeElement(element);

        this.resourceType = new Type(resourceElement);
        this.type = new Type(this.resourceType.getPackageName(), this.resourceType.getSimpleName() + NAME_SUFFIX);
        this.endPointDetails = new EndPointDetails(logger, utils, resourceElement);
        this.methods = resourceUtils.processMethods(resourceElement, this);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Type getResourceType() {
        return resourceType;
    }

    @Override
    public EndPointDetails getEndPointDetails() {
        return endPointDetails;
    }

    @Override
    public List<ResourceMethod> getMethods() {
        return methods;
    }

    @Override
    public Collection<String> getImports() {
        return FluentIterable.from(methods)
                .transformAndConcat(HasImports.EXTRACT_IMPORTS_FUNCTION)
                .append(resourceType.getImports())
                .toList();
    }
}
