/*
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

package com.gwtplatform.dispatch.rpc.client.interceptor.caching;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Default Implementation for {@link Cache}, backed by an {@link java.util.HashMap}.
 */
public class DefaultCacheImpl implements Cache {
    private static class CacheValue {
        private final Object value;
        private final long lastUpdateTime;

        CacheValue(Object value) {
            this.value = value;
            this.lastUpdateTime = new Date().getTime();
        }

        public Object getValue() {
            return this.value;
        }

        public long getLastUpateTime() {
            return this.lastUpdateTime;
        }
    }

    private final Map<Object, CacheValue> map;
    private long autoExpireTimeInMs;

    /**
     * Initializes the cache with auto expiration OFF.
     */
    public DefaultCacheImpl() {
        this.map = new HashMap<Object, CacheValue>();
        // By default, autoExpireTime is -1 so that objects never expire
        this.autoExpireTimeInMs = -1;
    }

    /**
     * Initialize the cache with auto expiration ON.
     *
     * @param autoExpireTimeInMs Time in milliseconds after which entries in cache expire
     */
    public DefaultCacheImpl(long autoExpireTimeInMs) {
        this.map = new HashMap<Object, CacheValue>();
        this.autoExpireTimeInMs = autoExpireTimeInMs;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Object get(Object key) {
        // Check for null as Cache should not store null values / keys
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheValue cacheValue = map.get(key);
        // Check for null
        if (cacheValue == null) {
            return null;
        }
        // Check if to be autoexpired, autoExpireTimeInMs = 0 means expire immediately / no caching
        // Note: This point will never be reached if the value was put in cache when autoExpireTimeInMs was 0
        if (this.autoExpireTimeInMs >= 0) {
            // Check if expired
            long now = new Date().getTime();
            if (cacheValue.getLastUpateTime() + this.autoExpireTimeInMs < now) {
                // Expired, remove
                remove(key);
                return null;
            }
        }
        // Not expired, return the value
        return cacheValue.getValue();
    }

    @Override
    public void put(Object key, Object value) {
        // No point caching if autoExpireTimeInMs = 0
        if (this.autoExpireTimeInMs != 0) {
            // Check for null as Cache should not store null values / keys
            if (key == null) {
                throw new NullPointerException("key is null");
            }
            if (value == null) {
                throw new NullPointerException("value is null");
            }

            // Put in map
            map.put(key, new CacheValue(value));
        }
    }

    @Override
    public void remove(Object key) {
        map.remove(key);
    }

    @Override
    public long getLastUpateTime(Object key) {
        // Check for null as Cache should not store null values / keys
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheValue value = map.get(key);

        if (value != null) {
            return value.getLastUpateTime();
        } else {
            return -1;
        }
    }

    @Override
    public long getAutoExpireTimeInMs() {
        return autoExpireTimeInMs;
    }

    @Override
    public void setAutoExpireTimeInMs(long autoExpireTimeInMs) {
        this.autoExpireTimeInMs = autoExpireTimeInMs;
    }
}
