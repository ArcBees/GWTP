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

package com.gwtplatform.dispatch.rest.client.gin;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.Sets;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestParameter;

public class RestParameterBuilder {
    private final RestDispatchAsyncModuleBuilder moduleBuilder;
    private final String key;
    private final Set<HttpMethod> httpMethods = Sets.newEnumSet(Arrays.asList(HttpMethod.values()), HttpMethod.class);

    RestParameterBuilder(RestDispatchAsyncModuleBuilder moduleBuilder,
                         String key) {
        this.moduleBuilder = moduleBuilder;
        this.key = key;
    }

    public RestParameterBuilder toHttpMethods(HttpMethod httpMethod, HttpMethod... otherHttpMethods) {
        httpMethods.clear();

        httpMethods.add(httpMethod);
        httpMethods.addAll(Arrays.asList(otherHttpMethods));

        return this;
    }

    public RestDispatchAsyncModuleBuilder withValue(String value) {
        RestParameter parameter = new RestParameter(key, value);

        for (HttpMethod httpMethod : httpMethods) {
            moduleBuilder.addGlobalHeaderParam(httpMethod, parameter);
        }

        return moduleBuilder;
    }
}
