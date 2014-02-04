/**
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client;

import java.util.UUID;

import javax.inject.Inject;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static com.gwtplatform.dispatch.rest.shared.HttpMethod.GET;
import static com.gwtplatform.dispatch.rest.shared.MetadataType.BODY_TYPE;

@RunWith(JukitoRunner.class)
public class DefaultRestRequestBuilderFactoryTest {
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bindConstant().annotatedWith(RestApplicationPath.class).to(APPLICATION_PATH);
            bindConstant().annotatedWith(XCSRFHeaderName.class).to(XSRF_HEADER_NAME);
            bindConstant().annotatedWith(RequestTimeout.class).to(TIMEOUT);

            forceMock(HttpRequestBuilderFactory.class);
        }
    }

    private static final String APPLICATION_PATH = "/api";
    private static final String ABSOLUTE_PATH = "http://www.arcbees.com/";
    private static final String RELATIVE_PATH = "/rel";
    private static final String XSRF_HEADER_NAME = "My-Header";
    private static final String SECURITY_TOKEN = "SecurityToken";
    private static final int TIMEOUT = 1000;

    private static final String DECODED_VALUE_1 = "Value1";
    private static final String ENCODED_VALUE_1 = "Value1";

    private static final String DECODED_VALUE_2 = "Value 2";
    private static final String ENCODED_VALUE_2 = "Value%201";

    private static final String DECODED_VALUE_3 = "Value 3";
    private static final String ENCODED_VALUE_3 = "Value%2B3";

    @Inject
    private DefaultRestRequestBuilderFactory factory;
    @Inject
    private HttpRequestBuilderFactory httpRequestBuilderFactory;

    private RequestBuilder requestBuilder;

    @Before
    public void setUp(HttpRequestBuilderFactory httpRequestBuilderFactory, UrlUtils urlUtils) {
        requestBuilder = mock(RequestBuilder.class);

        given(httpRequestBuilderFactory.create(any(Method.class), anyString())).willReturn(requestBuilder);

        given(urlUtils.encodeQueryString(DECODED_VALUE_1)).willReturn(ENCODED_VALUE_1);
        given(urlUtils.encodeQueryString(DECODED_VALUE_2)).willReturn(ENCODED_VALUE_2);
        given(urlUtils.encodeQueryString(DECODED_VALUE_3)).willReturn(ENCODED_VALUE_3);
    }

    @Test
    public void absoluteUrlShouldNotChange() throws ActionException {
        // Given
        RestAction<Void> action = new UnsecuredRestAction(GET, ABSOLUTE_PATH);

        // When
        factory.build(action, SECURITY_TOKEN);

        // Then
        verify(httpRequestBuilderFactory).create(eq(RequestBuilder.GET), eq(ABSOLUTE_PATH));
    }

    @Test
    public void relativeUrlShouldBePrependedByApplicationPath() throws ActionException {
        // Given
        RestAction<Void> action = new UnsecuredRestAction(GET, RELATIVE_PATH);

        // When
        factory.build(action, SECURITY_TOKEN);

        // Then
        verify(httpRequestBuilderFactory).create(eq(RequestBuilder.GET), eq(APPLICATION_PATH + RELATIVE_PATH));
    }

    @Test
    public void securityHeaderShouldBeSetWhenTokenIsNotEmpty() throws ActionException {
        // Given
        RestAction<Void> action = new SecuredRestAction(GET, RELATIVE_PATH);

        // When
        factory.build(action, SECURITY_TOKEN);

        // Then
        verify(requestBuilder).setHeader(eq(XSRF_HEADER_NAME), eq(SECURITY_TOKEN));
    }

    @Test
    public void securityHeaderShouldNotBeSetWhenSecurityTokenIsEmpty() throws ActionException {
        // Given
        RestAction<Void> action = new SecuredRestAction(GET, RELATIVE_PATH);

        // When
        factory.build(action, "");

        // Then
        verify(requestBuilder, never()).setHeader(eq(XSRF_HEADER_NAME), anyString());
    }

    @Test
    public void securityHeaderShouldNotBeSetWhenActionIsNotSecured() throws ActionException {
        // Given
        RestAction<Void> action = new UnsecuredRestAction(GET, RELATIVE_PATH);

        // When
        factory.build(action, SECURITY_TOKEN);

        // Then
        verify(requestBuilder, never()).setHeader(eq(XSRF_HEADER_NAME), anyString());
    }

    @Test
    public void requestTimeoutShouldBeTheTimeoutProvided() throws ActionException {
        // Given
        RestAction<Void> action = new SecuredRestAction(GET, RELATIVE_PATH);

        // When
        factory.build(action, SECURITY_TOKEN);

        // Then
        verify(requestBuilder).setTimeoutMillis(eq(TIMEOUT));
    }

    @Test
    public void requestDataShouldBeEncodedQueryStringWhenActionHasFormParams() throws ActionException {
        // Given
        ExposedRestAction<Void> action = new SecuredRestAction(GET, RELATIVE_PATH);
        action.addFormParam("Key1", DECODED_VALUE_1);
        action.addFormParam("Key2", DECODED_VALUE_2);
        action.addFormParam("Key3", DECODED_VALUE_3);

        // When
        factory.build(action, SECURITY_TOKEN);

        // Then
        String expectedRequestData = "Key1=" + ENCODED_VALUE_1
                                     + "&Key2=" + ENCODED_VALUE_2
                                     + "&Key3=" + ENCODED_VALUE_3;
        verify(requestBuilder).setRequestData(eq(expectedRequestData));
    }

    @Test
    public void requestDataShouldBeSerializedStringWhenActionHasBody(Serialization serialization,
                                                                     ActionMetadataProvider metadataProvider)
            throws ActionException {
        // Given
        Object unserializedObject = new Object();
        String serializationKey = "meta";
        String serializedValue = UUID.randomUUID().toString();

        ExposedRestAction<Void> action = new SecuredRestAction(GET, RELATIVE_PATH);
        action.setBodyParam(unserializedObject);

        given(metadataProvider.getValue(action, BODY_TYPE)).willReturn(serializationKey);
        given(serialization.canSerialize(serializationKey)).willReturn(true);
        given(serialization.serialize(unserializedObject, serializationKey)).willReturn(serializedValue);

        // When
        factory.build(action, SECURITY_TOKEN);

        // Then
        verify(requestBuilder).setRequestData(eq(serializedValue));
    }
}
