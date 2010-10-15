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

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;

import com.gwtplatform.mvp.client.AutobindDisable;
import com.gwtplatform.mvp.client.HandlerContainerImpl;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.PresenterWidget;

/**
 * A guice {@link com.google.inject.Module Module} with a bit of syntactic sugar to bind within
 * typical test scopes. Depends on mockito. 
 * <p />
 * Depends on Mockito.
 * 
 * @author Philippe Beaudoin
 */
public abstract class TestModule extends AbstractModule {
  
  protected Class<?> testClass;

  public void configure() {
    bindScope(TestSingleton.class, TestScope.SINGLETON);
    bindScope(TestEagerSingleton.class, TestScope.EAGER_SINGLETON);
    configureTest();
  }
  
  public void setTestClass(Class<?> testClass) {
    this.testClass = testClass;
  }

  /**
   * Configures a test {@link com.google.inject.Module Module} via the exposed methods.
   */
  protected abstract void configureTest();
  
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
  
  /**
   * Binds an interface annotated with a {@link com.google.inject.name.Named @Named} to a 
   * mocked version of itself.
   *  
   * @param <T> The type of the interface to bind, a parameterized type
   * @param typeLiteral The {@link TypeLiteral} corresponding to the parameterized type to bind.
   * @return A {@link ScopedBindingBuilder}.
   */
  @SuppressWarnings("unchecked")
  protected <T> ScopedBindingBuilder bindMock(
      TypeLiteral<T> typeLiteral) {
    return bind(typeLiteral).toProvider(new MockProvider<T>((Class<T>) typeLiteral.getRawType()));
  }
  
  /**
   * Binds an interface annotated with a {@link com.google.inject.name.Named @Named} to a 
   * mocked version of itself.
   * 
   * @param <T> The type of the interface to bind
   * @param klass The class to bind
   * @param name The name used with the {@link com.google.inject.name.Named @Named} annotation.
   * @return A {@link ScopedBindingBuilder}.
   */
  protected <T> ScopedBindingBuilder bindNamedMock(Class<T> klass, String name) {
    return bind(klass).annotatedWith(Names.named(name)).toProvider(new MockProvider<T>(klass));
  }

  /**
   * Binds an interface annotated with a {@link com.google.inject.name.Named @Named}.
   * 
   * @param <T> The type of the interface to bind
   * @param typeLiteral The {@link TypeLiteral} corresponding to the parameterized type to bind.
   * @param name The name used with the {@link com.google.inject.name.Named @Named} annotation.
   * @return A {@link ScopedBindingBuilder}.
   */
  @SuppressWarnings("unchecked")
  protected <T> ScopedBindingBuilder bindNamedMock(TypeLiteral<T> typeLiteral,
      String name) {
    return bind(typeLiteral).annotatedWith(Names.named(name)).toProvider(
        new MockProvider<T>((Class<T>) typeLiteral.getRawType()));
  }
  
  /**
   * Binds an interface annotated with a {@link com.google.inject.name.Named @Named}.
   * 
   * @param <T> The type of the interface to bind
   * @param klass The class to bind
   * @param name The name used with the {@link com.google.inject.name.Named @Named} annotation.
   * @return A {@link ScopedBindingBuilder}.
   */
  @SuppressWarnings("unchecked")
  protected <T> LinkedBindingBuilder bindNamed(Class<T> klass, String name) {
    return bind(klass).annotatedWith(Names.named(name));
  }

  /**
   * Binds an interface annotated with a {@link com.google.inject.name.Named @Named}.
   * 
   * @param <T> The type of the interface to bind
   * @param typeLiteral The {@link TypeLiteral} corresponding to the parameterized type to bind.
   * @param name The name used with the {@link com.google.inject.name.Named @Named} annotation.
   * @return A {@link ScopedBindingBuilder}.
   */
  @SuppressWarnings("unchecked")
  protected <T> LinkedBindingBuilder bindNamed(TypeLiteral<T> typeLiteral,
      String name) {
    return bind(typeLiteral).annotatedWith(Names.named(name));
  }
 
  /**
   * Globally disable automatic binding in
   * classes derived from {@link HandlerContainerImpl}, such as {@link PresenterWidget} 
   * or {@link Presenter}.
   */
  protected void disableAutobinding() {
    bind(AutobindDisable.class).toInstance(new AutobindDisable(true));    
  }
  
}
