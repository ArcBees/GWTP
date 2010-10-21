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

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test that the various flavours of singletons work correctly.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class SingletonTest {
  
  /**
   */
  @TestSingleton
  public static class Registry {
    public int registrationCount;
    public void register() { 
      registrationCount++;
    }
  }

  /**
   * This class keeps track of what happens in all the tests run in this
   * class. It's used to make sure all expected tests are called.
   */
  private static class Bookkeeper {
    static int numberOfTimesEagerSingletonIsInstantiated;
    static int numberOfTimesSingletonIsInstantiated;
  }
  
  /**
   * This should automatically register before each test.
   */
  @TestEagerSingleton
  public static class MyTestEagerSingleton {
    @Inject
    public MyTestEagerSingleton(Registry registry) {
      registry.register();
      Bookkeeper.numberOfTimesEagerSingletonIsInstantiated++;
    }
  }

  /**
   * This should register only in tests where it is injected.
   */
  @TestSingleton
  public static class MyTestSingleton {
    @Inject
    public MyTestSingleton(Registry registry) {
      registry.register();
      Bookkeeper.numberOfTimesSingletonIsInstantiated++;
    }
  }
  
  @Inject Registry registry;
  
  @Test
  public void onlyEagerSingletonShouldBeRegistered() {
    assertEquals(1, registry.registrationCount);
  }
  
  @Test
  public void bothSingletonsShouldBeRegistered(MyTestSingleton myTestSingleton) {
    assertEquals(2, registry.registrationCount);
  }

  @AfterClass
  public static void verifyNumberOfInstantiations() {
    assertEquals(2, Bookkeeper.numberOfTimesEagerSingletonIsInstantiated);
    assertEquals(1, Bookkeeper.numberOfTimesSingletonIsInstantiated);
  }

}
