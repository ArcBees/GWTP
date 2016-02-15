/*
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

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;

public class RestParameterBindings {
    private final Map<HttpMethod, Set<HttpParameter>> parametersMap;

    public RestParameterBindings() {
        parametersMap = new EnumMap<>(HttpMethod.class);
    }

    public void put(HttpMethod httpMethod, HttpParameter parameter) {
        Set<HttpParameter> parameters = parametersMap.get(httpMethod);

        if (parameters == null) {
            parameters = new LinkedHashSet<>();
        }
        if (parameter.getObject() != null) {
            parametersMap.put(httpMethod, parameters);
        }

        parameters.add(parameter);
    }

    public Set<HttpParameter> get(HttpMethod httpMethod) {
        if (!parametersMap.containsKey(httpMethod)) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(parametersMap.get(httpMethod));
    }

    public Set<Entry<HttpMethod, Set<HttpParameter>>> entrySet() {
        return Collections.unmodifiableSet(parametersMap.entrySet());
    }

    public boolean isEmpty() {
        for (Set<HttpParameter> parameters : parametersMap.values()) {
            if (!parameters.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
