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

package com.gwtplatform.dispatch.rest.client.core;

import javax.inject.Inject;

import com.github.nmorel.gwtjackson.client.exception.JsonMappingException;
import com.google.gwt.http.client.Response;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Default implementation for {@link ResponseDeserializer}.
 */
public class DefaultResponseDeserializer implements ResponseDeserializer {
    private final Serialization serialization;

    @Inject
    protected DefaultResponseDeserializer(
            Serialization serialization) {
        this.serialization = serialization;
    }

    @Override
    public <A extends RestAction<R>, R> R deserialize(A action, Response response) throws ActionException {
        if (isSuccessStatusCode(response)) {
            return getDeserializedResponse(action, response);
        } else {
            throw new ActionException(response.getStatusText());
        }
    }

    /**
     * Verify if the provided <code>resultType</code> can be deserialized.
     *
     * @param resultType the parameterized type to verify if it can be deserialized.
     *
     * @return <code>true</code> if <code>resultType</code> can be deserialized, <code>false</code> otherwise.
     */
    protected boolean canDeserialize(String resultType) {
        return serialization.canDeserialize(resultType);
    }

    /**
     * Deserializes the json as an object of the <code>resultClass</code> type.
     *
     * @param resultClass the parameterized type of the object once deserialized.
     * @param json the json to deserialize.
     * @param <R> the type of the object once deserialized
     *
     * @return The deserialized object.
     */
    protected <R> R deserializeValue(String resultClass, String json) {
        return serialization.deserialize(json, resultClass);
    }

    private boolean isSuccessStatusCode(Response response) {
        int statusCode = response.getStatusCode();

        return (statusCode >= 200 && statusCode < 300) || statusCode == 304;
    }

    private <R> R getDeserializedResponse(RestAction<R> action, Response response) throws ActionException {
        String resultClass = action.getResultClass();

        if (resultClass != null && canDeserialize(resultClass)) {
            try {
                String json = response.getText();
                return deserializeValue(resultClass, json);
            } catch (JsonMappingException e) {
                throw new ActionException("Unable to deserialize response. An unexpected error occurred.", e);
            }
        }

        throw new ActionException("Unable to deserialize response. No serializer found.");
    }
}
