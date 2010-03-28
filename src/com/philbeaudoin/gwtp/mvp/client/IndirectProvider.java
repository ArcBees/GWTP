package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Provider;

/**
 * Act as a {@link Provider}, but instead of returning the provided
 * object, the user must pass a callback in which the object is
 * handled. This makes it possible to use the {@link Provider} for
 * code-splitting. This feature should be available in a later
 * release of GIN. See <a href="http://code.google.com/p/google-gin/issues/detail?id=61">GIN Issue 61</a>
 * for details.
 *
 * @param <T> The type of the provided object.
 * 
 * @author Philippe Beaudoin
 */
public interface IndirectProvider<T> {
  /**
   * Asynchronously get the provided object.
   * 
   * @param callback The {@link Callback} to invoke once the object is available.
   *                 The parameter to the callback will be the provided object.
   */
  void get(AsyncCallback<T> callback);
}