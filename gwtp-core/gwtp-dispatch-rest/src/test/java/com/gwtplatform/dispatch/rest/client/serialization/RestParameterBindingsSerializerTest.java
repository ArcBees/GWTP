/**
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.serialization;

import org.junit.Test;

import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.testutils.MockHttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.utils.RestParameterBindings;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import static org.junit.Assert.assertEquals;

public class RestParameterBindingsSerializerTest {
    private final RestParameterBindingsSerializer serializer = new RestParameterBindingsSerializer();
    private final HttpParameterFactory factory = new MockHttpParameterFactory();

    @Test
    public void serializeEmpty() {
        // given
        RestParameterBindings map = new RestParameterBindings();

        // when
        String serialized = serializer.serialize(map);

        // then
        assertEquals("{}", serialized);
    }

    @Test
    public void serializeSimple() {
        // given
        RestParameterBindings map = new RestParameterBindings();
        map.put(HttpMethod.GET, factory.create(Type.QUERY, "a", 1));

        // when
        String serialized = serializer.serialize(map);

        // then
        assertEquals("{\"GET\":[{\"type\": \"QUERY\", \"key\": \"a\", \"value\": \"1\"}]}", serialized);
    }

    @Test
    public void serializeComplex() {
        // given
        RestParameterBindings map = new RestParameterBindings();
        map.put(HttpMethod.GET, factory.create(Type.QUERY, "a", 1));
        map.put(HttpMethod.GET, factory.create(Type.FORM, "b", false));
        map.put(HttpMethod.POST, factory.create(Type.HEADER, "c", "some string"));
        map.put(HttpMethod.POST, factory.create(Type.PATH, "d", 29L));

        // when
        String serialized = serializer.serialize(map);

        // then
        assertEquals("{\"GET\":["
                        + "{\"type\": \"QUERY\", \"key\": \"a\", \"value\": \"1\"},"
                        + "{\"type\": \"FORM\", \"key\": \"b\", \"value\": \"false\"}]," +
                        "\"POST\":["
                        + "{\"type\": \"HEADER\", \"key\": \"c\", \"value\": \"some string\"},"
                        + "{\"type\": \"PATH\", \"key\": \"d\", \"value\": \"29\"}]}",
                serialized);
    }
}
