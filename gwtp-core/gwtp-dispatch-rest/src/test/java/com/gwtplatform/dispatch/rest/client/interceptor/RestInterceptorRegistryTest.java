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

package com.gwtplatform.dispatch.rest.client.interceptor;

import org.junit.Before;
import org.junit.Test;

import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.client.context.RestContext;
import com.gwtplatform.dispatch.rest.client.context.RestContext.Builder;
import com.gwtplatform.dispatch.rest.client.core.parameters.QueryParameter;
import com.gwtplatform.dispatch.rest.client.testutils.ExposedRestAction;
import com.gwtplatform.dispatch.rest.client.testutils.SecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class RestInterceptorRegistryTest {
    private static final String PATH_1 = "/path_1";

    private static final String PARAM_NAME_1 = "someName";
    private static final String PARAM_NAME_2 = "someOtherName";
    private static final String PARAM_VALUE_1 = "someValue";
    private static final int PARAM_VALUE_2 = 7;

    private DefaultRestInterceptorRegistry interceptorRegistry;
    private ExposedRestAction<Void> action;
    private UrlUtils urlUtils;

    @Before
    public void setUp() {
        action = new SecuredRestAction(HttpMethod.GET, PATH_1);
        interceptorRegistry = new DefaultRestInterceptorRegistry();
        urlUtils = mock(UrlUtils.class);
    }

    @Test
    public void register_interceptor() {
        // Given
        RestContext context = new Builder()
                .path(PATH_1)
                .httpMethod(HttpMethod.GET)
                .build();

        // When
        interceptorRegistry.register(new SampleRestInterceptor(context));

        // Then
        assertNotNull(interceptorRegistry.find(action));
    }

    @Test
    public void register_interceptor_multipleParams() {
        // Given
        action.addParam(new QueryParameter(PARAM_NAME_1, PARAM_VALUE_1, null, urlUtils));
        action.addParam(new QueryParameter(PARAM_NAME_2, PARAM_VALUE_2, null, urlUtils));

        RestContext context = new Builder()
                .path(PATH_1)
                .httpMethod(HttpMethod.GET)
                .queryCount(2)
                .build();

        // When
        interceptorRegistry.register(new SampleRestInterceptor(context));

        // Then
        assertNotNull(interceptorRegistry.find(action));
    }

    @Test
    public void register_interceptor_multipleParams_invalid() {
        // Given
        action.addParam(new QueryParameter(PARAM_NAME_1, PARAM_VALUE_1, null, urlUtils));
        action.addParam(new QueryParameter(PARAM_NAME_2, PARAM_VALUE_2, null, urlUtils));

        RestContext context = new Builder()
                .path(PATH_1)
                .httpMethod(HttpMethod.GET)
                .queryCount(1)
                .build();

        // When
        interceptorRegistry.register(new SampleRestInterceptor(context));

        // Then
        assertNull(interceptorRegistry.find(action));
    }

    @Test
    public void register_interceptor_noHttpMethod() {
        // Given
        RestContext context = new Builder()
                .path(PATH_1)
                .build();

        // When
        interceptorRegistry.register(new SampleRestInterceptor(context));

        // Then
        assertNotNull(interceptorRegistry.find(action));
    }

    @Test
    public void register_interceptor_duplicate() {
        // Given
        RestContext context = new Builder()
                .path(PATH_1)
                .transcendent(true)
                .build();

        // When
        interceptorRegistry.register(new SampleRestInterceptor(context));
        try {
            interceptorRegistry.register(new SampleRestInterceptor(context));
            fail();
        } catch (DuplicateInterceptorContextException e) {
            // Then
            assertEquals(1, interceptorRegistry.getRegistryCount());
        }
    }
}
