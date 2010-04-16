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

/**
 * <b>Important:</b> This class participates in dependency injection
 * and should be injected, never instantiated with <code>new</code>.
 * <p />
 * A class that can contain handlers. These handlers are registered
 * when the class is bound and unregistered when the class is 
 * unbound.
 * 
 * @author Philippe Beaudoin
 */
public interface HandlerContainer {

  /**
   * Call this method after the object is constructed in order to
   * bind all its handlers.
   * <p />
   * When automatic binding is used (see {@link #HandlerContainer( 
   * boolean autoBind )}), this will be called immediately after the 
   * object is constructed through Guice/GIN dependency injection 
   * mechanism. This is done so that any singleton are correctly 
   * initialised. For this reason you should never call {@link 
   * #bind()} directly from the constructor.
   * <p />
   * If you are not using automatic binding, or if you later call 
   * {@link #unbind()} on this object, you will have to call {@link 
   * #bind()} again manually to make sure its handlers are bound. 
   * <p />
   * Multiple call to bind will not fail, the class will be bound once.
   */
  public void bind();

  /**
   * Call this method when you want to release the object and its handlers
   * are not needed anymore.
   */
  public void unbind();

  /**
   * Returns true if the presenter is currently in a 'bound' state. That is,
   * the {@link #bind()} method has completed and {@link #unbind()} has not
   * been called.
   *
   * @return <code>true</code> if bound.
   */
  public boolean isBound();
}