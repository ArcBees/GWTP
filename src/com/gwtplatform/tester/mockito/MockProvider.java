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

package com.gwtplatform.tester.mockito;

import com.google.inject.Provider;

import static org.mockito.Mockito.mock;

/**
 * For use in test cases where an {@link Provider} is required to provide an
 * object and the test case needs to provide a mock of the object.
 * <p />
 * A new object is returned each the the provider is invoked, unless the object
 * is bound as a {@link TestScope#SINGLETON} or {@link TestScope#EAGER_SINGLETON}.  
 * <p />
 * Depends on Mockito.
 * 
 * @author Philippe Beaudoin
 * 
 * @param <T> The class to provide.
 */
public class MockProvider<T> implements Provider<T> {

  private final Class<T> classToProvide;

  /**
   * Construct a {@link Provider} that will return mocked objects of the specified types.
   * 
   * @param mock The {@link Class} of the mock object to provide.
   */
  public MockProvider(Class<T> classToProvide) {
    this.classToProvide = classToProvide;
  }

  @Override
  public T get() {
    return mock(classToProvide);
  }

}
