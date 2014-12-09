/**
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

package com.gwtplatform.dispatch.rest.client;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.github.nmorel.gwtjackson.client.exception.JsonMappingException;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.client.utils.RestParameterBindings;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

import static com.google.gwt.user.client.rpc.RpcRequestBuilder.MODULE_BASE_HEADER;

/**
 * Default implementation for {@link RestRequestBuilderFactory}.
 */
public class DefaultRestRequestBuilderFactory implements RestRequestBuilderFactory {
    private static final Map<HttpMethod, Method> HTTP_METHOD_TO_REQUEST_BUILDER
            = new EnumMap<HttpMethod, Method>(HttpMethod.class);
    private static final String JSON_UTF8 = "application/json; charset=utf-8";

    static {
        HTTP_METHOD_TO_REQUEST_BUILDER.put(HttpMethod.GET, RequestBuilder.GET);
        HTTP_METHOD_TO_REQUEST_BUILDER.put(HttpMethod.POST, RequestBuilder.POST);
        HTTP_METHOD_TO_REQUEST_BUILDER.put(HttpMethod.PUT, RequestBuilder.PUT);
        HTTP_METHOD_TO_REQUEST_BUILDER.put(HttpMethod.DELETE, RequestBuilder.DELETE);
        HTTP_METHOD_TO_REQUEST_BUILDER.put(HttpMethod.HEAD, RequestBuilder.HEAD);
    }

    private final ActionMetadataProvider metadataProvider;
    private final Serialization serialization;
    private final HttpRequestBuilderFactory httpRequestBuilderFactory;
    private final UrlUtils urlUtils;
    private final RestParameterBindings globalHeaderParams;
    private final RestParameterBindings globalQueryParams;
    private final String baseUrl;
    private final String securityHeaderName;
    private final Integer requestTimeoutMs;

    @Inject
    protected DefaultRestRequestBuilderFactory(
            ActionMetadataProvider metadataProvider,
            Serialization serialization,
            HttpRequestBuilderFactory httpRequestBuilderFactory,
            UrlUtils urlUtils,
            @GlobalHeaderParams RestParameterBindings globalHeaderParams,
            @GlobalQueryParams RestParameterBindings globalQueryParams,
            @RestApplicationPath String baseUrl,
            @XsrfHeaderName String securityHeaderName,
            @RequestTimeout Integer requestTimeoutMs) {
        this.metadataProvider = metadataProvider;
        this.serialization = serialization;
        this.httpRequestBuilderFactory = httpRequestBuilderFactory;
        this.urlUtils = urlUtils;
        this.globalHeaderParams = globalHeaderParams;
        this.globalQueryParams = globalQueryParams;
        this.baseUrl = baseUrl;
        this.securityHeaderName = securityHeaderName;
        this.requestTimeoutMs = requestTimeoutMs;
    }

    @Override
    public <A extends RestAction<?>> RequestBuilder build(A action, String securityToken) throws ActionException {
        Method httpMethod = HTTP_METHOD_TO_REQUEST_BUILDER.get(action.getHttpMethod());
        String url = buildUrl(action);
        String xsrfToken = action.isSecured() ? securityToken : "";

        RequestBuilder requestBuilder = httpRequestBuilderFactory.create(httpMethod, url);
        requestBuilder.setTimeoutMillis(requestTimeoutMs);

        buildHeaders(requestBuilder, xsrfToken, action);
        buildBody(requestBuilder, action);

        return requestBuilder;
    }

    /**
     * Verify if the provided <code>bodyType</code> can be serialized.
     *
     * @param bodyType the parameterized type to verify if it can be serialized.
     *
     * @return <code>true</code> if <code>bodyType</code> can be serialized, otherwise <code>false</code>.
     */
    protected boolean canSerialize(String bodyType) {
        return serialization.canSerialize(bodyType);
    }

    /**
     * Serialize the given object. We assume {@link #canSerialize(String)} returns <code>true</code> or a runtime
     * exception may be thrown.
     *
     * @param object the object to serialize.
     * @param bodyType The parameterized type of the object to serialize.
     *
     * @return The serialized string.
     */
    protected String serialize(Object object, String bodyType) {
        return serialization.serialize(object, bodyType);
    }

    private void buildHeaders(RequestBuilder requestBuilder, String xsrfToken, RestAction<?> action) {
        List<HttpParameter> headerParams = getHeaderParameters(xsrfToken, action);

        for (HttpParameter param : headerParams) {
            for (Entry<String, String> entry : param.getEntries(urlUtils)) {
                requestBuilder.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private List<HttpParameter> getHeaderParameters(String xsrfToken, RestAction<?> action) {
        List<HttpParameter> headerParams = new ArrayList<HttpParameter>();

        // By setting the most generic headers first, we make sure they can be overridden by more specific ones
        headerParams.add(new HttpParameter(Type.HEADER, HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON));
        headerParams.add(new HttpParameter(Type.HEADER, HttpHeaders.CONTENT_TYPE, JSON_UTF8));
        headerParams.add(new HttpParameter(Type.HEADER, HttpHeaders.CONTENT_TYPE, JSON_UTF8));

        if (!isAbsoluteUrl(action.getPath())) {
            headerParams.add(new HttpParameter(Type.HEADER, MODULE_BASE_HEADER, baseUrl));
        }
        if (xsrfToken != null && !xsrfToken.isEmpty()) {
            headerParams.add(new HttpParameter(Type.HEADER, securityHeaderName, xsrfToken));
        }

        headerParams.addAll(globalHeaderParams.get(action.getHttpMethod()));
        headerParams.addAll(action.getParameters(Type.HEADER));

        return headerParams;
    }

    private <A extends RestAction<?>> void buildBody(RequestBuilder requestBuilder, A action) throws ActionException {
        if (action.hasFormParams()) {
            requestBuilder.setRequestData(buildQueryString(action.getParameters(Type.FORM)));
        } else if (action.hasBodyParam()) {
            requestBuilder.setRequestData(getSerializedValue(action, action.getBodyParam()));
        } else {
            // Fixes an issue for all IE versions (IE 11 is the latest at this time). If request data is not explicitly
            // set to 'null', the JS 'undefined' will be sent as the request body on IE. Other browsers don't send
            // undefined bodies.
            requestBuilder.setRequestData(null);
        }
    }

    private String buildUrl(RestAction<?> action) {
        List<HttpParameter> queryParams = new ArrayList<HttpParameter>();
        queryParams.addAll(globalQueryParams.get(action.getHttpMethod()));
        queryParams.addAll(action.getParameters(Type.QUERY));

        String queryString = buildQueryString(queryParams);
        if (!queryString.isEmpty()) {
            queryString = "?" + queryString;
        }

        String path = buildPath(action.getPath(), action.getParameters(Type.PATH));

        String prefix = "";
        if (!isAbsoluteUrl(path)) {
            prefix = baseUrl;
        }

        return prefix + path + queryString;
    }

    private boolean isAbsoluteUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }

    private String buildPath(String rawPath, List<HttpParameter> params) {
        String path = rawPath;

        for (HttpParameter param : params) {
            List<Entry<String, String>> entries = param.getEntries(urlUtils);
            assert entries.size() <= 1;

            Entry<String, String> entry = entries.get(0);
            path = path.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return path;
    }

    private String buildQueryString(List<HttpParameter> parameters) {
        StringBuilder queryString = new StringBuilder();

        for (HttpParameter parameter : parameters) {
            for (Entry<String, String> entry : parameter.getEntries(urlUtils)) {
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

    private String getSerializedValue(RestAction<?> action, Object object) throws ActionException {
        String bodyType = (String) metadataProvider.getValue(action, MetadataType.BODY_TYPE);

        if (bodyType != null && canSerialize(bodyType)) {
            try {
                return serialize(object, bodyType);
            } catch (JsonMappingException e) {
                throw new ActionException("Unable to serialize request body. An unexpected error occurred.", e);
            }
        }

        throw new ActionException("Unable to serialize request body. No serializer found.");
    }
}
