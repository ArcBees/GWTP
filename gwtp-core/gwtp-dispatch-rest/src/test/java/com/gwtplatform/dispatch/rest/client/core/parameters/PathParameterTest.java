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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.jukito.JukitoRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.junit.GWTMockUtilities;
import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(JukitoRunner.class)
public class PathParameterTest {
    private static final String SOME_KEY = "some-key";
    private static final String VALUE_1 = "some-value-1";
    private static final String ENCODED_VALUE_1 = "123456@1";
    private static final String VALUE_2 = "some-value-2";
    private static final String ENCODED_ANY_VALUE = "123456@2";

    @Inject
    private UrlUtils urlUtils;

    @Before
    public void setUp() {
        GWTMockUtilities.disarm();

        given(urlUtils.encodePathSegment(anyString())).willReturn(ENCODED_ANY_VALUE);
        given(urlUtils.encodePathSegment(VALUE_1)).willReturn(ENCODED_VALUE_1);
    }

    @After
    public void tearDown() {
        GWTMockUtilities.restore();
    }

    @Test
    public void getType_returnsForm() {
        // given
        PathParameter param = new PathParameter(SOME_KEY, VALUE_1, null, null, urlUtils);

        // when
        Type type = param.getType();

        // then
        assertThat(type).isSameAs(Type.PATH);
    }

    @Test
    public void getEntries_anyValue_encodesValue() {
        // given
        PathParameter param = new PathParameter(SOME_KEY, VALUE_1, null, null, urlUtils);

        // when
        List<Entry<String, String>> entries = param.getEncodedEntries();

        // then
        assertThat(entries)
                .hasSize(1)
                .extracting("key", "value")
                .containsExactly(tuple(SOME_KEY, ENCODED_VALUE_1));
    }

    @Test
    public void getEntries_collection_returnOneEntry() {
        // given
        Collection<String> objects = Arrays.asList(VALUE_1, VALUE_2);
        PathParameter param = new PathParameter(SOME_KEY, objects, null, null, urlUtils);

        // when
        List<Entry<String, String>> entries = param.getEncodedEntries();

        // then
        assertThat(entries)
                .hasSize(1)
                .extracting("key", "value")
                .containsExactly(tuple(SOME_KEY, ENCODED_ANY_VALUE));
    }
}
