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

    /**
     * Put some data associated with the given key.
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        ensureDataMap().put(key, value);
    }

    /**
     * Get data associated with the given key.
     *
     * @param key
     * @return  data associated with the given key.
     */
    public <T> T get(String key) {
        return dataMap == null ? null : (T) dataMap.get(key);
    }

    private Map<String, Object> ensureDataMap() {
        return dataMap = dataMap == null ? new HashMap<String, Object>() : dataMap;
    }
}
