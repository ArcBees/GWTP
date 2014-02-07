package com.gwtplatform.dispatch.rest.client.serialization;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

/**
 * Serializes a {@link com.google.common.collect.Multimap} to valid JSON
 */
public class MultimapJsonSerializer {
    public String serialize(Multimap<?, ?> parameters) {
        if (parameters.isEmpty()) {
            return "{}";
        }

        return "{\"" + Joiner.on(",\"").withKeyValueSeparator("\":").join(parameters.asMap()) + "}";
    }
}
