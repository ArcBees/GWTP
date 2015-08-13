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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.lang.model.element.ExecutableElement;

import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.domain.Method;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.dispatch.rest.processors.resource.Resource;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.processors.tools.utils.Utils;

public class EndPointMethod implements ResourceMethod {
    private final Resource resource;

    private final Method method;
    private final EndPointDetails endPointDetails;
    private final EndPoint endPoint;

    public EndPointMethod(
            Logger logger,
            Utils utils,
            Resource resource,
            ExecutableElement element) {
        this.resource = resource;

        // TODO: Add an order to HttpVariables and converge both HttpVariables and Variables.
        method = new Method(element);
        endPointDetails = new EndPointDetails(logger, utils, element, resource.getEndPointDetails());
        endPoint = new EndPoint(logger, utils, this, element);
    }

    @Override
    public Type getParentImpl() {
        return resource.getImpl();
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public EndPointDetails getEndPointDetails() {
        return endPointDetails;
    }

    public EndPoint getEndPoint() {
        return endPoint;
    }

    @Override
    public Collection<String> getImports() {
        List<String> imports = new ArrayList<>(method.getImports());
        imports.addAll(endPoint.getImports());

        return imports;
    }
}
