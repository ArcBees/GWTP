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

package com.gwtplatform.dispatch.rest.client.core.parameters;

import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JukitoRunner.class)
public class DefaultHttpParameterFactoryTest {
    private static final String SOME_KEY = "some key";
    private static final Object SOME_VALUE = new Object();

    @Inject
    private DefaultHttpParameterFactory factory;

    @Test
    public void createFormParam() {
        // when
        HttpParameter param = factory.create(Type.FORM, SOME_KEY, SOME_VALUE, null);

        // then
        assertThat(param).isInstanceOf(FormParameter.class);
    }

    @Test
    public void createHeaderParam() {
        // when
        HttpParameter param = factory.create(Type.HEADER, SOME_KEY, SOME_VALUE, null);

        // then
        assertThat(param).isInstanceOf(HeaderParameter.class);
    }

    @Test
    public void createPathParam() {
        // when
        HttpParameter param = factory.create(Type.PATH, SOME_KEY, SOME_VALUE, null);

        // then
        assertThat(param).isInstanceOf(PathParameter.class);
    }

    @Test
    public void createQueryParam() {
        // when
        HttpParameter param = factory.create(Type.QUERY, SOME_KEY, SOME_VALUE, null);

        // then
        assertThat(param).isInstanceOf(QueryParameter.class);
    }

    @Test
    public void createCookieParam() {
        // when
        HttpParameter param = factory.create(Type.COOKIE, SOME_KEY, SOME_VALUE, null);

        // then
        assertThat(param).isInstanceOf(CookieParameter.class);
    }

    @Test
    public void createMatrixParam() {
        // when
        HttpParameter param = factory.create(Type.MATRIX, SOME_KEY, SOME_VALUE, null);

        // then
        assertThat(param).isInstanceOf(MatrixParameter.class);
    }
}
