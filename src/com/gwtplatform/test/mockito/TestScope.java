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

package com.gwtplatform.test.mockito;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;


import java.util.HashMap;
import java.util.Map;

/**
 * Container of the {@link #SINGLETON} and {@link #EAGER_SINGLETON} scopes for 
 * test cases running with the {@link GuiceMockitoJUnitRunner}. Depends on mockito.
 * <p />
 * Depends on Mockito.
 * 
 * @author Philippe Beaudoin
 */
public class TestScope {

  private static class Singleton implements Scope {
    private final Map<Key<?>, Object> backingMap = new HashMap<Key<?>, Object>();

    public void clear() {
      backingMap.clear();
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
      return new Provider<T>() {
        @SuppressWarnings("unchecked")
        public T get() {

          Object o = backingMap.get(key);

          if (o == null) {
            o = unscoped.get();
            backingMap.put(key, o);
          }
          return (T) o;
        }
      };
    }
  }

  /**
   * Test-scoped singletons are typically used in test cases for objects that
   * correspond to singletons in the application. Your JUnit test case must use
   * {@link GuiceMockitoJUnitRunner} on its {@code @RunWith} annotation so that
   * test-scoped singletons are reset before every test case.
   * <p />
   * If you want your singleton to be instantiated automatically with each new
   * test, use {@link #EAGER_SINGLETON}.
   */
  public static final Singleton SINGLETON = new Singleton();
  
  /**
   * Eager test-scoped singleton are similar to test-scoped {@link #SINGLETON}
   * but they get instantiated automatically with each new test. 
   */
  public static final Singleton EAGER_SINGLETON = new Singleton();

  /**
   * Clears all the instances of test-scoped singletons. After this method is
   * called, any "singleton" bound to this scope that had already been created
   * will be created again next time it gets injected.
   */
  public static void clear() {
    SINGLETON.clear();
    EAGER_SINGLETON.clear();
  }

}
