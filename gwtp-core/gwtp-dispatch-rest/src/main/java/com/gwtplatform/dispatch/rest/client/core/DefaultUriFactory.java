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

package com.gwtplatform.dispatch.rest.client.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.gwtplatform.dispatch.rest.client.RestApplicationPath;
import com.gwtplatform.dispatch.rest.client.annotations.GlobalQueryParams;
import com.gwtplatform.dispatch.rest.client.gin.RestParameterBindings;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import jakarta.inject.Inject;

public class DefaultUriFactory implements UriFactory {
    private final RestParameterBindings globalQueryParams;
    private final String applicationPath;

    @Inject
    DefaultUriFactory(
            @GlobalQueryParams RestParameterBindings globalQueryParams,
            @RestApplicationPath String applicationPath) {
        this.globalQueryParams = globalQueryParams;
        this.applicationPath = applicationPath;
    }

    @Override
    public String buildUrl(RestAction<?> action) {
        String prefix = buildPrefix(action);
        String path = buildPath(action);
        String matrixString = buildMatrixString(action);
        String queryString = buildQueryString(action, Type.QUERY);

        StringBuilder url = new StringBuilder(prefix).append(path).append(matrixString);

        if (!queryString.isEmpty()) {
            url.append('?').append(queryString);
        }

        return url.toString();
    }

    @Override
    public String buildQueryString(RestAction<?> action, Type type) {
        List<HttpParameter> parameters = getParameters(action, type);
        StringBuilder queryString = new StringBuilder();

        for (HttpParameter parameter : parameters) {
            for (Entry<String, String> entry : parameter.getEncodedEntries()) {
                queryString.append("&")
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
            }
        }

        if (queryString.length() != 0) {
            queryString.deleteCharAt(0);
        }

        return queryString.toString();
    }

    private String buildPrefix(RestAction<?> action) {
        String prefix = "";
        if (!isAbsoluteUrl(action.getPath())) {
            prefix = applicationPath;
        }

        return prefix;
    }

    private String buildPath(RestAction<?> action) {
        List<HttpParameter> params = action.getParameters(Type.PATH);
        String path = action.getPath();

        for (HttpParameter param : params) {
            List<Entry<String, String>> entries = param.getEncodedEntries();
            assert entries.size() == 1;

            Entry<String, String> entry = entries.get(0);
            path = path.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return path;
    }

    private String buildMatrixString(RestAction<?> action) {
        List<HttpParameter> parameters = action.getParameters(Type.MATRIX);
        StringBuilder matrixString = new StringBuilder();

        for (HttpParameter parameter : parameters) {
            for (Entry<String, String> entry : parameter.getEncodedEntries()) {
                matrixString.append(';')
                        .append(entry.getKey())
                        .append('=')
                        .append(entry.getValue());
            }
        }

        return matrixString.toString();
    }

    private List<HttpParameter> getParameters(RestAction<?> action, Type type) {
        List<HttpParameter> queryParams = new ArrayList<HttpParameter>();
        queryParams.addAll(globalQueryParams.get(action.getHttpMethod()));
        queryParams.addAll(action.getParameters(type));
        return queryParams;
    }

    private boolean isAbsoluteUrl(String path) {
        String lowerCase = path.toLowerCase();
        return lowerCase.startsWith("http://") || lowerCase.startsWith("https://");
    }
}
