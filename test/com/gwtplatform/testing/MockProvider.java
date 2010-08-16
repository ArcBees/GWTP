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

package com.gwtplatform.testing;

import com.google.inject.Provider;

import static org.mockito.Mockito.mock;

/**
 * Provider of mock objects for dependency injection.
 * 
 * @author Philippe Beaudoin
 * 
 * @param <T> The class to provide.
 */
public class MockProvider<T> implements Provider<T> {

  private final Class<T> classToProvide;

  /**
   * Creates a mock singleton provider for a given class.
   * 
   * @param classToProvide The class for which to create a mock singleton
   *          provider.
   */
  public MockProvider(Class<T> classToProvide) {
    this.classToProvide = classToProvide;
  }

  @Override
  public T get() {
    return mock(classToProvide);
  }

}
