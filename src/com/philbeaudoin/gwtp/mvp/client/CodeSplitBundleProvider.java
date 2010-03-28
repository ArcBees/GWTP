package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Provider;

/**
 * Implements a {@link IndirectProvider} that uses code
 * splitting for a specific type. The object will be provided 
 * from a {@link ProviderBundle}.
 * 
 * @param <T> The type of the provided object.
 * @param <B> The type of the {@link ProviderBundle} providing this object.
 *
 * @author Philippe Beaudoin
 */
public class CodeSplitBundleProvider<T,B extends ProviderBundle> implements IndirectProvider<T> {

  private final AsyncProvider<B> bundleProvider;
  private final int providerId;

  /**
   * Construct a {@link IndirectProvider}  that implements code
   * splitting for a specific type. The object will be provided 
   * from a {@link ProviderBundle}.
   * 
   * @param bundleProvider The {@link ProviderBundle} providing the object.
   * @param providerId The identifier of the provided object, within the {@link ProviderBundle}.
   */
  public CodeSplitBundleProvider( 
      AsyncProvider<B> bundleProvider, 
      int providerId ) {
    this.bundleProvider = bundleProvider;
    this.providerId = providerId;
  }

  @Override
  public void get(final AsyncCallback<T> callback) {
    bundleProvider.get( new AsyncCallback<B>(){
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @SuppressWarnings("unchecked")
      @Override
      public void onSuccess(B providerBundle) {
        callback.onSuccess(((Provider<T>)providerBundle.get(providerId)).get());
      }
    } );
  }

}