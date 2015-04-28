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

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

import com.gwtplatform.dispatch.rest.rebind.utils.Arrays;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class HeaderParameterTest {
    private static final String SOME_KEY = "some-key";
    private static final String VALUE_1 = "some-value-1";
    private static final String VALUE_2 = "some-value-2";

    @Test
    public void getType_returnsForm() {
        // given
        HeaderParameter param = new HeaderParameter(SOME_KEY, VALUE_1, null);

        // when
        Type type = param.getType();

        // then
        assertThat(type).isSameAs(Type.HEADER);
    }

    @Test
    public void getEntries_anyValue_doesNotEncodeValue() {
        // given
        HeaderParameter param = new HeaderParameter(SOME_KEY, VALUE_1, null);

        // when
        List<Entry<String, String>> entries = param.getEncodedEntries();

        // then
        assertThat(entries)
                .hasSize(1)
                .extracting("key", "value")
                .containsExactly(tuple(SOME_KEY, VALUE_1));
    }

    @Test
    public void getEntries_collection_returnMultipleEntries() {
        // given
        Collection<String> objects = Arrays.asList(VALUE_1, VALUE_2);
        HeaderParameter param = new HeaderParameter(SOME_KEY, objects, null);

        // when
        List<Entry<String, String>> entries = param.getEncodedEntries();

        // then
        assertThat(entries)
                .hasSameSizeAs(objects)
                .extracting("key", "value")
                .containsExactly(
                        tuple(SOME_KEY, VALUE_1),
                        tuple(SOME_KEY, VALUE_2));
    }
}
