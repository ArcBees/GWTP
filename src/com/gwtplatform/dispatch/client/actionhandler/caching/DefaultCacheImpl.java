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

import com.google.inject.Inject;

import java.util.Date;
import java.util.HashMap;

/**
 * Default Implementation for {@link Cache}.
 * @author Sunny Gupta
 *
 */
public class DefaultCacheImpl implements Cache {

  private class CacheValue {

    private Object value;
    private Date lastUpdateTime;
    
    public CacheValue(Object value) {
      this.value = value;
      this.lastUpdateTime = new Date();
    }

    public Object getValue() {
      return this.value;
    }

    public Date getLastUpateTime() {
      return this.lastUpdateTime;
    }  
  }
  
  private HashMap<Object, CacheValue> map;
  
  @Inject
  public DefaultCacheImpl() {
    // Initialize map
    map = new HashMap<Object, CacheValue>();
  }
  
  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Object get(Object key) {
    // Check for null as Cache should not store null values / keys
    if (key == null) {
      // TODO Find a way to throw NullArgumentException, maybe create a custom one for gwtp?
      // throw new NullArgumentException("key");
      throw new RuntimeException("key is null");
   }
    return map.get(key).getValue();
  }

  @Override
  public void put(Object key, Object value) {
    // Check for null as Cache should not store null values / keys
    if (key == null) {
      // TODO Find a way to throw NullArgumentException, maybe create a custom one for gwtp?
      // throw new NullArgumentException("key");
      throw new RuntimeException("key is null");
    }
    if (value == null) {
      // TODO Find a way to throw NullArgumentException, maybe create a custom one for gwtp?
      // throw new NullArgumentException("key");
      throw new RuntimeException("value is null");
    }
    
    // Put in map
    map.put(key, new CacheValue(value));
  }

  @Override
  public void remove(Object key) {
    map.remove(key);
  }

  @Override
  public Date getLastUpateTime(Object key) {
    // Check for null as Cache should not store null values / keys
    if (key == null) {
      // TODO Find a way to throw NullArgumentException, maybe create a custom one for gwtp?
      // throw new NullArgumentException("key");
      throw new RuntimeException("key is null");
    }
    return map.get(key).getLastUpateTime();
  }

}
