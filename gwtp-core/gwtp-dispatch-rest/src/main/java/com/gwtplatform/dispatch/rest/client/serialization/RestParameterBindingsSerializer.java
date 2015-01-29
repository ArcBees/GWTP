/**
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.serialization;

import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.client.core.parameters.DefaultHttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.utils.RestParameterBindings;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

/**
 * Parses {@link com.gwtplatform.dispatch.rest.client.utils.RestParameterBindings} from and to JSON.
 */
public class RestParameterBindingsSerializer {
    private static final UrlUtils URL_UTILS = new UrlUtils() {
        @Override
        public String encodePathSegment(String decodedPathSegment) {
            return encodeQueryString(decodedPathSegment);
        }

        @Override
        public String encodeQueryString(String decodedUrlComponent) {
            return decodedUrlComponent == null ? null : decodedUrlComponent.replace("\\", "\\\\").replace("\"", "\\\"");
        }

        @Override
        public String decodePathSegment(String encodedPathSegment) {
            // not needed
            return null;
        }

        @Override
        public String decodeQueryString(String encodedUrlComponent) {
            // not needed
            return null;
        }
    };

    private final HttpParameterFactory parameterFactory;

    public RestParameterBindingsSerializer() {
        parameterFactory = new DefaultHttpParameterFactory(URL_UTILS, null);
    }

    /**
     * Used to serialize the bindings at compilation. Usage of GWT code is <b>not</b> allowed.
     */
    public String serialize(RestParameterBindings parameterBindings) {
        StringBuilder result = new StringBuilder("{");

        for (Entry<HttpMethod, Set<HttpParameter>> entry : parameterBindings.entrySet()) {
            serializeValues(result, entry.getKey(), entry.getValue());
        }

        if (!parameterBindings.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append("}");

        return result.toString();
    }

    /**
     * Used to deserialize the bindings once on the client. Usage of GWT code is allowed.
     */
    public RestParameterBindings deserialize(String encodedParameters) {
        RestParameterBindings parameters = new RestParameterBindings();

        JSONObject json = JSONParser.parseStrict(encodedParameters).isObject();
        for (String method : json.keySet()) {
            HttpMethod httpMethod = HttpMethod.valueOf(method);
            JSONArray jsonParameters = json.get(method).isArray();

            for (int i = 0; i < jsonParameters.size(); ++i) {
                JSONObject jsonParameter = jsonParameters.get(i).isObject();
                String key = jsonParameter.get("key").isString().stringValue();
                String value = jsonParameter.get("value").isString().stringValue();
                Type type = Type.valueOf(jsonParameter.get("type").isString().stringValue());
                HttpParameter parameter = parameterFactory.create(type, key, value);

                parameters.put(httpMethod, parameter);
            }
        }

        return parameters;
    }

    private void serializeValues(StringBuilder result, HttpMethod method, Set<HttpParameter> parameters) {
        result.append("\"").append(method.name()).append("\":[");

        for (HttpParameter parameter : parameters) {
            for (Entry<String, String> entry : parameter.getEntries()) {
                result.append("{\"type\": \"").append(parameter.getType().name())
                        .append("\", \"key\": \"").append(entry.getKey())
                        .append("\", \"value\": \"").append(entry.getValue()).append("\"},");
            }
        }

        if (!parameters.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
        }

        result.append("],");
    }
}
