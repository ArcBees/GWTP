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

package com.gwtplatform.dispatch.client.rest;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.rest.BodyParameter;
import com.gwtplatform.dispatch.shared.rest.HttpMethod;
import com.gwtplatform.dispatch.shared.rest.ResponseParameter;
import com.gwtplatform.dispatch.shared.rest.RestAction;
import com.gwtplatform.dispatch.shared.rest.RestParameter;

/**
 * TODO: Documentation.
 * TODO: Serialization should be handled by a custom ActionHandler that wraps the user handler (SRP)
 */
public class RestDispatchAsync implements DispatchAsync {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final Map<HttpMethod, Method> HTTP_METHODS = new EnumMap<HttpMethod, Method>(HttpMethod.class);

    static {
        HTTP_METHODS.put(HttpMethod.GET, RequestBuilder.GET);
        HTTP_METHODS.put(HttpMethod.POST, RequestBuilder.POST);
        HTTP_METHODS.put(HttpMethod.PUT, RequestBuilder.PUT);
        HTTP_METHODS.put(HttpMethod.DELETE, RequestBuilder.DELETE);
        HTTP_METHODS.put(HttpMethod.HEAD, RequestBuilder.HEAD);
    }

    public static final String JSON_UTF8 = "application/json; charset=utf-8";

    private final SerializerProvider serializerProvider;
    private final String baseUrl;

    public RestDispatchAsync(SerializerProvider serializerProvider, String applicationPath) {
        this.serializerProvider = serializerProvider;
        baseUrl = applicationPath;
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest execute(A action, AsyncCallback<R> callback) {
        if (!(action instanceof RestAction)) {
            // TODO: Any better way?
            throw new IllegalArgumentException("RestDispatchAsync should be used with actions implementing RestAction.");
        }

        return execute((RestAction) action, callback);
    }

    public <A extends RestAction<R>, R extends Result> DispatchRequest execute(final A action,
            final AsyncCallback<R> callback) {
        try {
            RequestBuilder requestBuilder = createRequestBuilder(action);

            requestBuilder.setCallback(new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    onExecuteResponseReceived(action, response, callback);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    onExecuteFailure(callback, exception);
                }
            });

            return new GwtHttpDispatchRequest(requestBuilder.send());
        } catch (RequestException e) {
            onExecuteFailure(callback, e);
        } catch (ActionException e) {
            onExecuteFailure(callback, e);
        }

        return new CompletedDispatchRequest();
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest undo(A action, R result,
            AsyncCallback<Void> callback) {
        throw new UnsupportedOperationException();
    }

    private <A extends RestAction<R>, R extends Result> void onExecuteResponseReceived(A action, Response response,
            AsyncCallback<R> callback) {
        // TODO: Add possibility for other success codes
        if (response.getStatusCode() == Response.SC_OK) {
            onExecuteSuccess(action, callback, response);
        } else {
            // TODO: Add message / wrap RestActionException
            onExecuteFailure(callback, new ActionException(response.getStatusText()));
        }
    }

    private <A extends RestAction<R>, R extends Result> void onExecuteSuccess(A action, AsyncCallback<R> callback,
            Response response) {
        try {
            @SuppressWarnings("unchecked")
            R deserializedReponse = (R) getDeserializedReponse(response.getText(), action.getResponseParam());

            callback.onSuccess(deserializedReponse);
        } catch (ActionException e) {
            callback.onFailure(e);
        }
    }

    private <R extends Result> void onExecuteFailure(AsyncCallback<R> callback,
            Throwable exception) {
        callback.onFailure(exception);
    }

    private <A extends RestAction<?>> RequestBuilder createRequestBuilder(A action) throws ActionException {
        Method httpMethod = HTTP_METHODS.get(action.getHttpMethod());
        String url = buildUrl(action);

        RequestBuilder requestBuilder = new RequestBuilder(httpMethod, url);

        for (RestParameter param : action.getHeaderParams()) {
            requestBuilder.setHeader(param.getName(), getSerializedValue(param));
        }

        requestBuilder.setHeader(CONTENT_TYPE, JSON_UTF8);

        if (action.hasFormParams()) {
            requestBuilder.setRequestData(buildQueryString(action.getFormParams()));
        } else if (action.hasBodyParam()) {
            requestBuilder.setRequestData(getSerializedValue(action.getBodyParam()));
        }

        return requestBuilder;
    }

    private String buildUrl(RestAction<?> restAction) throws ActionException {
        String queryString = buildQueryString(restAction.getQueryParams());

        if (!queryString.isEmpty()) {
            queryString = "?" + queryString;
        }

        String path = buildPath(restAction.getServiceName(), restAction.getPathParams());
        return baseUrl
                + path
                + queryString;
    }

    private String buildPath(String rawPath, List<RestParameter> params) throws ActionException {
        String path = rawPath;

        for (RestParameter param : params) {
            path = path.replace("{" + param.getName() + "}", getSerializedValue(param));
        }

        return path;
    }

    private String buildQueryString(List<RestParameter> params) throws ActionException {
        StringBuilder queryString = new StringBuilder();

        for (RestParameter param : params) {
            queryString.append("&")
                    .append(param.getName())
                    .append("=")
                    .append(getSerializedValue(param));
        }

        if (queryString.length() != 0) {
            queryString.deleteCharAt(0);
        }

        return queryString.toString();
    }

    private String getSerializedValue(RestParameter value) throws ActionException {
        return UriUtils.encode(value.getObject().toString());
    }

    @SuppressWarnings("unchecked")
    private String getSerializedValue(BodyParameter bodyParameter) throws ActionException {
        try {
            Serializer<Serializable> serializer =
                    serializerProvider.<Serializable>getSerializer(bodyParameter.getSerializerId());

            if (serializer == null) {
                throw new ActionException("Unable to serialize request body. Serializer not found.");
            }

            return serializer.serialize(bodyParameter.getObject());
        } catch (SerializationException e) {
            throw new ActionException("Unable to serialize request body.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <R extends Result> R getDeserializedReponse(String text, ResponseParameter responseParameter)
            throws ActionException {
        try {
            Serializer<R> serializer = serializerProvider.<R>getSerializer(responseParameter.getSerializerId());

            if (serializer == null) {
                throw new ActionException("Unable to deserialize response. Serializer not found.");
            }

            return serializer.deserialize(text);
        } catch (SerializationException e) {
            throw new ActionException("Unable to deserialize response.", e);
        }
    }
}
