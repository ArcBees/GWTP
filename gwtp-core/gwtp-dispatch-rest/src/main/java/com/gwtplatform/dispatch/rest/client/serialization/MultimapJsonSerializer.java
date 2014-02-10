/**
 * Copyright (c) 2014 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.gwtplatform.dispatch.rest.client.serialization;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

/**
 * Serializes a {@link com.google.common.collect.Multimap} to valid JSON.
 * Uses {@link java.lang.Object#toString()} to serialize the value on non-native types.
 * If you need a more rebust serializer, you should consider
 * https://github.com/nmorel/gwt-jackson/tree/master/extensions/guava
 */
public class MultimapJsonSerializer {
    public String serialize(Multimap<?, ?> parameters) {
        if (parameters.isEmpty()) {
            return "{}";
        }

        return "{\"" + Joiner.on(",\"").withKeyValueSeparator("\":").join(parameters.asMap()) + "}";
    }
}
