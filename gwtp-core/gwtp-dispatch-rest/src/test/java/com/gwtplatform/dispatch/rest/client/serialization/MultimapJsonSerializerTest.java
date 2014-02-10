/**
 * Copyright (c) 2014 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
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
