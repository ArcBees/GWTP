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
