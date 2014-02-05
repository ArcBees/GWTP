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

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.github.nmorel.gwtjackson.client.exception.JsonMappingException;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.MetadataType;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestParameter;
import com.gwtplatform.dispatch.shared.ActionException;

import static com.google.gwt.user.client.rpc.RpcRequestBuilder.MODULE_BASE_HEADER;

/**
 * Default implementation for {@link RestRequestBuilderFactory}.
 */
public class DefaultRestRequestBuilderFactory implements RestRequestBuilderFactory {
    private static final Map<HttpMethod, Method> HTTP_METHODS = Maps.newEnumMap(HttpMethod.class);
    private static final String JSON_UTF8 = "application/json; charset=utf-8";

    static {
        HTTP_METHODS.put(HttpMethod.GET, RequestBuilder.GET);
        HTTP_METHODS.put(HttpMethod.POST, RequestBuilder.POST);
        HTTP_METHODS.put(HttpMethod.PUT, RequestBuilder.PUT);
        HTTP_METHODS.put(HttpMethod.DELETE, RequestBuilder.DELETE);
        HTTP_METHODS.put(HttpMethod.HEAD, RequestBuilder.HEAD);
    }

    private final ActionMetadataProvider metadataProvider;
    private final Serialization serialization;
    private final HttpRequestBuilderFactory httpRequestBuilderFactory;
    private final UrlUtils urlUtils;
    private final String baseUrl;
    private final String securityHeaderName;
    private final Integer requestTimeoutMs;

    @Inject
    DefaultRestRequestBuilderFactory(ActionMetadataProvider metadataProvider,
                                     Serialization serialization,
                                     HttpRequestBuilderFactory httpRequestBuilderFactory,
                                     UrlUtils urlUtils,
                                     @RestApplicationPath String baseUrl,
                                     @XsrfHeaderNameRename String securityHeaderName,
                                     @RequestTimeout Integer requestTimeoutMs) {
        this.metadataProvider = metadataProvider;
        this.serialization = serialization;
        this.httpRequestBuilderFactory = httpRequestBuilderFactory;
        this.urlUtils = urlUtils;
        this.baseUrl = baseUrl;
        this.securityHeaderName = securityHeaderName;
        this.requestTimeoutMs = requestTimeoutMs;
    }

    @Override
    public <A extends RestAction<?>> RequestBuilder build(A action, String securityToken) throws ActionException {
        Method httpMethod = HTTP_METHODS.get(action.getHttpMethod());
        String url = buildUrl(action);
        String xsrfToken = action.isSecured() ? securityToken : "";

        RequestBuilder requestBuilder = httpRequestBuilderFactory.create(httpMethod, url);
        requestBuilder.setTimeoutMillis(requestTimeoutMs);

        buildHeaders(requestBuilder, xsrfToken, action.getPath(), action.getHeaderParams());
        buildBody(requestBuilder, action);

        return requestBuilder;
    }

    /**
     * Encodes the given {@link RestParameter} as a path parameter. The default implementation delegates to
     * {@link UrlUtils#encodePathSegment(String)}.
     *
     * @param value a {@link RestParameter} to encode.
     * @return the encoded path parameter.
     * @throws ActionException if an exception occurred while encoding the path parameter.
     * @see #encode(com.gwtplatform.dispatch.rest.shared.RestParameter)
     */
    protected String encodePathParam(RestParameter value) throws ActionException {
        return urlUtils.encodePathSegment(value.getStringValue());
    }

    /**
     * Encodes the given {@link RestParameter} as a query parameter. The default implementation delegates to
     * {@link UrlUtils#encodeQueryString(String)}.
     *
     * @param value a {@link RestParameter} to encode.
     * @return the encoded query parameter.
     * @throws ActionException if an exception occurred while encoding the query parameter.
     * @see #encode(com.gwtplatform.dispatch.rest.shared.RestParameter)
     */
    protected String encodeQueryParam(RestParameter value) throws ActionException {
        return urlUtils.encodeQueryString(value.getStringValue());
    }

    /**
     * Encodes the given {@link RestParameter} as a header parameter. The default implementation returns the string
     * value directly without any encoding.
     *
     * @param value a {@link RestParameter} to encode.
     * @return the encoded header parameter.
     * @throws ActionException if an exception occurred while encoding the header parameter.
     */
    protected String encodeHeaderParam(RestParameter value) throws ActionException {
        return value.getStringValue();
    }

    /**
     * Verify if the provided <code>bodyType</code> can be serialized.
     *
     * @param bodyType the parameterized type to verify if it can be serialized.
     * @return <code>true</code> if <code>bodyType</code> can be serialized, otherwise <code>false</code>.
     */
    protected boolean canSerialize(String bodyType) {
        return serialization.canSerialize(bodyType);
    }

    /**
     * Serialize the given object. We assume {@link #canSerialize(String)} returns <code>true</code> or a runtime
     * exception may be thrown.
     *
     * @param object   the object to serialize.
     * @param bodyType The parameterized type of the object to serialize.
     * @return The serialized string.
     */
    protected String serialize(Object object, String bodyType) {
        return serialization.serialize(object, bodyType);
    }

    private void buildHeaders(RequestBuilder requestBuilder, String xsrfToken, String path,
                              List<RestParameter> customHeaders) throws ActionException {
        requestBuilder.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        if (!isAbsoluteUrl(path)) {
            requestBuilder.setHeader(MODULE_BASE_HEADER, baseUrl);
        }

        if (!Strings.isNullOrEmpty(xsrfToken)) {
            requestBuilder.setHeader(securityHeaderName, xsrfToken);
        }

        for (RestParameter param : customHeaders) {
            requestBuilder.setHeader(param.getName(), encodeHeaderParam(param));
        }

        if (requestBuilder.getHeader(HttpHeaders.CONTENT_TYPE) == null) {
            requestBuilder.setHeader(HttpHeaders.CONTENT_TYPE, JSON_UTF8);
        }
    }

    private <A extends RestAction<?>> void buildBody(RequestBuilder requestBuilder, A action) throws ActionException {
        if (action.hasFormParams()) {
            requestBuilder.setRequestData(buildQueryString(action.getFormParams()));
        } else if (action.hasBodyParam()) {
            requestBuilder.setRequestData(getSerializedValue(action, action.getBodyParam()));
        }
    }

    private String buildUrl(RestAction<?> restAction) throws ActionException {
        String queryString = buildQueryString(restAction.getQueryParams());

        if (!queryString.isEmpty()) {
            queryString = "?" + queryString;
        }

        String path = buildPath(restAction.getPath(), restAction.getPathParams());

        if (isAbsoluteUrl(path)) {
            return path + queryString;
        } else {
            return baseUrl + path + queryString;
        }
    }

    private boolean isAbsoluteUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }

    private String buildPath(String rawPath, List<RestParameter> params) throws ActionException {
        String path = rawPath;

        for (RestParameter param : params) {
            path = path.replace("{" + param.getName() + "}", encodePathParam(param));
        }

        return path;
    }

    private String buildQueryString(List<RestParameter> params) throws ActionException {
        StringBuilder queryString = new StringBuilder();

        for (RestParameter param : params) {
            queryString.append("&")
                       .append(param.getName())
                       .append("=")
                       .append(encodeQueryParam(param));
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
