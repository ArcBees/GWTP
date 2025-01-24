/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.gin;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.gwtplatform.dispatch.rest.client.core.parameters.FormParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.HeaderParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.PathParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.QueryParameter;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;

import static org.junit.Assert.assertEquals;
import static com.gwtplatform.dispatch.rest.client.gin.RestParameterBindingsSerializer.URL_UTILS;

public class RestParameterBindingsSerializerTest {
    private RestParameterBindingsSerializer serializer;

    @Before
    public void setUp() {
        serializer = new RestParameterBindingsSerializer();
    }

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
        map.put(HttpMethod.GET, new QueryParameter("a", 1, null, URL_UTILS));

        // when
        String serialized = serializer.serialize(map);

        // then
        assertEquals("{\"GET\":[{\"type\": \"QUERY\", \"key\": \"a\", \"value\": \"1\"}]}", serialized);
    }

    @Test
    public void serializeComplex() {
        // given
        GWTMockUtilities.disarm();
        RestParameterBindings map = new RestParameterBindings();
        map.put(HttpMethod.GET, new QueryParameter("a", 1, null, URL_UTILS));
        map.put(HttpMethod.GET, new FormParameter("b", false, null, URL_UTILS));
        map.put(HttpMethod.POST, new HeaderParameter("c", "some string", null));
        map.put(HttpMethod.POST, new PathParameter("d", 29L, null, null, URL_UTILS));

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
