/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.filter;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gwtplatform.dispatch.rest.client.context.RestContext;

public class DefaultRestFilterRegistry implements RestFilterRegistry {
    private final List<Entry<RestContext, RestFilter>> filters = new ArrayList<>();

    @Override
    public Iterator<Map.Entry<RestContext, RestFilter>> iterator() {
        return filters.iterator();
    }

    @Override
    public void register(RestFilter filter) {
        filters.add(new SimpleEntry<>(filter.getRestContext(), filter));
    }
}
