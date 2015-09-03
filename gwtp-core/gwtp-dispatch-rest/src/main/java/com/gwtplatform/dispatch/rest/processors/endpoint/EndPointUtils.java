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
import java.util.List;

import com.gwtplatform.dispatch.rest.processors.details.Variable;
import com.gwtplatform.dispatch.rest.processors.resource.Resource;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethod;
import com.gwtplatform.dispatch.rest.processors.subresource.SubResource;

public class EndPointUtils {
    public List<Variable> processFields(ResourceMethod method) {
        List<Variable> fields = new ArrayList<>();
        Resource parent = method.getParentResource();

        if (parent instanceof SubResource) {
            fields.addAll(((SubResource) parent).getFields());
        }

        fields.addAll(method.getMethod().getParameters());

        return fields;
    }
}
