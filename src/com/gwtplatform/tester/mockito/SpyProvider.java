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

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;

import static org.mockito.Mockito.spy;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For use in test cases where an {@link Provider} is required to provide an
 * object and the test case needs to provide a spy of the object.
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
public class SpyProvider<T> implements Provider<T> {

  private static Map<Class<?>, Injector> testClassToInjector = new HashMap<Class<?>, Injector>();
  private final Class<?> testClass;
private final InjectionPoint injectionPoint;
  private final Constructor<T> constructor;
  
  /**
   * Construct a {@link Provider} that will return spied instances of objects 
   * of the specified types.
   * 
   * @param injector The {@link Class} of the mock object to provide.
   * @param typeToProvide The {@link TypeLiteral} of the mock object to provide.
   * @param annotation 
   */
  @SuppressWarnings("unchecked")
  public SpyProvider(Class<?> testClass, TypeLiteral<T> typeToProvide) {
    this.testClass = testClass;
    injectionPoint = InjectionPoint.forConstructorOf(typeToProvide);
    constructor = (Constructor<T>) injectionPoint.getMember();
  }

  @SuppressWarnings("unchecked")
  @Override
  public T get() {
    List<Dependency<?>> dependencies = injectionPoint.getDependencies();
    Injector injector = testClassToInjector.get(testClass);
    
    Object[] constructorParameters = new Object[dependencies.size()];
    for (Dependency dependency : dependencies) {
      constructorParameters[dependency.getParameterIndex()] = 
        injector.getInstance(dependency.getKey());
    }
    T instance;
    try {
      instance = constructor.newInstance(constructorParameters);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    injector.injectMembers(instance);
    return spy(instance);
  }

  public static synchronized void setInjector(Class<?> testClass, Injector injector) {
    testClassToInjector.put(testClass, injector);
  }
}
