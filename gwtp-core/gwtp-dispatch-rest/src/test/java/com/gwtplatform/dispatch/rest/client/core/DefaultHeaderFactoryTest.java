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

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import com.google.gwt.http.client.RequestBuilder;
import com.google.inject.Provides;
import com.gwtplatform.dispatch.rest.client.RestApplicationPath;
import com.gwtplatform.dispatch.rest.client.annotations.GlobalHeaderParams;
import com.gwtplatform.dispatch.rest.client.annotations.XsrfHeaderName;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.gin.RestParameterBindings;
import com.gwtplatform.dispatch.rest.client.testutils.ExposedRestAction;
import com.gwtplatform.dispatch.rest.client.testutils.MockHttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.testutils.SecuredRestAction;
import com.gwtplatform.dispatch.rest.client.testutils.UnsecuredRestAction;
import com.gwtplatform.dispatch.rest.rebind.utils.Arrays;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static com.gwtplatform.dispatch.rest.shared.HttpMethod.GET;
import static com.gwtplatform.dispatch.rest.shared.HttpMethod.POST;

@RunWith(JukitoRunner.class)
public class DefaultHeaderFactoryTest {
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bindConstant().annotatedWith(RestApplicationPath.class).to(APPLICATION_PATH);
            bindConstant().annotatedWith(XsrfHeaderName.class).to(XSRF_HEADER_NAME);

            forceMock(RequestBuilder.class);

            bind(HttpParameterFactory.class).to(MockHttpParameterFactory.class);
        }

        @Provides
        @TestSingleton
        @GlobalHeaderParams
        RestParameterBindings getHeaderParams(HttpParameterFactory parameterFactory) {
            RestParameterBindings headers = new RestParameterBindings();
            headers.put(GET, parameterFactory.create(Type.HEADER, KEY_1, VALUE_1));
            headers.put(GET, parameterFactory.create(Type.HEADER, KEY_2, VALUE_2));
            headers.put(POST, parameterFactory.create(Type.HEADER, KEY_3, VALUE_3));

            return headers;
        }
    }

    private static final String KEY_1 = "key1";
    private static final String KEY_2 = "key2";
    private static final String KEY_3 = "key3";
    private static final String VALUE_1 = "Value1";
    private static final String VALUE_2 = "Value 2";
    private static final String VALUE_3 = "Value 3";
    private static final String ACTION_KEY_1 = "action" + KEY_1;
    private static final String ACTION_KEY_2 = "action" + KEY_2;
    private static final String APPLICATION_PATH = "/api";
    private static final String RELATIVE_PATH = "/rel";
    private static final String XSRF_HEADER_NAME = "My-Header";
    private static final String SECURITY_TOKEN = "SecurityToken";

    @Inject
    private DefaultHeaderFactory factory;
    @Inject
    private HttpParameterFactory parameterFactory;
    @Inject
    private RequestBuilder requestBuilder;

    @Test
    public void build_securityToken_securityHeaderIsSet() {
        // Given
        RestAction<Void> action = new SecuredRestAction(parameterFactory, GET, RELATIVE_PATH);

        // When
        factory.buildHeaders(requestBuilder, action, SECURITY_TOKEN);

        // Then
        verify(requestBuilder).setHeader(eq(XSRF_HEADER_NAME), eq(SECURITY_TOKEN));
    }

    @Test
    public void build_emptySecurityToken_securityHeaderIsNotSet() {
        // Given
        RestAction<Void> action = new SecuredRestAction(parameterFactory, GET, RELATIVE_PATH);

        // When
        factory.buildHeaders(requestBuilder, action, "");

        // Then
        verify(requestBuilder, never()).setHeader(eq(XSRF_HEADER_NAME), anyString());
    }

    @Test
    public void build_actionNotSecured_securityHeaderIsNotSet() {
        // Given
        RestAction<Void> action = new UnsecuredRestAction(parameterFactory, GET, RELATIVE_PATH);

        // When
        factory.buildHeaders(requestBuilder, action, SECURITY_TOKEN);

        // Then
        verify(requestBuilder, never()).setHeader(eq(XSRF_HEADER_NAME), anyString());
    }

    @Test
    public void build_globalHeadersWithHttpMethod_areSetForSameHttpMethod() {
        // Given
        RestAction<Void> action = new SecuredRestAction(parameterFactory, GET, RELATIVE_PATH);

        // When
        factory.buildHeaders(requestBuilder, action, SECURITY_TOKEN);

        // Then
        verify(requestBuilder).setHeader(eq(KEY_1), eq(VALUE_1));
        verify(requestBuilder).setHeader(eq(KEY_2), eq(VALUE_2));
        verify(requestBuilder, never()).setHeader(eq(KEY_3), eq(VALUE_3));
    }

    @Test
    public void build_actionHeaders_areSet() {
        // Given
        RestAction<Void> action = createActionWithHeaderParams();

        // When
        factory.buildHeaders(requestBuilder, action, SECURITY_TOKEN);

        // Then
        verify(requestBuilder).setHeader(eq(ACTION_KEY_1), eq(VALUE_1));
        verify(requestBuilder).setHeader(eq(ACTION_KEY_2), eq(VALUE_2));
    }

    @Test
    public void build_actionHeaderAreSetAfterGlobalHeaders() {
        // Given
        RestAction<Void> action = createActionWithHeaderParams();

        // When
        factory.buildHeaders(requestBuilder, action, SECURITY_TOKEN);

        // Then
        InOrder inOrder = inOrder(requestBuilder);
        inOrder.verify(requestBuilder).setHeader(eq(KEY_1), anyString());
        inOrder.verify(requestBuilder).setHeader(eq(KEY_2), anyString());
        inOrder.verify(requestBuilder).setHeader(eq(ACTION_KEY_1), anyString());
        inOrder.verify(requestBuilder).setHeader(eq(ACTION_KEY_2), anyString());
    }

    @Test
    public void build_acceptContentTypes_acceptHeaderIsSet() {
        // given
        UnsecuredRestAction action = new UnsecuredRestAction(parameterFactory, GET, RELATIVE_PATH);
        action.setClientConsumedContentTypes(Arrays.asList(ContentType.valueOf("a"), ContentType.valueOf("b")));

        // when
        factory.buildHeaders(requestBuilder, action, SECURITY_TOKEN);

        // then
        verify(requestBuilder).setHeader(eq(HttpHeaders.ACCEPT), eq("a/*,b/*"));
    }

    private RestAction<Void> createActionWithHeaderParams() {
        ExposedRestAction<Void> action = new SecuredRestAction(parameterFactory, GET, RELATIVE_PATH);
        action.addParam(Type.HEADER, ACTION_KEY_1, VALUE_1);
        action.addParam(Type.HEADER, ACTION_KEY_2, VALUE_2);

        return action;
    }
}
