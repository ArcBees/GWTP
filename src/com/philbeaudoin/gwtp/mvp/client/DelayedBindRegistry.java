package com.philbeaudoin.gwtp.mvp.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.inject.client.Ginjector;

/**
 * A static registry containing all the classes that want to be bound using
 * delayed binding. That is, classes implementing the {@link DelayedBind}
 * interface. These classes should be eager singletons and they should
 * register themselves with the {@link DelayedBindRegistry} in their
 * constructor by calling {@link #register(DelayedBind)}.
 * 
 * @author Philippe Beaudoin
 */
public class DelayedBindRegistry {

  private static List<DelayedBind> delayedBindObjects = new ArrayList<DelayedBind>();
  
  /**
   * Registers a new object so that it is bound using delayed binding.
   * This method should be called in the constructor of objects implementing
   * the {@link DelayedBind} interface.
   * 
   * @param delayedBindObject The object to register.
   */
  public static void register( DelayedBind delayedBindObject ) {
    delayedBindObjects.add( delayedBindObject );
  }
  
  /**
   * Bind all the registered classes, by calling
   * their {@link DelayedBind#bind(Ginjector)} method.
   * This method should only be called once, typically when the program
   * starts.
   * 
   * @param ginjector The {@link Ginjector} from which to get object instances.
   */
  public static void bind( Ginjector ginjector ) {
    for( DelayedBind delayedBindObject : delayedBindObjects )
      delayedBindObject.delayedBind( ginjector );
  }
}
