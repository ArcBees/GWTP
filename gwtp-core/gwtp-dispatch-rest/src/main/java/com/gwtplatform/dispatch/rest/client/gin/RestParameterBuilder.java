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
import java.util.EnumSet;
import java.util.Set;

import com.gwtplatform.dispatch.rest.client.utils.RestParameterBindings;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestParameter;

/**
 * Configure a global parameter to be sent with every HTTP requests.
 */
public class RestParameterBuilder {
    private final RestDispatchAsyncModuleBuilder moduleBuilder;
    private final RestParameterBindings target;
    private final String key;
    private final Set<HttpMethod> httpMethods = EnumSet.allOf(HttpMethod.class);

    RestParameterBuilder(
            RestDispatchAsyncModuleBuilder moduleBuilder,
            RestParameterBindings target,
            String key) {
        this.moduleBuilder = moduleBuilder;
        this.target = target;
        this.key = key;
    }

    /**
     * Define with which {@link HttpMethod} the parameter will be added. If none are specified, it will defaults to
     * all.
     *
     * @param httpMethod an {@link HttpMethod} to link this parameter to.
     * @param otherHttpMethods more {@link HttpMethod}s to link this parameter to.
     *
     * @return this builder instance.
     */
    public RestParameterBuilder toHttpMethods(HttpMethod httpMethod, HttpMethod... otherHttpMethods) {
        httpMethods.clear();

        httpMethods.add(httpMethod);
        httpMethods.addAll(Arrays.asList(otherHttpMethods));

        return this;
    }

    /**
     * Define the value of this parameter.
     *
     * @param value The value for this parameter.
     *
     * @return The module builder instance.
     */
    public RestDispatchAsyncModuleBuilder withValue(String value) {
        RestParameter parameter = new RestParameter(key, value);

        for (HttpMethod httpMethod : httpMethods) {
            target.put(httpMethod, parameter);
        }

        return moduleBuilder;
    }
}
