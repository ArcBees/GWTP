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

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Provides;
import com.gwtplatform.dispatch.rest.client.RestApplicationPath;
import com.gwtplatform.dispatch.rest.client.annotations.GlobalQueryParams;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.gin.RestParameterBindings;
import com.gwtplatform.dispatch.rest.client.testutils.ExposedRestAction;
import com.gwtplatform.dispatch.rest.client.testutils.MockHttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.testutils.SecuredRestAction;
import com.gwtplatform.dispatch.rest.client.testutils.UnsecuredRestAction;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static com.gwtplatform.dispatch.rest.shared.HttpMethod.GET;
import static com.gwtplatform.dispatch.rest.shared.HttpMethod.POST;

@RunWith(JukitoRunner.class)
public class DefaultUriFactoryTest {
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bindConstant().annotatedWith(RestApplicationPath.class).to(APPLICATION_PATH);

            bind(HttpParameterFactory.class).to(MockHttpParameterFactory.class);
        }

        @Provides
        @TestSingleton
        @GlobalQueryParams
        RestParameterBindings getQueryParams(HttpParameterFactory parameterFactory) {
            RestParameterBindings queries = new RestParameterBindings();
            queries.put(GET, parameterFactory.create(Type.QUERY, KEY_1, VALUE_1));
            queries.put(GET, parameterFactory.create(Type.QUERY, KEY_2, VALUE_2));
            queries.put(POST, parameterFactory.create(Type.QUERY, KEY_3, VALUE_3));

            return queries;
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
    private static final String ABSOLUTE_PATH = "http://www.arcbees.com/";
    private static final String RELATIVE_PATH = "/rel";

    @Inject
    private DefaultUriFactory factory;
    @Inject
    private HttpParameterFactory parameterFactory;

    @Test
    public void absoluteUrlShouldNotChange() {
        // Given
        RestAction<Void> action = new UnsecuredRestAction(parameterFactory, GET, ABSOLUTE_PATH);

        // When
        String url = factory.buildUrl(action);

        // Then
        assertThat(url).startsWith(ABSOLUTE_PATH);
    }

    @Test
    public void relativeUrlShouldBePrependedByApplicationPath() {
        // Given
        RestAction<Void> action = new UnsecuredRestAction(parameterFactory, GET, RELATIVE_PATH);

        // When
        String url = factory.buildUrl(action);

        // Then
        assertThat(url).startsWith(APPLICATION_PATH + RELATIVE_PATH);
    }

    @Test
    public void globalQueryParamsShouldBeSetForCorrespondingHttpMethod() {
        // Given
        RestAction<Void> action = new SecuredRestAction(parameterFactory, GET, RELATIVE_PATH);

        // When
        String url = factory.buildUrl(action);

        // Then
        String expectedUrl = String.format("?%s=%s&%s=%s", KEY_1, VALUE_1, KEY_2, VALUE_2);

        assertThat(url).endsWith(expectedUrl);
    }

    @Test
    public void allActionQueryParamsShouldBeSet() {
        // Given
        ExposedRestAction<Void> action = new SecuredRestAction(parameterFactory, GET, RELATIVE_PATH);
        action.addParam(Type.QUERY, ACTION_KEY_1, VALUE_1);
        action.addParam(Type.QUERY, ACTION_KEY_2, VALUE_2);

        // When
        String url = factory.buildUrl(action);

        // Then
        String expectedUrl = String.format("?%s=%s&%s=%s&%s=%s&%s=%s", KEY_1, VALUE_1, KEY_2, VALUE_2,
                ACTION_KEY_1, VALUE_1, ACTION_KEY_2, VALUE_2);

        assertThat(url).endsWith(expectedUrl);
    }

    @Test
    public void allMatrixParamsShouldBeSet() {
        // Given
        ExposedRestAction<Void> action = new SecuredRestAction(parameterFactory, GET, RELATIVE_PATH);
        action.addParam(Type.MATRIX, ACTION_KEY_1, VALUE_1);
        action.addParam(Type.MATRIX, ACTION_KEY_2, VALUE_2);

        // When
        String url = factory.buildUrl(action);

        // Then
        String expectedUrl = String.format(";%s=%s;%s=%s?%s=%s&%s=%s", ACTION_KEY_1, VALUE_1, ACTION_KEY_2, VALUE_2,
                KEY_1, VALUE_1, KEY_2, VALUE_2);

        assertThat(url).endsWith(expectedUrl);
    }
}
