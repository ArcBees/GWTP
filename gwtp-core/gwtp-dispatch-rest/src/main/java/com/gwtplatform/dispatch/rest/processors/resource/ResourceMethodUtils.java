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

import javax.lang.model.element.ExecutableElement;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.dispatch.rest.processors.details.HttpVariable;
import com.gwtplatform.dispatch.rest.processors.details.Method;

public class ResourceMethodUtils {
    public Method processMethod(ExecutableElement element, Resource parentResource) {
        Collection<HttpVariable> variables = parentResource.getEndPointDetails().getHttpVariables();
        Collection<String> existingVariableNames = FluentIterable.from(variables)
                .transform(new Function<HttpVariable, String>() {
                    @Override
                    public String apply(HttpVariable variable) {
                        return variable.getName();
                    }
                })
                .toList();

        return new Method(element, existingVariableNames);
    }
}
