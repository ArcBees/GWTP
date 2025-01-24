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

package com.gwtplatform.dispatch.client.actionhandler.caching;

/**
 * Interface for client side caching.
 *
 * @deprecated Since 1.4. Use com.gwtplatform.dispatch.rpc.client.interceptor.caching.Cache
 */
@Deprecated
public interface Cache {
    /**
     * Puts the key-value pair in the cache. If an entry with key already exists, it is overwritten.
     * If automatic expiration is used, this entry will be expired after {@link #getAutoExpireTimeInMs()} milliseconds.
     *
     * @param key   The key for the entry to be cached
     * @param value The corresponding value
     */
    void put(Object key, Object value);

    /*
     * Returns the auto expiry time in milliseconds.
     */
    long getAutoExpireTimeInMs();

    /**
     * Set the auto expiry time, after which an entry will expire after it is put in cache.
     *
     * @param autoExpireTimeInMs The auto expiry time in milliseconds
     */
    void setAutoExpireTimeInMs(long autoExpireTimeInMs);

    /**
     * Returns the cached value corresponding to key.
     *
     * @param key The key for the cached entry
     * @return The value corresponding to key
     */
    Object get(Object key);

    /**
     * Clears the entire cache.
     */
    void clear();

    /**
     * Removes the entry from the cache.
     *
     * @param key The key for the cached entry
     */
    void remove(Object key);

    /**
     * Returns the last update time in milliseconds since January 1, 1970, 00:00:00 GMT for the cached entry.
     *
     * @param key The key for which last update time is required
     * @return The last update time as long value, if the value is cached, otherwise -1
     */
    long getLastUpateTime(Object key);
}
