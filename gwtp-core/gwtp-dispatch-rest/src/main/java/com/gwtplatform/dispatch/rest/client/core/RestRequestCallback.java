/*
 * Copyright 2016 ArcBees Inc.
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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.gwtplatform.dispatch.rest.client.RestCallback;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

class RestRequestCallback<A extends RestAction<R>, R> implements RequestCallback {
    private final ResponseDeserializer responseDeserializer;
    private final A action;
    private final RestCallback<R> callback;

    private Response response;

    RestRequestCallback(
            ResponseDeserializer responseDeserializer,
            A action,
            RestCallback<R> callback) {
        this.responseDeserializer = responseDeserializer;
        this.action = action;
        this.callback = callback;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        this.response = new ResponseWrapper(response);

        try {
            R result = responseDeserializer.deserialize(action, response);

            onSuccess(result);
        } catch (ActionException e) {
            onFailure(e);
        }

        callback.always(response);
    }

    @Override
    public void onError(Request request, Throwable throwable) {
        onFailure(throwable);

        callback.always(response);
    }

    void onSuccess(R result) {
        callback.onSuccess(result, response);
    }

    void onFailure(Throwable caught) {
        callback.onFailure(caught, response);
    }
}
