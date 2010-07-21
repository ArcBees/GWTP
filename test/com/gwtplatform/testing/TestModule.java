package com.gwtplatform.testing;

import com.google.inject.AbstractModule;
import com.google.inject.binder.ScopedBindingBuilder;

/**
 * A guice {@link AbstractModule} with a bit of syntactic sugar
 * to bind within typical test scopes.
 * 
 * @author Philippe Beaudoin
 */
public abstract class TestModule extends AbstractModule {

    /**
     * Binds an interface to a mocked version of itself.
     * 
     * @param <T> The type of the interface to bind
     * @param klass The class to bind
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindMock( Class<T> klass ) {
      return bind(klass).toProvider( new MockProvider<T>(klass) );
    }
  
}
