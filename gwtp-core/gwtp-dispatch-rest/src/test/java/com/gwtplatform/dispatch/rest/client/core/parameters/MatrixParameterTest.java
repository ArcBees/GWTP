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

package com.gwtplatform.dispatch.rest.client.core.parameters;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.rebind.utils.Arrays;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

@RunWith(JukitoRunner.class)
public class MatrixParameterTest {
    private static final String KEY = "key";
    private static final String ENCODED_KEY = "^31337$";
    private static final String VALUE_1 = "some-value-1";
    private static final String VALUE_2 = "some-value-2";
    private static final String ENCODED_VALUE_1 = "123456@1";
    private static final String ENCODED_VALUE_2 = "123456@2";

    @Inject
    private UrlUtils urlUtils;

    @Before
    public void setUp() {
        given(urlUtils.encodeMatrixParameter(KEY)).willReturn(ENCODED_KEY);
        given(urlUtils.encodeMatrixParameter(VALUE_1)).willReturn(ENCODED_VALUE_1);
        given(urlUtils.encodeMatrixParameter(VALUE_2)).willReturn(ENCODED_VALUE_2);
    }

    @Test
    public void getType_returnsMatrix() {
        // given
        MatrixParameter param = new MatrixParameter(KEY, VALUE_1, null, urlUtils);

        // when
        Type type = param.getType();

        // then
        assertThat(type).isSameAs(Type.MATRIX);
    }

    @Test
    public void getEntries_anyValue_encodesValue() {
        // given
        MatrixParameter param = new MatrixParameter(KEY, VALUE_1, null, urlUtils);

        // when
        List<Entry<String, String>> entries = param.getEntries();

        // then
        assertThat(entries)
                .hasSize(1)
                .extracting("key", "value")
                .containsExactly(tuple(ENCODED_KEY, ENCODED_VALUE_1));
    }

    @Test
    public void getEntries_collection_returnMultipleEntries() {
        // given
        Collection<String> objects = Arrays.asList(VALUE_1, VALUE_2);
        MatrixParameter param = new MatrixParameter(KEY, objects, null, urlUtils);

        // when
        List<Entry<String, String>> entries = param.getEntries();

        // then
        assertThat(entries)
                .hasSameSizeAs(objects)
                .extracting("key", "value")
                .containsExactly(
                        tuple(ENCODED_KEY, ENCODED_VALUE_1),
                        tuple(ENCODED_KEY, ENCODED_VALUE_2));
    }
}
