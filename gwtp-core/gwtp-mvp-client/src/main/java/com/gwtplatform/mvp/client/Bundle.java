package com.gwtplatform.mvp.client;

import java.util.HashMap;

/**
 * Created at 30/08/13 23:32
 *
 * @author Danilo Reinert
 */

public class Bundle {

    private final HashMap<String, Object> dataMap = new HashMap<String, Object>();

    public void put(String key, Object value) {
        dataMap.put(key, value);
    }

    public Object get(String key) {
        return dataMap.get(key);
    }
}
