/**
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

package com.gwtplatform.dispatch.rest.shared;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RestParameterTest {
    static class WrappedValue {
        private final String value;

        WrappedValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @Test
    public void getStringValue_collection_oneValue() {
        // Given
        ArrayList<WrappedValue> list = new ArrayList<WrappedValue>();
        list.add(new WrappedValue("test1"));

        RestParameter parameter = new RestParameter("name", list);

        // When
        String stringValue = parameter.getStringValue();

        // Then
        assertEquals("test1", stringValue);
    }

    @Test
    public void getStringValue_collection_multipleValues() {
        // Given
        ArrayList<WrappedValue> list = new ArrayList<WrappedValue>();
        list.add(new WrappedValue("test1"));
        list.add(new WrappedValue("test2"));
        list.add(new WrappedValue("test3"));

        RestParameter parameter = new RestParameter("name", list);

        // When
        String stringValue = parameter.getStringValue();

        // Then
        assertEquals("test1,test2,test3", stringValue);
    }

    @Test
    public void getStringValue_object() {
        // Given
        RestParameter parameter = new RestParameter("name", new WrappedValue("test1"));

        // When
        String stringValue = parameter.getStringValue();

        // Then
        assertEquals("test1", stringValue);
    }

    @Test
    public void getStringValue_null() {
        // Given
        RestParameter parameter = new RestParameter("name", null);

        // When
        String stringValue = parameter.getStringValue();

        // Then
        assertEquals("", stringValue);
    }
}
