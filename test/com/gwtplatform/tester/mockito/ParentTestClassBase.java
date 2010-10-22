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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This parent test class is used by {@link ParentTestClassTest}.
 * 
 * @author Philippe Beaudoin
 */
@Ignore("Tests in this base class are not meant to be run independantly.")
public class ParentTestClassBase {

  /**
   * This should be automatically injected in the child class.
   */
  @TestSingleton
  public static class SingletonDefinedInParent {
    private String value = "SingletonDefinedInParentValue";
    public String getValue() {
      return value;
    }
  }
  
  /**
   * This should be automatically injected in the child class.
   */
  @TestMockSingleton
  public interface MockSingletonDefinedInParent { 
    void mockSingletonMethod();
  }

  /**
   */
  public interface DummyInterface { 
    String getDummyValue();
  }

  /**
   */
  public interface DummyInterfaceUsedOnlyInParent {
    String getDummyValue();
  }
  
  /**
   */
  public static class DummyClassUsedOnlyInParent { }
  
  @Inject protected Provider<DummyInterface> dummyProvider;
  @Inject protected MockSingletonDefinedInParent mockSingletonDefinedInParent;
  
  /**
   * This class keeps track of what happens in all the tests run in this
   * class and its child. It's used to make sure all expected tests are called.
   */
  protected static class Bookkeeper {
    static boolean parentTestShouldRunExecuted;
  }
  
  @Test
  public void parentTestShouldRun() {
    Bookkeeper.parentTestShouldRunExecuted = true;
  }
  
  @Test
  public void interfaceBoundInChildIsInjectedInParent() {
    assertEquals("DummyValue", dummyProvider.get().getDummyValue());
  }
  
  @Test
  public void interfaceBoundInChildIsInjectedInParentTestMethod(
      DummyInterface dummyInterface) {
    assertEquals("DummyValue", dummyInterface.getDummyValue());
  }

  @Test
  public void interfaceUsedInParentTestMethodShouldBeMockedAsTestSingleton(
      Provider<DummyInterfaceUsedOnlyInParent> provider) {
    // Following should not crash
    verify(provider.get(), never()).getDummyValue();
    
    assertSame(provider.get(), provider.get());
  }
  
  @Test
  public void concreteClassUsedInParentTestMethodShouldBeBoundAsTestSingleton(
      Provider<DummyClassUsedOnlyInParent> provider) {
    assertSame(provider.get(), provider.get());
  }  
}
