/*
 * Copyright 2015 ArcBees Inc.
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

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.google.gwt.http.client.RequestBuilder;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.client.serialization.SerializationException;
import com.gwtplatform.dispatch.rest.client.serialization.SerializedValue;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

public class DefaultBodyFactory implements BodyFactory {
    private final Provider<Set<Serialization>> serializationsProvider;
    private final UriFactory uriFactory;

    private Set<Serialization> serializations;

    @Inject
    DefaultBodyFactory(
            Provider<Set<Serialization>> serializationsProvider,
            UriFactory uriFactory) {
        this.serializationsProvider = serializationsProvider;
        this.uriFactory = uriFactory;
    }

    @Override
    public void buildBody(RequestBuilder requestBuilder, RestAction<?> action) throws ActionException {
        if (action.hasFormParams()) {
            assignBodyFromForm(requestBuilder, action);
        } else {
            assignBodyFromSerialization(requestBuilder, action);
        }
    }

    /**
     * Find a serializer capable of handling <code>bodyClass</code> and <code>contentTypes</code>.
     *
     * @param bodyClass the parameterized type to verify if it can be serialized.
     * @param contentTypes A list of content types a serializer is allowed to return.
     *
     * @return <code>true</code> if <code>bodyClass</code> can be serialized, otherwise <code>false</code>.
     */
    protected Serialization findSerialization(String bodyClass, List<ContentType> contentTypes) {
        for (Serialization serialization : getSerializations()) {
            if (serialization.canSerialize(bodyClass, contentTypes)) {
                return serialization;
            }
        }

        return null;
    }

    /**
     * Serialize the given object using the given <code>serialization</code> instance.
     *
     * @param serialization the serialization object to be used.
     * @param object the object to serialize.
     * @param bodyClass The parameterized type of the object to serialize.
     * @param contentTypes A list of content types a serializer is allowed to return.
     *
     * @return The serialized string.
     */
    protected SerializedValue serialize(Serialization serialization, Object object, String bodyClass,
            List<ContentType> contentTypes) throws ActionException {
        try {
            return serialization.serialize(bodyClass, contentTypes, object);
        } catch (SerializationException e) {
            throw new ActionException(e);
        }
    }

    private void assignBodyFromForm(RequestBuilder requestBuilder, RestAction<?> action) {
        String queryString = uriFactory.buildQueryString(action, Type.FORM);

        requestBuilder.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        requestBuilder.setRequestData(queryString);
    }

    private void assignBodyFromSerialization(RequestBuilder requestBuilder, RestAction<?> action)
            throws ActionException {
        String data;
        if (action.hasBodyParam()) {
            SerializedValue serializedValue = getSerializedValue(action, action.getBodyParam());
            ContentType contentType = serializedValue.getContentType();
            data = serializedValue.getData();

            requestBuilder.setHeader(HttpHeaders.CONTENT_TYPE, contentType.toString());
        } else {
            // Fixes an issue for all IE versions (IE 11 is the latest at this time). If request data is not
            // explicitly set to 'null', the JS 'undefined' will be sent as the request body on IE. Other
            // browsers don't send undefined bodies.
            data = null;
        }

        requestBuilder.setRequestData(data);
    }

    private SerializedValue getSerializedValue(RestAction<?> action, Object object) throws ActionException {
        String bodyClass = action.getBodyClass();

        if (bodyClass != null) {
            List<ContentType> contentTypes = action.getClientProducedContentTypes();
            Serialization serialization = findSerialization(bodyClass, contentTypes);

            if (serialization != null) {
                return serialize(serialization, object, bodyClass, contentTypes);
            }
        }

        throw new ActionException("Unable to serialize request body. No serializer found.");
    }

    private Set<Serialization> getSerializations() {
        if (serializations == null) {
            serializations = serializationsProvider.get();
        }

        return serializations;
    }
}
