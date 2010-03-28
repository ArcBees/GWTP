package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.inject.client.Ginjector;

/**
 * Classes implementing that interface are expected to be bound
 * with GIN as eager singletons ({code .asEagerSingleton()}).
 * However, they have an empty constructor and are bound manually
 * when the program starts by calling their {@link #bind(Ginjector)}.
 * Their constructor will typically register themselves with the
 * {@link DelayedBindRegistry}, which will take car of calling 
 * {@code bind} on all the registered classes.
 * 
 * @author Philippe Beaudoin
 */
public interface DelayedBind {
  /**
   * Requests that the classes binds all its objects
   * using the {@link Ginjector} to get the required instances.
   * This should ever only be called once, typically by
   * {@link DelayedBindRegistry#bind(Ginjector)}.
   * You should cast the passed {@link Ginjector} to your
   * specific Ginjector interface.
   * 
   * @param ginjector The {@link Ginjector} from which to get object instances.
   */
  public void delayedBind( Ginjector ginjector );  
}
