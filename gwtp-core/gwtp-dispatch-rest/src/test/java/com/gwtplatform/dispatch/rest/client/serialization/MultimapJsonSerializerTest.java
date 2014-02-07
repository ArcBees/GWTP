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
    public void serialize_empty() {
        //given
        Multimap<HttpMethod, RestParameter> map = LinkedHashMultimap.create();

        //when
        String serialized = serializer.serialize(map);

        //then
        assertEquals("{}", serialized);
    }

    @Test
    public void serialize_simple() {
        //given
        Multimap<HttpMethod, RestParameter> map = LinkedHashMultimap.create();
        map.put(HttpMethod.GET, new RestParameter("a", 1));

        //when
        String serialized = serializer.serialize(map);

        //then
        assertEquals("{\"GET\":[{\"key\": \"a\", \"value\": \"1\"}]}", serialized);
    }

    @Test
    public void serialize_complex() {
        //given
        Multimap<HttpMethod, RestParameter> map = LinkedHashMultimap.create();
        map.put(HttpMethod.GET, new RestParameter("a", 1));
        map.put(HttpMethod.GET, new RestParameter("b", false));
        map.put(HttpMethod.POST, new RestParameter("c", "some string"));
        map.put(HttpMethod.POST, new RestParameter("d", 29l));

        //when
        String serialized = serializer.serialize(map);

        //then
        assertEquals("{\"GET\":[{\"key\": \"a\", \"value\": \"1\"}, {\"key\": \"b\", \"value\": \"false\"}]," +
                "\"POST\":[{\"key\": \"c\", \"value\": \"some string\"}, {\"key\": \"d\", \"value\": \"29\"}]}",
                serialized);
    }
}
