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

package com.gwtplatform.dispatch.rest.client.utils;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestParameter;

public class RestParameterBindings {
    private final Map<HttpMethod, Set<RestParameter>> parametersMap;

    public RestParameterBindings() {
        parametersMap = new EnumMap<HttpMethod, Set<RestParameter>>(HttpMethod.class);
    }

    public void put(HttpMethod httpMethod, RestParameter parameter) {
        Set<RestParameter> parameters = parametersMap.get(httpMethod);

        if (parameters == null) {
            parameters = new LinkedHashSet<RestParameter>();
            parametersMap.put(httpMethod, parameters);
        }

        parameters.add(parameter);
    }

    public Set<RestParameter> get(HttpMethod httpMethod) {
        if (!parametersMap.containsKey(httpMethod)) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(parametersMap.get(httpMethod));
    }

    public Set<Entry<HttpMethod, Set<RestParameter>>> entrySet() {
        return Collections.unmodifiableSet(parametersMap.entrySet());
    }

    public boolean isEmpty() {
        for (Set<RestParameter> parameters : parametersMap.values()) {
            if (!parameters.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
