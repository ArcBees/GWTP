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

import com.gwtplatform.dispatch.rest.processors.domain.EndPointDetails;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.dispatch.rest.processors.domain.Variable;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.processors.tools.utils.Utils;

public class SubResource implements HasImports {
    private final Type impl;
    private final Type subResource;
    private final List<Variable> fields;
    private final List<ResourceMethod> methods;
    private final EndPointDetails endPointDetails;

    public SubResource(
            Logger logger,
            Utils utils,
            SubResourceMethod method,
            TypeElement element) {
        this.impl = null;
        this.fields = null;
        this.endPointDetails = new EndPointDetails(logger, utils, element, method.getEndPointDetails());
        this.subResource = null;
        this.methods = null;
    }

    public Type getImpl() {
        return impl;
    }

    public EndPointDetails getEndPointDetails() {
        return endPointDetails;
    }

    @Override
    public Collection<String> getImports() {
        return null;
    }
}
