/**
 * Copyright 2010 ArcBees Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.test.mockito;

import com.google.inject.AbstractModule;
import com.google.inject.binder.ScopedBindingBuilder;

/**
 * A guice {@link AbstractModule} with a bit of syntactic sugar to bind within
 * typical test scopes. Depends on mockito.
 * <p />
 * Depends on Mockito.
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
  protected <T> ScopedBindingBuilder bindMock(Class<T> klass) {
    return bind(klass).toProvider(new MockProvider<T>(klass));
  }

}
