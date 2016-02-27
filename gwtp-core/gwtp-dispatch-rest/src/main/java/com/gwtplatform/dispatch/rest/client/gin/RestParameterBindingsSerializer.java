/*
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

package com.gwtplatform.dispatch.rest.client.gin;

import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.client.core.parameters.CookieParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.FormParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.HeaderParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.MatrixParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.PathParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.QueryParameter;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

/**
 * Parses {@link com.gwtplatform.dispatch.rest.client.gin.RestParameterBindings} from and to JSON.
 */
public class RestParameterBindingsSerializer {
    static final UrlUtils URL_UTILS = new UrlUtils() {
        @Override
        public String encodePathSegment(String decodedPathSegment) {
            return encodeQueryString(decodedPathSegment);
        }

        @Override
        public String encodeQueryString(String decodedUrlComponent) {
            return decodedUrlComponent == null ? null : decodedUrlComponent.replace("\\", "\\\\").replace("\"", "\\\"");
        }

        @Override
        public String encodeMatrixParameter(String decodedMatrixParameter) {
            // Matrix param not supported in global params
            return null;
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

        @Override
        public String decodeMatrixParameter(String encodedMatrixParameter) {
            // not needed
            return null;
        }
    };

    public RestParameterBindingsSerializer() {
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
                HttpParameter parameter = deserialize(jsonParameters.get(i).isObject());
                parameters.put(httpMethod, parameter);
            }
        }

        return parameters;
    }

    private HttpParameter deserialize(JSONObject jsonParameter) {
        String key = jsonParameter.get("key").isString().stringValue();
        String value = jsonParameter.get("value").isString().stringValue();
        Type type = Type.valueOf(jsonParameter.get("type").isString().stringValue());
        HttpParameter parameter = null;

        switch (type) {
            case COOKIE:
                parameter = new CookieParameter(key, value);
                break;
            case FORM:
                parameter = new FormParameter(key, value, URL_UTILS);
                break;
            case HEADER:
                parameter = new HeaderParameter(key, value);
                break;
            case MATRIX:
                parameter = new MatrixParameter(key, value, URL_UTILS);
                break;
            case PATH:
                parameter = new PathParameter(key, value, null, URL_UTILS);
                break;
            case QUERY:
                parameter = new QueryParameter(key, value, URL_UTILS);
                break;
        }

        return parameter;
    }

    private void serializeValues(StringBuilder result, HttpMethod method, Set<HttpParameter> parameters) {
        result.append("\"").append(method.name()).append("\":[");

        for (HttpParameter parameter : parameters) {
            for (Entry<String, String> entry : parameter.getEncodedEntries()) {
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
