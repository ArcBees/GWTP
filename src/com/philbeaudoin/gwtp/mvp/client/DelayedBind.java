/**
 * Copyright 2010 Gwt-Platform
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
