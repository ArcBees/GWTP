package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * An {@link IndirectProvider} that gets the object using
 * code splitting and invokes the callback once the code
 * is loaded. This is essentially the same as a standard 
 * {@link AsyncProvider}, but shares the interface of other
 * {@link IndirectProvider}.
 *
 * @param <T> The type of the provided object.
 * 
 * @author Philippe Beaudoin
 */
public class CodeSplitProvider<T> implements IndirectProvider<T> {

  private final AsyncProvider<T> provider;

  /**
   * Construct an {@link IndirectProvider} that gets the object using
   * code splitting and invokes the callback once the code
   * is loaded.
   * 
   * @param provider The {@link AsyncProvider} providing the object.
   */
  public CodeSplitProvider( AsyncProvider<T> provider ) {
    this.provider = provider;
  }

  @Override
  public void get(AsyncCallback<T> callback) {
    provider.get(callback);
  }

}