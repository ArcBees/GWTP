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

package com.gwtplatform.dispatch.client.rest;

import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.SerializationException;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.dispatch.shared.rest.RestAction;

import static com.gwtplatform.dispatch.client.rest.SerializedType.RESPONSE;

public class RestResponseDeserializer {
    private final SerializerProvider serializerProvider;

    public RestResponseDeserializer(SerializerProvider serializerProvider) {
        this.serializerProvider = serializerProvider;
    }

    public <A extends RestAction<R>, R extends Result> R deserialize(A action, Response response)
            throws ActionException {
        if (isSuccessStatusCode(response)) {
            return getDeserializedResponse(action, response);
        } else {
            throw new ActionException(response.getStatusText());
        }
    }

    private boolean isSuccessStatusCode(Response response) {
        int statusCode = response.getStatusCode();

        return (statusCode >= 200 && statusCode < 300) || statusCode == 304;
    }

    private <R extends Result> R getDeserializedResponse(Action<R> action, Response response) throws ActionException {
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
