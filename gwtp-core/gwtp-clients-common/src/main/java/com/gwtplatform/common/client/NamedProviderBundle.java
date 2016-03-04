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

package com.gwtplatform.common.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Provider;

public abstract class NamedProviderBundle extends ProviderBundle {
    private final List<String> presenterIds;

    protected NamedProviderBundle(Entry<String, Provider<?>>... providerEntries) {
        super(providerEntries == null ? 0 : providerEntries.length);

        presenterIds = new ArrayList<>(providers.length);
        if (providers.length != 0) {
            assignProviders(providerEntries);
        }
    }

    private void assignProviders(Entry<String, Provider<?>>[] providerEntries) {
        int i = 0;
        for (Entry<String, Provider<?>> provider : providerEntries) {
            presenterIds.add(provider.getKey());
            providers[i++] = provider.getValue();
        }
    }

    public Provider<?> get(String name) {
        if (presenterIds.contains(name)) {
            return get(presenterIds.indexOf(name));
        }

        return null;
    }
}
