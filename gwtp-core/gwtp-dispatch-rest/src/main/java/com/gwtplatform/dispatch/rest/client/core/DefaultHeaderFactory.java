/**
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

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.google.gwt.http.client.RequestBuilder;
import com.gwtplatform.dispatch.rest.client.RestApplicationPath;
import com.gwtplatform.dispatch.rest.client.annotations.GlobalHeaderParams;
import com.gwtplatform.dispatch.rest.client.annotations.XsrfHeaderName;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.gin.RestParameterBindings;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import static com.google.gwt.user.client.rpc.RpcRequestBuilder.MODULE_BASE_HEADER;

public class DefaultHeaderFactory implements HeaderFactory {
    private static final String JSON_UTF8 = "application/json; charset=utf-8";

    private final HttpParameterFactory httpParameterFactory;
    private final RestParameterBindings globalParams;
    private final String securityHeaderName;
    private final String applicationPath;

    @Inject
    DefaultHeaderFactory(
            HttpParameterFactory httpParameterFactory,
            @GlobalHeaderParams RestParameterBindings globalParams,
            @XsrfHeaderName String securityHeaderName,
            @RestApplicationPath String applicationPath) {
        this.httpParameterFactory = httpParameterFactory;
        this.globalParams = globalParams;
        this.securityHeaderName = securityHeaderName;
        this.applicationPath = applicationPath;
    }

    @Override
    public void buildHeaders(RequestBuilder requestBuilder, RestAction<?> action, String securityToken) {
        List<HttpParameter> parameters = buildParameters(action, securityToken);
        addHeaders(requestBuilder, parameters);
    }

    private void addHeaders(RequestBuilder requestBuilder, List<HttpParameter> parameters) {
        for (HttpParameter parameter : parameters) {
            for (Entry<String, String> entry : parameter.getEncodedEntries()) {
                requestBuilder.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private List<HttpParameter> buildParameters(RestAction<?> action, String securityToken) {
        List<HttpParameter> headerParams = new ArrayList<HttpParameter>();

        addMediaTypes(headerParams);
        maybeAddModuleBase(action, headerParams);
        maybeAddSecurityToken(securityToken, action, headerParams);
        addGlobalHeaders(action, headerParams);
        addActionHeaders(action, headerParams);

        return headerParams;
    }

    private void addMediaTypes(List<HttpParameter> headerParams) {
        headerParams.add(httpParameterFactory.create(Type.HEADER, HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON));
        headerParams.add(httpParameterFactory.create(Type.HEADER, HttpHeaders.CONTENT_TYPE, JSON_UTF8));
    }

    private void maybeAddModuleBase(RestAction<?> action, List<HttpParameter> headerParams) {
        if (!isAbsoluteUrl(action.getPath())) {
            headerParams.add(httpParameterFactory.create(Type.HEADER, MODULE_BASE_HEADER, applicationPath));
        }
    }

    private void maybeAddSecurityToken(String securityToken, RestAction<?> action, List<HttpParameter> headerParams) {
        String xsrfToken = action.isSecured() ? securityToken : "";

        if (xsrfToken != null && !xsrfToken.isEmpty()) {
            headerParams.add(httpParameterFactory.create(Type.HEADER, securityHeaderName, xsrfToken));
        }
    }

    private boolean addGlobalHeaders(RestAction<?> action, List<HttpParameter> headerParams) {
        return headerParams.addAll(globalParams.get(action.getHttpMethod()));
    }

    private void addActionHeaders(RestAction<?> action, List<HttpParameter> headerParams) {
        headerParams.addAll(action.getParameters(Type.HEADER));
    }

    private boolean isAbsoluteUrl(String path) {
        String lowerCase = path.toLowerCase();
        return lowerCase.startsWith("http://") || lowerCase.startsWith("https://");
    }
}
