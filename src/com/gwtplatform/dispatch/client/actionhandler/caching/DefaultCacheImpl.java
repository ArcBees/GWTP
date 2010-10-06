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
    private final boolean autoExpire;
    private final long expirationTimeInMs;
    
    public CacheValue(final Object value, final boolean autoExpire, long expirationTimeInMs) {
      this.value = value;
      this.lastUpdateTime = new Date().getTime();
      this.autoExpire = autoExpire;
      this.expirationTimeInMs = expirationTimeInMs;
    }

    public Object getValue() {
      return this.value;
    }

    public long getLastUpateTime() {
      return this.lastUpdateTime;
    }

    public boolean isAutoExpire() {
      return autoExpire;
    }

    public long getExpirationTimeInMs() {
      return expirationTimeInMs;
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
    // First check if entry can auto-expire
    if (cacheValue.isAutoExpire()) {
      // Now check if expirationTimeInMs is positive
      long expireTime = 0;
      if (cacheValue.getExpirationTimeInMs() > 0) {
        expireTime = cacheValue.getExpirationTimeInMs();
      } else {
        // Apply default behaviour of cache: Check if autoexpired
        if (cacheValue.isAutoExpire() && this.autoExpireTimeInMs > 0) {
          expireTime = this.autoExpireTimeInMs;
        }
      }
      // Check if expired
      long now = new Date().getTime();
      if (cacheValue.getLastUpateTime() + expireTime > now) {
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
    // Put with autoExpire = true
    put(key, value, true, 0);
  }
  
  @Override
  public void put(Object key, Object value, boolean autoExpire) {
    // Put with autoExpire = true
    put(key, value, autoExpire, 0);
  }

  @Override
  public void put(Object key, Object value, long expirationTimeInMs) {
    // Put with autoExpire = true
    put(key, value, true, expirationTimeInMs);
  }
  
  private void put(Object key, Object value, boolean autoExpire, long expirationTimeInMs) {
    // Check for null as Cache should not store null values / keys
    if (key == null) {
      throw new NullPointerException("key is null");
    }
    if (value == null) {
      throw new NullPointerException("value is null");
    }
    
    // Put in map
    map.put(key, new CacheValue(value, autoExpire, expirationTimeInMs));
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
