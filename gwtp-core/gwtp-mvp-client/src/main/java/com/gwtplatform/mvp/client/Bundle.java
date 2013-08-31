package com.gwtplatform.mvp.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Object that holds data to be transferred between {@link PresenterWidget}s
 * through {@link com.gwtplatform.mvp.client.PresenterWidget.FinishCallback}
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
