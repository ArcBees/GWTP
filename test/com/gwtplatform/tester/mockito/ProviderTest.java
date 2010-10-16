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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test that providers injected by the tester module behaves correctly.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class ProviderTest {

  /**
   * Guice test module.
   */
  public static class Module extends AutomockingModule {
    @Override
    protected void configureTest() {
      bindNamedMock(Mock.class, "singleton").in(TestScope.SINGLETON);
      bindNamedMock(Mock.class, "nonsingleton");
      bindNamed(Instance.class, "singleton").to(Instance.class).in(TestScope.SINGLETON);
      bindNamed(Instance.class, "nonsingleton").to(Instance.class);
    }
  }
  
  /** 
   */
  public interface Mock { }  

  /** 
   */
  public static class Instance {
    @Inject Instance() { }
  }
  
  @Test
  public void mockSingletonProviderShouldReturnTheSameInstance(
      @Named("singleton") Provider<Mock> provider) {
    assertSame(provider.get(), provider.get());
  }
  
  @Test
  public void mockNonSingletonProviderShouldNotReturnTheSameInstance(
      @Named("nonsingleton") Provider<Mock> provider) {
    assertNotSame(provider.get(), provider.get());
  }
  
  @Test
  public void singletonClassShouldReturnTheSameInstance(
      @Named("singleton") Provider<Instance> provider) {
    assertSame(provider.get(), provider.get());
  }
  
  @Test
  public void nonSingletonClassShouldNotReturnTheSameInstance(
      @Named("nonsingleton") Provider<Instance> provider) {
    assertNotSame(provider.get(), provider.get());
  }
  
}
