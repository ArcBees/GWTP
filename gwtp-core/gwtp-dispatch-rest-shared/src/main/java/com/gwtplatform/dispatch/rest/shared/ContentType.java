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

package com.gwtplatform.dispatch.rest.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represent each parts of a content type. Formatting is based on
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">RFC 2616, section 3.7</a>.
 */
public class ContentType {
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String PARAMETER_DELIMITER = ";";
    private static final String TYPE_DELIMITER = "/";
    private static final String WILDCARD = "*";

    private final String type;
    private final String subType;
    private final Map<String, String> parameters;

    public ContentType(
            String type,
            String subType,
            Map<String, String> parameters) {
        this.type = valueOrWildcard(type);
        this.subType = valueOrWildcard(subType);
        this.parameters = parameters;
    }

    /**
     * Parses a content type and return an object representation. Input may look like:
     * <p>
     * application/json; charset=utf-8; q=0.9
     */
    public static ContentType valueOf(String value) {
        String type = WILDCARD;
        String subType = WILDCARD;
        Map<String, String> parameters = new HashMap<>();

        if (value != null) {
            String[] parts = value.split(PARAMETER_DELIMITER);
            String[] types = parts[0].split(TYPE_DELIMITER);

            type = types[0];

            if (types.length > 1) {
                subType = types[1];
            }

            // Element 0 is type/sub-type, others are parameters
            for (int i = 1; i < parts.length; ++i) {
                String[] keyValue = parts[i].split(KEY_VALUE_DELIMITER);

                if (keyValue.length == 2) {
                    parameters.put(keyValue[0].trim(), keyValue[1].trim());
                }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContentType that = (ContentType) o;
        return parameters.equals(that.parameters)
                && subType.equals(that.subType)
                && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + subType.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }

    public String getType() {
        return type;
    }

    /**
     * Checks if the primary type is a wildcard.
     *
     * @return <code>true</code> if the primary type is a wildcard.
     */
    public boolean isWildcardType() {
        return getType().equals(WILDCARD);
    }

    public String getSubType() {
        return subType;
    }

    /**
     * Checks if the sub type is a wildcard.
     *
     * @return <code>true</code> if the sub type is a wildcard.
     */
    public boolean isWildcardSubType() {
        return getSubType().equals(WILDCARD);
    }

    /**
     * Checks if either the primary type or the sub type is a wildcard.
     *
     * @return <code>true</code> if the primary type or sub type is a wildcard.
     */
    public boolean isWildcard() {
        return isWildcardType() || isWildcardSubType();
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public boolean isCompatible(ContentType other) {
        if (other == null) {
            return false;
        } else if (getType().equals(WILDCARD) || other.getType().equals(WILDCARD)) {
            return true;
        } else if (getType().equals(other.getType())) {
            return getSubType().equals(WILDCARD)
                    || other.getSubType().equals(WILDCARD)
                    || getSubType().equals(other.getSubType());
        }

        return false;
    }

    private String valueOrWildcard(String type) {
        return type == null || type.trim().isEmpty() ? WILDCARD : type.trim().toLowerCase();
    }
}
