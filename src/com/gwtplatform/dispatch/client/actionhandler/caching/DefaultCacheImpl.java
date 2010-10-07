/**
 * Copyright 2010 ArcBees Inc.
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

import java.util.Date;
import java.util.HashMap;

/**
 * Default Implementation for {@link Cache}.
 * @author Sunny Gupta
 *
 */
public class DefaultCacheImpl implements Cache {

  private class CacheValue {

    private final Object value;
    private final long lastUpdateTime;
    
    public CacheValue(final Object value) {
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
  
  private HashMap<Object, CacheValue> map;
  private long autoExpireTimeInMs;
  
  /**
   * Initializes the cache with auto expiration OFF.
   */
  public DefaultCacheImpl() {
    // Initialize map
    this.map = new HashMap<Object, CacheValue>();
    // By default, autoExpireTime is 0 so that objects never expire
    this.autoExpireTimeInMs = 0;
  }
  
  /**
   * Initialize the cache with auto expiration ON.
   * 
   * @param autoExpireTimeInMs Time in milliseconds after which entries in cache expire
   */
  public DefaultCacheImpl(long autoExpireTimeInMs) {
    // Initialize map
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
    // Check if to be autoexpired
    if (this.autoExpireTimeInMs > 0) {
      // Check if expired
      long now = new Date().getTime();
      if (cacheValue.getLastUpateTime() + this.autoExpireTimeInMs > now) {
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
    return map.get(key).getLastUpateTime();
  }

  @Override
  public long getAutoExpireTimeInMs() {
    return autoExpireTimeInMs;
  }

  @Override
  public void setAutoExpireTimeInMs(long autoExpireTimeInMs) {
    // Check for a positive value
    if (autoExpireTimeInMs >= 0) {
      this.autoExpireTimeInMs = autoExpireTimeInMs;
    }
  }

}
