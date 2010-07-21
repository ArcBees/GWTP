package com.gwtplatform.testing;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * Container of various scopes useful in test cases:
 * <ul>
 * <li>{@link #SINGLETON}</li>
 * </ul>
 * 
 * @author Philippe Beaudoin
 */
public class TestScope{

  /**
   * Test-scoped singletons are 
   * typically used in test cases for objects that correspond to singletons 
   * in the application. Your JUnit test case must use 
   * {@link GuiceMockitoJUnitRunner} on its {@code @RunWith}
   * annotation so that test-scoped singletons are reset before every
   * test case.
   */
  public final static Singleton SINGLETON = new Singleton();
  
  /**
   * Clears all the instances of test-scoped singletons. After this method is called, any 
   * "singleton" bound to this scope that had already been created will be
   * created again next time it gets injected.
   */
  public static void clear() { 
    SINGLETON.clear();
  }
    
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
  
}
