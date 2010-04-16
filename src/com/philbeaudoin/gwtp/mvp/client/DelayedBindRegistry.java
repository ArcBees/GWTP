/**
 * Copyright 2010 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
public final class DelayedBindRegistry {

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
