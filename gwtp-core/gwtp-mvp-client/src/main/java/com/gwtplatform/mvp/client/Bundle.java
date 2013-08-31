package com.gwtplatform.mvp.client;

import java.util.HashMap;

/**
 * Object that holds data to be transferred between {@link PresenterWidget}s
 * through {@link com.gwtplatform.mvp.client.PresenterWidget.FinishCallback}
 *
 * @author Danilo Reinert
 */

public class Bundle {

    private final HashMap<String, Object> dataMap = new HashMap<String, Object>();

    /**
     * Put some data associated with the given key.
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        dataMap.put(key, value);
    }

    /**
     * Get data associated with the given key.
     *
     * @param key
     * @return  data associated with the given key.
     */
    public Object get(String key) {
        return dataMap.get(key);
    }
}
