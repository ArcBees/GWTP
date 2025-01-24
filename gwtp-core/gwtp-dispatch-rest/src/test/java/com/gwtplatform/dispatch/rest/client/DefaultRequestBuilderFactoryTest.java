/*
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

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.http.client.RequestBuilder;
import com.gwtplatform.dispatch.rest.client.annotations.RequestTimeout;
import com.gwtplatform.dispatch.rest.client.core.DefaultRequestBuilderFactory;
import com.gwtplatform.dispatch.rest.client.core.HttpRequestBuilderFactory;
import com.gwtplatform.dispatch.rest.client.testutils.SecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

import jakarta.inject.Inject;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static com.gwtplatform.dispatch.rest.shared.HttpMethod.GET;

@RunWith(JukitoRunner.class)
public class DefaultRequestBuilderFactoryTest {
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bindConstant().annotatedWith(RequestTimeout.class).to(TIMEOUT);

            forceMock(HttpRequestBuilderFactory.class);
            forceMock(RequestBuilder.class);
        }
    }

    private static final String RELATIVE_PATH = "/rel";
    private static final String SECURITY_TOKEN = "SecurityToken";
    private static final int TIMEOUT = 1000;

    @Inject
    private DefaultRequestBuilderFactory factory;
    @Inject
    private HttpRequestBuilderFactory httpRequestBuilderFactory;
    @Inject
    private RequestBuilder requestBuilder;

    @Test
    public void requestTimeoutShouldBeTheTimeoutProvided() throws ActionException {
        // Given
        given(httpRequestBuilderFactory.create(eq(RequestBuilder.GET), isNull())).willReturn(requestBuilder);

        RestAction<Void> action = new SecuredRestAction(GET, RELATIVE_PATH);

        // When
        factory.build(action, SECURITY_TOKEN);

        // Then
        verify(requestBuilder).setTimeoutMillis(eq(TIMEOUT));
    }
}
