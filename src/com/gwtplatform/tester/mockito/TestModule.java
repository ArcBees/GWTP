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
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.name.Names;

import com.gwtplatform.mvp.client.AutobindDisable;

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
   * This method binds many different instances to the same class or interface. Use this only
   * if the instances are totally stateless. That is, they are immutable and have
   * no mutable dependencies (e.g. a {@link String} or a simple POJO). For more
   * complex classes use {@link #bindMany}. 
   * <p />
   * The specified {@link Class} will be bound to all the different instances, each
   * binding using a different unique annotation.
   * <p />
   * This method is useful when combined with the {@literal @}{@link All} annotation.
   * 
   * @param clazz The {@link Class} to which the instances will be bound.
   * @param instances All the instances to bind.
   */
  protected <T, V extends T> void bindManyInstances(Class<T> clazz, V... instances) {
    for (V instance : instances) {
      bind(clazz).annotatedWith(UniqueAnnotations.create()).toInstance(instance);
    }
  }

  /**
   * This method binds many different instances to the same type literal. Use this only
   * if the instances are totally stateless. That is, they are immutable and have
   * no mutable dependencies (e.g. a {@link String} or a simple POJO). For more
   * complex classes use {@link #bindMany}. 
   * <p />
   * The specified {@link TypeLiteral} will be bound to all the different instances, each
   * binding using a different unique annotation.
   * <p />
   * This method is useful when combined with the {@literal @}{@link All} annotation.
   * 
   * @param type The {@link TypeLiteral} to which the instances will be bound.
   * @param instances All the instances to bind.
   */
  protected <T, V extends T> void bindManyInstances(TypeLiteral<T> type, V... instances) {
    for (V instance : instances) {
      bind(type).annotatedWith(UniqueAnnotations.create()).toInstance(instance);
    }
  }
  
  /**
   * This method binds many different classes to the same interface. All the
   * classes will be bound within the {@link TestScope#SINGLETON} scope.
   * <p />
   * This method is useful when combined with the {@literal @}{@link All} annotation.
   * 
   * @param clazz The {@link Class} to which the instances will be bound.
   * @param boundClasses All the classes to bind.
   */
  protected <T> void bindMany(Class<T> clazz, Class<? extends T>... boundClasses) {
    for (Class<? extends T> boundClass : boundClasses) {
      bind(clazz).annotatedWith(UniqueAnnotations.create()).to(boundClass).in(TestScope.SINGLETON);
    }
  }

  /**
   * This method binds many different type litterals to the same type litteral. All the
   * classes will be bound within the {@link TestScope#SINGLETON} scope.
   * <p />
   * This method is useful when combined with the {@literal @}{@link All} annotation.
   * 
   * @param type The {@link Class} to which the instances will be bound.
   * @param boundTypes All the types to bind.
   */
  protected <T, V extends T> void bindMany(TypeLiteral<T> type, TypeLiteral<? extends T>... boundTypes) {
    for (TypeLiteral<? extends T> boundType : boundTypes) {
      bind(type).annotatedWith(UniqueAnnotations.create()).to(boundType).in(TestScope.SINGLETON);
    }
  }
  
  /**
   * Binds an interface annotated with a {@link com.google.inject.name.Named @Named}.
   * 
   * @param <T> The type of the interface to bind
   * @param klass The class to bind
   * @param name The name used with the {@link com.google.inject.name.Named @Named} annotation.
   * @return A {@link ScopedBindingBuilder}.
   */
  protected <T> LinkedBindingBuilder<T> bindNamed(Class<T> klass, String name) {
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
  protected <T> LinkedBindingBuilder<T> bindNamed(TypeLiteral<T> typeLiteral,
      String name) {
    return bind(typeLiteral).annotatedWith(Names.named(name));
  }

  /**
   * Globally disable automatic binding in
   * classes derived from {@link com.gwtplatform.mvp.client.HandlerContainerImpl HandlerContainerImpl}, 
   * such as {@link com.gwtplatform.mvp.client.Presenter PresenterWidget} or {@link com.gwtplatform.mvp.client.PresenterWidget Presenter}.
   */
  protected void disableAutobinding() {
    bind(AutobindDisable.class).toInstance(new AutobindDisable(true));    
  }
  
}
