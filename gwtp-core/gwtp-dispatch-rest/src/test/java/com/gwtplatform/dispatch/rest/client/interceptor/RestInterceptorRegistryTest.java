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

package com.gwtplatform.dispatch.rest.client.interceptor;

import org.junit.Before;
import org.junit.Test;

import com.gwtplatform.dispatch.rest.client.ExposedRestAction;
import com.gwtplatform.dispatch.rest.client.SecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;

import static org.junit.Assert.assertTrue;

public class RestInterceptorRegistryTest {
    private static final String PATH_1 = "/path_1";

    private static final String PARAM_NAME_1 = "someName";
    private static final String PARAM_NAME_2 = "someOtherName";
    private static final String PARAM_VALUE_1 = "someValue";
    private static final int PARAM_VALUE_2 = 7;
    private static final String PARAM_VALUE_FORMATTED_2 = "7";

    private DefaultRestInterceptorRegistry interceptorRegistry;
    private ExposedRestAction<Void> action;

    @Before
    public void setUp() {
        action = new SecuredRestAction(HttpMethod.GET, PATH_1);
        interceptorRegistry = new DefaultRestInterceptorRegistry();
    }

    @Test
     public void register_interceptor() {
        // When
        InterceptorContext index = new InterceptorContext(PATH_1, HttpMethod.GET, 0, false);
        interceptorRegistry.register(new SampleRestInterceptor(index));

        // Then
        assertTrue(interceptorRegistry.find(action) != null);
    }

    @Test
    public void register_interceptor_multipleparams() {
        // When
        action.addQueryParam(PARAM_NAME_1, PARAM_VALUE_1);
        action.addQueryParam(PARAM_NAME_2, PARAM_VALUE_2);

        InterceptorContext index = new InterceptorContext(PATH_1, HttpMethod.GET, 2, false);
        interceptorRegistry.register(new SampleRestInterceptor(index));

        // Then
        assertTrue(interceptorRegistry.find(action) != null);
    }

    @Test
    public void register_interceptor_multipleparams_invalid() {
        // When
        action.addQueryParam(PARAM_NAME_1, PARAM_VALUE_1);
        action.addQueryParam(PARAM_NAME_2, PARAM_VALUE_2);

        InterceptorContext index = new InterceptorContext(PATH_1, HttpMethod.GET, 1, false);
        interceptorRegistry.register(new SampleRestInterceptor(index));

        // Then
        assertTrue(interceptorRegistry.find(action) == null);
    }
}
