/*
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

import java.util.Set;

import javax.ws.rs.core.HttpHeaders;

import com.google.gwt.http.client.Response;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.client.serialization.SerializationException;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

/**
 * Default implementation for {@link ResponseDeserializer}.
 */
public class DefaultResponseDeserializer implements ResponseDeserializer {
    private final Provider<Set<Serialization>> serializationsProvider;

    private Set<Serialization> serializations;

    @Inject
    protected DefaultResponseDeserializer(
            Provider<Set<Serialization>> serializationsProvider) {
        this.serializationsProvider = serializationsProvider;
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
     * Find a deserializer capable of handling <code>resultType</code> and <code>contentType</code>.
     *
     * @param resultType the parameterized type to verify if it can be deserialized.
     * @param contentType the contentType of the value that requires deserialization.
     *
     * @return <code>true</code> if <code>resultType</code> can be deserialized, <code>false</code> otherwise.
     */
    protected Serialization findSerialization(String resultType, ContentType contentType) {
        for (Serialization serialization : getSerializations()) {
            if (serialization.canDeserialize(resultType, contentType)) {
                return serialization;
            }
        }

        return null;
    }

    /**
     * Deserializes the data as an object of the <code>resultClass</code> type using the given
     * <code>serialization</code> instance.
     *
     * @param <R> the type of the object once deserialized.
     * @param serialization the serialization object to be used.
     * @param resultClass the parameterized type of the object once deserialized.
     * @param contentType the contentType of <code>data</code>.
     * @param data the data to deserialize.    @return The deserialized object.
     */
    protected <R> R deserializeValue(Serialization serialization, String resultClass, ContentType contentType,
            String data) throws ActionException {
        try {
            return serialization.deserialize(resultClass, contentType, data);
        } catch (SerializationException e) {
            throw new ActionException(e);
        }
    }

    private boolean isSuccessStatusCode(Response response) {
        int statusCode = response.getStatusCode();

        return statusCode >= 200 && statusCode < 300 || statusCode == 304;
    }

    private <R> R getDeserializedResponse(RestAction<R> action, Response response) throws ActionException {
        String resultClass = action.getResultClass();

        if (resultClass != null) {
            ContentType contentType = ContentType.valueOf(response.getHeader(HttpHeaders.CONTENT_TYPE));
            Serialization serialization = findSerialization(resultClass, contentType);

            if (serialization != null) {
                return deserializeValue(serialization, resultClass, contentType, response.getText());
            }
        }

        throw new ActionException("Unable to deserialize response. No serializer found.");
    }

    private Set<Serialization> getSerializations() {
        if (serializations == null) {
            serializations = serializationsProvider.get();
        }

        return serializations;
    }
}
