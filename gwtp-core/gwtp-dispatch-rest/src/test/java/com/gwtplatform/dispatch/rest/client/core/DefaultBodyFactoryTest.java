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

import java.util.Date;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.http.client.RequestBuilder;
import com.google.inject.multibindings.Multibinder;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.client.serialization.SerializedValue;
import com.gwtplatform.dispatch.rest.client.testutils.ExposedRestAction;
import com.gwtplatform.dispatch.rest.client.testutils.MockHttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.testutils.SecuredRestAction;
import com.gwtplatform.dispatch.rest.client.testutils.UnsecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

import jakarta.inject.Inject;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static com.gwtplatform.dispatch.rest.shared.HttpMethod.DELETE;
import static com.gwtplatform.dispatch.rest.shared.HttpMethod.GET;

@RunWith(JukitoRunner.class)
public class DefaultBodyFactoryTest {
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            forceMock(RequestBuilder.class);

            bind(HttpParameterFactory.class).to(MockHttpParameterFactory.class);

            Serialization serialization = mock(Serialization.class);
            bind(Serialization.class).toInstance(serialization);

            Multibinder.newSetBinder(binder(), Serialization.class).addBinding().toInstance(serialization);
        }
    }

    private static final String RELATIVE_PATH = "/rel";
    private static final String QUERY_STRING = "abc=def&ghi=jkl";

    @Inject
    private DefaultBodyFactory factory;
    @Inject
    private UriFactory uriFactory;
    @Inject
    private HttpParameterFactory parameterFactory;
    @Inject
    private RequestBuilder requestBuilder;

    @Before
    public void setUp() {
        given(uriFactory.buildQueryString(any(RestAction.class), eq(Type.FORM))).willReturn(QUERY_STRING);
    }

    @Test
    public void build_noFormParamsNoBody_setsExplicitNullAsRequestData() throws ActionException {
        // Given
        RestAction<Void> action = new UnsecuredRestAction(parameterFactory, DELETE, RELATIVE_PATH);

        // When
        factory.buildBody(requestBuilder, action);

        // Then
        verify(requestBuilder).setRequestData(isNull(String.class));
        verify(requestBuilder, never()).setHeader(anyString(), anyString());
    }

    @Test
    public void build_formParams_requestContainsOnlyFormParams() throws ActionException {
        // Given
        ExposedRestAction<Void> action = new SecuredRestAction(parameterFactory, GET, RELATIVE_PATH);
        action.addParam(Type.FORM, "Key1", "Some Value");

        // When
        factory.buildBody(requestBuilder, action);

        // Then
        verify(requestBuilder).setRequestData(eq(QUERY_STRING));
        verify(requestBuilder).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Test
    public void build_body_requestContainsSerializedString(Serialization serialization) throws ActionException {
        // Given
        Object unserializedObject = new Object();
        String serializationKey = "meta";
        String serializedData = new Date().toString();
        ContentType contentType = ContentType.valueOf("json-abc");
        SerializedValue serializedValue = new SerializedValue(contentType, serializedData);

        ExposedRestAction<Void> action = new SecuredRestAction(parameterFactory, GET, RELATIVE_PATH);
        action.setBodyParam(unserializedObject);
        action.setBodyClass(serializationKey);

        given(serialization.canSerialize(eq(serializationKey), anyList())).willReturn(true);
        given(serialization.serialize(eq(serializationKey), anyList(), eq(unserializedObject)))
                .willReturn(serializedValue);

        // When
        factory.buildBody(requestBuilder, action);

        // Then
        verify(requestBuilder).setRequestData(eq(serializedData));
        verify(requestBuilder).setHeader(HttpHeaders.CONTENT_TYPE, contentType.toString());
    }

    @Test(expected = ActionException.class)
    public void build_body_noSerializer_throws(Serialization serialization) throws ActionException {
        // Given
        Object unserializedObject = new Object();
        String serializationKey = "meta";

        ExposedRestAction<Void> action = new SecuredRestAction(parameterFactory, GET, RELATIVE_PATH);
        action.setBodyParam(unserializedObject);
        action.setBodyClass(serializationKey);

        given(serialization.canSerialize(eq(serializationKey), anyList())).willReturn(false);

        // When
        factory.buildBody(requestBuilder, action);
    }
}
