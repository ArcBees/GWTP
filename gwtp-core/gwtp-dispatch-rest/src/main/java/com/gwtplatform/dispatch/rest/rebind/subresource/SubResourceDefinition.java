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

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.gwtplatform.dispatch.rest.rebind.Parameter;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceDefinition;

public class SubResourceDefinition extends ResourceDefinition {
    private final List<Parameter> parameters;

    public SubResourceDefinition(
            JClassType resourceInterface,
            String packageName,
            String className,
            List<Parameter> parameters,
            String path,
            boolean secured) {
        super(resourceInterface, packageName, className, path, secured);

        this.parameters = parameters;
    }

    public List<Parameter> getParameters() {
        return Lists.newArrayList(parameters);
    }
}
