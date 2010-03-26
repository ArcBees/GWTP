package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Provider;

/**
 * An {@link IndirectProvider} that immediately gets the object and 
 * invokes the callback, without performing code splitting. 
 * This is essentially the same as a standard {@link Provider}, 
 * but shares the interface of other {@link IndirectProvider}.
 *
 * @param <T> The type of the provided object.
 * 
 * @author Philippe Beaudoin
 */
public class StandardProvider<T> implements IndirectProvider<T> {

  private final Provider<T> provider;

  /**
   * Creates a {@link IndirectProvider} that directly gets the object and 
   * invokes the callback.
   * 
   * @param provider The {@link Provider} of the object. 
   */
  public StandardProvider( Provider<T> provider ) {
    this.provider = provider;
  }

  @Override
  public void get(AsyncCallback<T> callback) {
    callback.onSuccess( provider.get() );
  }

}
