/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.core.parameters;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;

public class ClientHttpParameter implements HttpParameter {
    private final Type type;
    private final String name;
    private final Object object;
    private final DateTimeFormat dateFormatter;

    public ClientHttpParameter(
            Type type,
            String name,
            Object object,
            String dateFormat) {
        this.type = type;
        this.name = name;
        this.object = object;
        this.dateFormatter = dateFormat != null ? DateTimeFormat.getFormat(dateFormat) : null;
    }

    @Override
    public Type getType() {
        return type;
    }

    /**
     * @return the name of this parameter.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the object associated with this parameter.
     */
    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public List<Entry<String, String>> getEncodedEntries() {
        List<Entry<String, String>> entries = new ArrayList<Entry<String, String>>();
        entries.add(createEntry(object));
        return entries;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "type=" + type +
                ", name='" + name + "'" +
                ", object=" + object +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClientHttpParameter that = (ClientHttpParameter) o;

        return name.equals(that.name)
                || (object == null ? that.object == null : object.equals(that.object));
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (object != null ? object.hashCode() : 0);
        return result;
    }

    protected Entry<String, String> createEntry(Object object) {
        String value = parseObject(object);
        String encodedValue = encodeValue(value);
        String encodedKey = encodeKey(name);

        return new SimpleEntry<>(encodedKey, encodedValue);
    }

    protected String parseObject(Object object) {
        if (object == null) {
            return null;
        } else if (dateFormatter != null && object instanceof Date) {
            return dateFormatter.format((Date) object);
        } else {
            return object.toString();
        }
    }

    protected String encodeKey(String key) {
        return key;
    }

    protected String encodeValue(String value) {
        return value;
    }
}
