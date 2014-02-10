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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestParameter;

import static org.junit.Assert.assertEquals;

public class MultimapJsonSerializerTest {
    private MultimapJsonSerializer serializer = new MultimapJsonSerializer();

    @Test
    public void serializeEmpty() {
        // given
        Multimap<HttpMethod, RestParameter> map = LinkedHashMultimap.create();

        // when
        String serialized = serializer.serialize(map);

        // then
        assertEquals("{}", serialized);
    }

    @Test
    public void serializeSimple() {
        // given
        Multimap<HttpMethod, RestParameter> map = LinkedHashMultimap.create();
        map.put(HttpMethod.GET, new RestParameter("a", 1));

        // when
        String serialized = serializer.serialize(map);

        // then
        assertEquals("{\"GET\":[{\"key\": \"a\", \"value\": \"1\"}]}", serialized);
    }

    @Test
    public void serializeComplex() {
        // given
        Multimap<HttpMethod, RestParameter> map = LinkedHashMultimap.create();
        map.put(HttpMethod.GET, new RestParameter("a", 1));
        map.put(HttpMethod.GET, new RestParameter("b", false));
        map.put(HttpMethod.POST, new RestParameter("c", "some string"));
        map.put(HttpMethod.POST, new RestParameter("d", 29L));

        // when
        String serialized = serializer.serialize(map);

        // then
        assertEquals("{\"GET\":[{\"key\": \"a\", \"value\": \"1\"}, {\"key\": \"b\", \"value\": \"false\"}]," +
                "\"POST\":[{\"key\": \"c\", \"value\": \"some string\"}, {\"key\": \"d\", \"value\": \"29\"}]}",
                serialized);
    }
}
