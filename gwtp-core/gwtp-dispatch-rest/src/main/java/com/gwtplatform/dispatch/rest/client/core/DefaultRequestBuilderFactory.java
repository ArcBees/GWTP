/*
 * Copyright 2013 ArcBees Inc.
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

import java.util.EnumMap;
import java.util.Map;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.gwtplatform.dispatch.rest.client.annotations.RequestTimeout;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

import jakarta.inject.Inject;

/**
 * Default implementation for {@link RequestBuilderFactory}.
 */
public class DefaultRequestBuilderFactory implements RequestBuilderFactory {
    private static final Map<HttpMethod, Method> HTTP_METHOD_TO_REQUEST_BUILDER;

    static {
        HTTP_METHOD_TO_REQUEST_BUILDER = new EnumMap<HttpMethod, Method>(HttpMethod.class);
        HTTP_METHOD_TO_REQUEST_BUILDER.put(HttpMethod.GET, RequestBuilder.GET);
        HTTP_METHOD_TO_REQUEST_BUILDER.put(HttpMethod.POST, RequestBuilder.POST);
        HTTP_METHOD_TO_REQUEST_BUILDER.put(HttpMethod.PUT, RequestBuilder.PUT);
        HTTP_METHOD_TO_REQUEST_BUILDER.put(HttpMethod.DELETE, RequestBuilder.DELETE);
        HTTP_METHOD_TO_REQUEST_BUILDER.put(HttpMethod.HEAD, RequestBuilder.HEAD);
    }

    private final HttpRequestBuilderFactory httpRequestBuilderFactory;
    private final BodyFactory bodyFactory;
    private final HeaderFactory headerFactory;
    private final UriFactory uriFactory;
    private final Integer requestTimeoutMs;

    @Inject
    protected DefaultRequestBuilderFactory(
            HttpRequestBuilderFactory httpRequestBuilderFactory,
            BodyFactory bodyFactory,
            HeaderFactory headerFactory,
            UriFactory uriFactory,
            @RequestTimeout Integer requestTimeoutMs) {
        this.httpRequestBuilderFactory = httpRequestBuilderFactory;
        this.bodyFactory = bodyFactory;
        this.headerFactory = headerFactory;
        this.uriFactory = uriFactory;
        this.requestTimeoutMs = requestTimeoutMs;
    }

    @Override
    public <A extends RestAction<?>> RequestBuilder build(A action, String securityToken) throws ActionException {
        Method httpMethod = HTTP_METHOD_TO_REQUEST_BUILDER.get(action.getHttpMethod());
        String url = uriFactory.buildUrl(action);

        RequestBuilder requestBuilder = httpRequestBuilderFactory.create(httpMethod, url);
        requestBuilder.setTimeoutMillis(requestTimeoutMs);

        headerFactory.buildHeaders(requestBuilder, action, securityToken);
        bodyFactory.buildBody(requestBuilder, action);

        return requestBuilder;
    }
}
