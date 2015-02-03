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

package com.gwtplatform.dispatch.rest.rebind.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static javax.ws.rs.core.MediaType.MEDIA_TYPE_WILDCARD;

public class ContentType {
    private static final Pattern PARAMETER_SPLIT_PATTERN = Pattern.compile("[;]");
    private static final Pattern TYPE_SPLIT_PATTERN = Pattern.compile("[/]");
    private static final Pattern KEY_VALUE_SPLIT_PATTERN = Pattern.compile("[=]");

    private final String type;
    private final String subType;
    private final Map<String, String> parameters;

    public ContentType(
            String type,
            String subType,
            Map<String, String> parameters) {
        this.type = type.trim().toLowerCase();
        this.subType = subType.trim().toLowerCase();
        this.parameters = parameters;
    }

    /**
     * Parses a content type and return an object representation. Input may look like:
     * <p/>
     * application/json; charset=utf-8; q=0.9
     */
    public static ContentType valueOf(String value) {
        String[] parts = PARAMETER_SPLIT_PATTERN.split(value);
        String[] types = TYPE_SPLIT_PATTERN.split(parts[0]);

        String type = types[0];
        String subType = MEDIA_TYPE_WILDCARD;
        Map<String, String> parameters = new HashMap<String, String>();

        if (types.length > 0) {
            subType = types[0];
        }

        // Element 0 is type/sub-type
        for (int i = 1; i < parts.length; ++i) {
            String[] keyValue = KEY_VALUE_SPLIT_PATTERN.split(parts[i]);

            if (keyValue.length == 2) {
                parameters.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        return new ContentType(type, subType, parameters);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append(type).append('/').append(subType);

        for (Entry<String, String> parameter : parameters.entrySet()) {
            builder.append("; ").append(parameter.getKey()).append('=').append(parameter.getValue());
        }

        return builder.toString();
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public boolean isCompatible(ContentType other) {
        if (other == null) {
            return false;
        } else if (getType().equals(MEDIA_TYPE_WILDCARD) || other.getType().equals(MEDIA_TYPE_WILDCARD)) {
            return true;
        } else if (getType().equals(other.getType())) {
            return getSubType().equals(MEDIA_TYPE_WILDCARD)
                    || other.getSubType().equals(MEDIA_TYPE_WILDCARD)
                    || getSubType().equals(other.getSubType());
        }

        return false;
    }
}
