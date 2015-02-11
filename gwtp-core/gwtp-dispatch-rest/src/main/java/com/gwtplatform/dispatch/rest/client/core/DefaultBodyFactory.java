/**
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

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import com.google.gwt.http.client.RequestBuilder;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.client.serialization.SerializationException;
import com.gwtplatform.dispatch.rest.client.serialization.SerializedValue;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

public class DefaultBodyFactory implements BodyFactory {
    private final Serialization serialization;
    private final UriFactory uriFactory;

    @Inject
    DefaultBodyFactory(
            Serialization serialization,
            UriFactory uriFactory) {
        this.serialization = serialization;
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
     * Verify if the provided <code>bodyClass</code> can be serialized.
     *
     * @param bodyClass the parameterized type to verify if it can be serialized.
     * @param contentTypes A list of content types a serializer is allowed to return.
     *
     * @return <code>true</code> if <code>bodyClass</code> can be serialized, otherwise <code>false</code>.
     */
    protected boolean canSerialize(String bodyClass, List<String> contentTypes) {
        // TODO: Loop over all deserializers
        return serialization.canSerialize(bodyClass, contentTypes);
    }

    /**
     * Serialize the given object. We assume {@link #canSerialize(String, java.util.List)} returns <code>true</code> or
     * a runtime exception may be thrown.
     *
     * @param object the object to serialize.
     * @param bodyClass The parameterized type of the object to serialize.
     * @param contentTypes A list of content types a serializer is allowed to return.
     *
     * @return The serialized string.
     */
    protected SerializedValue serialize(Object object, String bodyClass, List<String> contentTypes) {
        // TODO: Loop over all deserializers
        return serialization.serialize(bodyClass, contentTypes, object);
    }

    private void assignBodyFromForm(RequestBuilder requestBuilder, RestAction<?> action) {
        String queryString = uriFactory.buildQueryString(action, Type.FORM);

        // TODO: Set Header
        requestBuilder.setRequestData(queryString);
    }

    private void assignBodyFromSerialization(RequestBuilder requestBuilder, RestAction<?> action)
            throws ActionException {
        String data;
        if (action.hasBodyParam()) {
            SerializedValue serializedValue = getSerializedValue(action, action.getBodyParam());
            data = serializedValue.getData();

            requestBuilder.setHeader(HttpHeaders.CONTENT_TYPE, serializedValue.getContentType());
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
        List<String> contentTypes = action.getClientProducedContentTypes();

        if (bodyClass != null && canSerialize(bodyClass, contentTypes)) {
            try {
                return serialize(object, bodyClass, contentTypes);
            } catch (SerializationException e) {
                throw new ActionException(e);
            }
        }

        throw new ActionException("Unable to serialize request body. No serializer found.");
    }
}
