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
import com.gwtplatform.dispatch.client.AbstractDispatchAsync;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;
import com.gwtplatform.dispatch.shared.rest.HttpMethod;
import com.gwtplatform.dispatch.shared.rest.RestAction;
import com.gwtplatform.dispatch.shared.rest.RestParameter;

import static com.gwtplatform.dispatch.client.rest.SerializedType.BODY;
import static com.gwtplatform.dispatch.client.rest.SerializedType.RESPONSE;

/**
 * TODO: Documentation.
 * TODO: Serialization should be handled by a custom ActionHandler that wraps the user handler (SRP)
 */
public class RestDispatchAsync extends AbstractDispatchAsync {
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
    private final String securityHeaderName;

    public RestDispatchAsync(ExceptionHandler exceptionHandler,
            SecurityCookieAccessor securityCookieAccessor,
            ClientActionHandlerRegistry clientActionHandlerRegistry,
            SerializerProvider serializerProvider,
            String applicationPath,
            String securityHeaderName) {
        super(exceptionHandler, securityCookieAccessor, clientActionHandlerRegistry);

        this.serializerProvider = serializerProvider;
        baseUrl = applicationPath;
        this.securityHeaderName = securityHeaderName;
    }

    @Override
    protected <A extends Action<R>, R extends Result> DispatchRequest doExecute(String securityCookie, A action,
            final AsyncCallback<R> callback) {
        if (!(action instanceof RestAction)) {
            throw new IllegalArgumentException("RestDispatchAsync should be used with actions implementing " +
                    "RestAction.");
        }

        final RestAction<R> restAction = (RestAction<R>) action;

        try {
            RequestBuilder requestBuilder = createRequestBuilder(restAction, securityCookie);

            requestBuilder.setCallback(new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    onExecuteResponseReceived(restAction, response, callback);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    onExecuteFailure(restAction, exception, callback);
                }
            });

            return new GwtHttpDispatchRequest(requestBuilder.send());
        } catch (RequestException e) {
            onExecuteFailure(restAction, e, callback);
        } catch (ActionException e) {
            onExecuteFailure(restAction, e, callback);
        }

        return new CompletedDispatchRequest();
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest undo(A action, R result,
            AsyncCallback<Void> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected <A extends Action<R>, R extends Result> DispatchRequest doUndo(String securityCookie, A action,
            R result, AsyncCallback<Void> callback) {
        return null;
    }

    private <A extends RestAction<R>, R extends Result> void onExecuteResponseReceived(A action, Response response,
            AsyncCallback<R> callback) {
        int statusCode = response.getStatusCode();
        // TODO normalize 1223 to 204
        if ((statusCode >= 200 && statusCode < 300) || statusCode == 304 || statusCode == 1223) {
            try {
                R deserializedReponse = getDeserializedReponse(action, response);

                onExecuteSuccess(action, deserializedReponse, callback);
            } catch (ActionException e) {
                onExecuteFailure(action, e, callback);
            }
        } else {
            onExecuteFailure(action,  new ActionException(response.getStatusText()), callback);
        }
    }

    private <A extends RestAction<?>> RequestBuilder createRequestBuilder(A action,
            String securityToken) throws ActionException {
        Method httpMethod = HTTP_METHODS.get(action.getHttpMethod());
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
        return UriUtils.encode(value.getObject().toString());
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

    private <R extends Result> R getDeserializedReponse(Action<R> action, Response response)
            throws ActionException {
        try {
            Serializer<R> serializer = serializerProvider.getSerializer(action.getClass(), RESPONSE);

            if (serializer == null) {
                throw new ActionException("Unable to deserialize response. Serializer not found.");
            }

            return serializer.deserialize(response.getText());
        } catch (SerializationException e) {
            throw new ActionException("Unable to deserialize response.", e);
        }
    }
}
