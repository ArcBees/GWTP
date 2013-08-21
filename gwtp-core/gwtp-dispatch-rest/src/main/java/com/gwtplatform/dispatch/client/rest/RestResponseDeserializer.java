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

import javax.inject.Inject;

import org.jboss.errai.enterprise.client.jaxrs.JacksonTransformer;
import org.jboss.errai.marshalling.client.Marshalling;

import com.google.gwt.http.client.Response;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.rest.RestAction;

import static com.gwtplatform.dispatch.shared.rest.MetadataType.KEY_CLASS;
import static com.gwtplatform.dispatch.shared.rest.MetadataType.RESPONSE_CLASS;
import static com.gwtplatform.dispatch.shared.rest.MetadataType.VALUE_CLASS;

public class RestResponseDeserializer {
    private final ActionMetadataProvider metadataProvider;

    @Inject
    RestResponseDeserializer(ActionMetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    public <A extends RestAction<R>, R> R deserialize(A action, Response response) throws ActionException {
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

    private <R> R getDeserializedResponse(RestAction<R> action, Response response) throws ActionException {
        @SuppressWarnings("unchecked")
        Class<R> resultClass = (Class<R>) metadataProvider.getValue(action, RESPONSE_CLASS);
        Class<?> keyClass = (Class<?>) metadataProvider.getValue(action, KEY_CLASS);
        Class<?> valueClass = (Class<?>) metadataProvider.getValue(action, VALUE_CLASS);
        R result = null;

        if (resultClass != Void.class) {
            if (resultClass != null && Marshalling.canHandle(resultClass)) {
                String json = JacksonTransformer.fromJackson(response.getText());

                if (valueClass != null) {
                    result = Marshalling.fromJSON(json, resultClass, keyClass, valueClass);
                } else if (keyClass != null) {
                    result = Marshalling.fromJSON(json, resultClass, keyClass);
                } else {
                    result = Marshalling.fromJSON(json, resultClass);
                }
            } else {
                throw new ActionException("Unable to deserialize response. No serializer found.");
            }
        }

        return result;
    }
}
