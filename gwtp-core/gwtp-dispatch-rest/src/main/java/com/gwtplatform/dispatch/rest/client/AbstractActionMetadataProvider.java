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

package com.gwtplatform.dispatch.rest.client;

import java.util.HashMap;
import java.util.Map;

import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * This class offers helper methods to help the generated implementation of {@link ActionMetadataProvider} register
 * metadata in an {@link java.util.HashMap}.
 */
public abstract class AbstractActionMetadataProvider implements ActionMetadataProvider {
    private final Map<MetadataKey, Object> metadata = new HashMap<MetadataKey, Object>();

    @Override
    public Object getValue(RestAction<?> action, MetadataType metadataType) {
        return metadata.get(MetadataKey.create(action.getClass(), metadataType));
    }

    @SuppressWarnings("rawtypes")
    protected void register(Class<? extends RestAction> actionClass, MetadataType metadataType, Object value) {
        metadata.put(MetadataKey.create(actionClass, metadataType), value);
    }
}
