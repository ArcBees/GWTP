package com.philbeaudoin.gwtp.testing;

import com.google.inject.Provider;
import static org.mockito.Mockito.*;

/**
 * Provider of mock objects for dependency injection.
 * 
 * @author Philippe Beaudoin
 *
 * @param <T> The class to provide.
 */
public class MockProvider<T> implements Provider<T> {
  
  private final Class<T> classToProvide;

  /**
   * Creates a mock singleton provider for a given class.
   * 
   * @param classToProvide The class for which to create a mock singleton provider.
   */
  public MockProvider( Class<T> classToProvide ) {
    this.classToProvide = classToProvide;
  }

  @Override
  public T get() {
    return mock(classToProvide);
  }


}
