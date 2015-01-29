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

package com.gwtplatform.dispatch.rest.client.core.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CollectionSupportedParameter extends ClientHttpParameter {
    public CollectionSupportedParameter(
            Type type,
            String name,
            Object object,
            String dateFormat) {
        super(type, name, object, dateFormat);
    }

    @Override
    public List<Entry<String, String>> getEntries() {
        List<Map.Entry<String, String>> entries = new ArrayList<Entry<String, String>>();

        if (getObject() instanceof Collection) {
            // Spec. requires only List<T>, Set<T> or SortedSet<T>... but really?!
            for (Object item : (Collection<?>) getObject()) {
                entries.add(createEntry(item));
            }
        } else {
            entries.add(createEntry(getObject()));
        }

        return entries;
    }
}
