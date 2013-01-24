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
import java.util.Map;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.client.GwtHttpDispatchRequest;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.HttpMethod;
import com.gwtplatform.dispatch.shared.Result;

/**
 * TODO: Documentation
 */
public class RestDispatchAsync implements DispatchAsync {
    private static final Map<HttpMethod, Method> httpMethods = new EnumMap<HttpMethod, Method>(HttpMethod.class);
    private static final String CONTENT_TYPE = "Content-Type";

    static {
        httpMethods.put(HttpMethod.GET, RequestBuilder.GET);
        httpMethods.put(HttpMethod.POST, RequestBuilder.POST);
        httpMethods.put(HttpMethod.PUT, RequestBuilder.PUT);
        httpMethods.put(HttpMethod.DELETE, RequestBuilder.DELETE);
        httpMethods.put(HttpMethod.HEAD, RequestBuilder.HEAD);
    }

    private final String baseUrl;

    public RestDispatchAsync(String applicationPath) {
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
        RequestBuilder requestBuilder = createRequestBuilder(action);

        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                // TODO: Add possibility for other success codes
                if (response.getStatusCode() == Response.SC_OK) {
                    onExecuteSuccess(action, callback, response);
                } else {
                    // TODO: Add message / wrap RestActionException
                    onExecuteFailure(action, callback, new ActionException(response.getStatusText()));
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                onExecuteFailure(action, callback, exception);
            }
        });

        try {
            return new GwtHttpDispatchRequest(requestBuilder.send());
        } catch (RequestException e) {
            onExecuteFailure(action, callback, e);
        }

        return new CompletedDispatchRequest();
    }

    private <A extends RestAction<R>, R extends Result> void onExecuteSuccess(A action, AsyncCallback<R> callback,
            Response response) {
        callback.onSuccess((R) getDeserializedReponse(response.getText()));
    }

    private <A extends RestAction<R>, R extends Result> void onExecuteFailure(A action, AsyncCallback<R> callback,
            Throwable exception) {
        callback.onFailure(exception);
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest undo(A action, R result,
            AsyncCallback<Void> callback) {
        throw new UnsupportedOperationException();
    }

    private <A extends RestAction<?>> RequestBuilder createRequestBuilder(A action) {
        Method httpMethod = httpMethods.get(action.getHttpMethod());
        String url = buildUrl(action);

        RequestBuilder requestBuilder = new RequestBuilder(httpMethod, url);

        for (Map.Entry<String, Object> param : action.getHeaderParams().entrySet()) {
            // TODO: header param should be a string?
            requestBuilder.setHeader(param.getKey(), getSerializedValue(param.getValue()));
        }

        // TODO: use @Produces
        requestBuilder.setHeader(CONTENT_TYPE, "application/json; charset=utf-8");

        requestBuilder.setRequestData(getSerializedContent(action.getFormParams()));

        return requestBuilder;
    }

    private String buildUrl(RestAction<?> restAction) {
        return baseUrl
                + buildPath(restAction.getServiceName(), restAction.getPathParams())
                + buildQueryString(restAction.getQueryParams());
    }

    private String buildPath(String rawPath, Map<String, Object> params) {
        String path = rawPath;

        for (Map.Entry<String, Object> param : params.entrySet()) {
            path = path.replace(param.getKey(), getSerializedValue(param.getValue()));
        }

        return path;
    }

    private String buildQueryString(Map<String, Object> params) {
        StringBuilder queryString = new StringBuilder();

        for (Map.Entry<String, Object> param : params.entrySet()) {
            queryString.append("&")
                    .append(param.getKey())
                    .append("=")
                    .append(getSerializedValue(param.getValue()));
        }

        if (queryString.length() != 0) {
            queryString.replace(0, 1, "?");
        }

        return queryString.toString();
    }

    private String getSerializedContent(Map<String, Object> params) {
        // TODO: add piriti and use the corresponding writer

        return params.toString();
    }

    private String getSerializedValue(Object object) {
        // TODO: add piriti and use the corresponding writer

        return object.toString();
    }

    private <R extends Result> R getDeserializedReponse(String text) {
        // TODO: use @Consumes
        // TODO: add piriti and use the corresponding reader

        return null;
    }
}
