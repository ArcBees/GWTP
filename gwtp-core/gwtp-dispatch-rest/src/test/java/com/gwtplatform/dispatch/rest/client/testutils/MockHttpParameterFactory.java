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

package com.gwtplatform.dispatch.rest.client.testutils;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.gwtplatform.dispatch.rest.client.core.parameters.CookieParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.FormParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.HeaderParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.core.parameters.PathParameter;
import com.gwtplatform.dispatch.rest.client.core.parameters.QueryParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MockHttpParameterFactory implements HttpParameterFactory {
    @Override
    public HttpParameter create(Type type, String name, Object object, String dateFormat) {
        return create(type, name, object);
    }

    @Override
    public HttpParameter create(Type type, String name, Object object) {
        HttpParameter mock = createMock(type);
        initMock(mock, type, name, object);

        return mock;
    }

    private static HttpParameter createMock(Type type) {
        switch (type) {
            case FORM:
                return mock(FormParameter.class);
            case HEADER:
                return mock(HeaderParameter.class);
            case PATH:
                return mock(PathParameter.class);
            case QUERY:
                return mock(QueryParameter.class);
            case COOKIE:
                return mock(CookieParameter.class);
            case MATRIX:
            default:
                return mock(HttpParameter.class);
        }
    }

    private static void initMock(HttpParameter mock, Type type, String key, Object value) {
        List<Entry<String, String>> entries = new ArrayList<Entry<String, String>>();
        entries.add(new SimpleEntry<String, String>(key, String.valueOf(value)));

        given(mock.getType()).willReturn(type);
        given(mock.getName()).willReturn(key);
        given(mock.getObject()).willReturn(value);
        given(mock.getEntries()).willReturn(entries);
    }
}
