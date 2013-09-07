/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.mvp.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Object that holds data to be transferred between {@link PresenterWidget}s.
 * This object is passed through {@link com.gwtplatform.mvp.client.PresenterWidget.FinishCallback}
 *
 * @author Danilo Reinert
 */
public class Bundle {

    private HashMap<String, Object> dataMap;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bundle)) return false;

        Bundle bundle = (Bundle) o;

        if (dataMap != null ? !dataMap.equals(bundle.dataMap) : bundle.dataMap != null) return false;

        return true;
    }

    /**
     * Checks if bundle has data associated with the given key.
     *
     * @param key
     * @return  <code>true</code> if bundle contains some instantiated object related to the key;
     *          <code>false</code> otherwise.
     */
    public boolean has(String key) {
        return dataMap.get(key) != null;
    }

    @Override
    public int hashCode() {
        return dataMap != null ? dataMap.hashCode() : 0;
    }

    /**
     * Put some data associated with the given key.
     *
     * @param key
     * @param value
     */
    public Bundle put(String key, Object value) {
        ensureDataMap().put(key, value);
        return this;
    }

    /**
     * Get data associated with the given key.
     *
     * @param key
     * @return  data associated with the given key.
     */
    public Object get(String key) {
        return dataMap == null ? null : dataMap.get(key);
    }

    private Map<String, Object> ensureDataMap() {
        return dataMap = dataMap == null ? new HashMap<String, Object>() : dataMap;
    }
}
