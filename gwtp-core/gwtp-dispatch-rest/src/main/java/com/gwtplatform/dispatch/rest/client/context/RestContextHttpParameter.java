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

package com.gwtplatform.dispatch.rest.client.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.gwtplatform.dispatch.rest.shared.HttpParameter;

class RestContextHttpParameter implements HttpParameter {
    private final String name;
    private final String value;

    RestContextHttpParameter(
            String name,
            String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getObject() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.QUERY;
    }

    @Override
    public List<Entry<String, String>> getEncodedEntries() {
        return new ArrayList<>();
    }
}
