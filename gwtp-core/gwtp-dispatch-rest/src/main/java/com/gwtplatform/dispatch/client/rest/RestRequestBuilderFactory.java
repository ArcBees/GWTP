package com.gwtplatform.dispatch.client.rest;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.rpc.SerializationException;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.rest.HttpMethod;
import com.gwtplatform.dispatch.shared.rest.RestAction;
import com.gwtplatform.dispatch.shared.rest.RestParameter;

import static com.gwtplatform.dispatch.client.rest.SerializedType.BODY;

class RestRequestBuilderFactory {
    private static final Map<HttpMethod, RequestBuilder.Method> HTTP_METHODS = new EnumMap<HttpMethod, RequestBuilder.Method>(HttpMethod.class);
    private static final String CONTENT_TYPE = "Content-Type";

    public static final String JSON_UTF8 = "application/json; charset=utf-8";

    static {
        HTTP_METHODS.put(HttpMethod.GET, RequestBuilder.GET);
        HTTP_METHODS.put(HttpMethod.POST, RequestBuilder.POST);
        HTTP_METHODS.put(HttpMethod.PUT, RequestBuilder.PUT);
        HTTP_METHODS.put(HttpMethod.DELETE, RequestBuilder.DELETE);
        HTTP_METHODS.put(HttpMethod.HEAD, RequestBuilder.HEAD);
    }

    private final SerializerProvider serializerProvider;
    private final String baseUrl;
    private final String securityHeaderName;

    RestRequestBuilderFactory(SerializerProvider serializerProvider, String baseUrl, String securityHeaderName) {
        this.serializerProvider = serializerProvider;
        this.baseUrl = baseUrl;
        this.securityHeaderName = securityHeaderName;
    }

    public <A extends RestAction<?>> RequestBuilder build(A action, String securityToken) throws ActionException {
        RequestBuilder.Method httpMethod = HTTP_METHODS.get(action.getHttpMethod());
        String url = buildUrl(action);

        RequestBuilder requestBuilder = new RequestBuilder(httpMethod, url);

        for (RestParameter param : action.getHeaderParams()) {
            requestBuilder.setHeader(param.getName(), encode(param));
        }

        requestBuilder.setHeader(CONTENT_TYPE, JSON_UTF8);

        if (action.hasFormParams()) {
            requestBuilder.setRequestData(buildQueryString(action.getFormParams()));
        } else if (action.hasBodyParam()) {
            requestBuilder.setRequestData(getSerializedValue(action, action.getBodyParam()));
        }

        if (securityToken != null && securityToken.length() > 0) {
            requestBuilder.setHeader(securityHeaderName, securityToken);
        }

        return requestBuilder;
    }

    private String buildUrl(RestAction<?> restAction) throws ActionException {
        String queryString = buildQueryString(restAction.getQueryParams());

        if (!queryString.isEmpty()) {
            queryString = "?" + queryString;
        }

        String path = buildPath(restAction.getServiceName(), restAction.getPathParams());

        return baseUrl + path + queryString;
    }

    private String buildPath(String rawPath, List<RestParameter> params) throws ActionException {
        String path = rawPath;

        for (RestParameter param : params) {
            path = path.replace("{" + param.getName() + "}", encode(param));
        }

        return path;
    }

    private String buildQueryString(List<RestParameter> params) throws ActionException {
        StringBuilder queryString = new StringBuilder();

        for (RestParameter param : params) {
            queryString.append("&")
                    .append(param.getName())
                    .append("=")
                    .append(encode(param));
        }

        if (queryString.length() != 0) {
            queryString.deleteCharAt(0);
        }

        return queryString.toString();
    }

    private String encode(RestParameter value) throws ActionException {
        return UriUtils.encode(value.getStringValue());
    }

    private String getSerializedValue(Action<?> action, Object object) throws ActionException {
        try {
            Serializer<Object> serializer = serializerProvider.getSerializer(action.getClass(), BODY);

            if (serializer == null) {
                throw new ActionException("Unable to serialize request body. Serializer not found.");
            }

            return serializer.serialize(object);
        } catch (SerializationException e) {
            throw new ActionException("Unable to serialize request body.", e);
        }
    }
}
