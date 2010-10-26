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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test that methods with some parameters annotated with {@literal @}{@link All} behave correctly.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class BindSpyTest {

  /**
   * Guice test module.
   */
  public static class Module extends AutomockingModule {
    @Override
    protected void configureTest() {
      bindSpy(SimpleClass.class).in(TestScope.SINGLETON);
    }
  }
  
  /**
   */
  public interface CompositionMockA {
    String test();
  }
  
  /**
   */
  public interface CompositionMockB {
    String test();
  }

  /**
   */
  public static class SimpleClass {
    private CompositionMockA mockA;
    @Inject CompositionMockB mockB;

    @Inject
    SimpleClass(CompositionMockA mockA) {
      this.mockA = mockA;
    }
    
    String callTestMethodOnMock() {
      mockA.test();
      mockB.test();
      return "Default string";
    }
  }
  
  @Inject CompositionMockA mockA;
  @Inject CompositionMockA mockB;
  
  @Test
  public void testStubbingSpiedInstance(SimpleClass simpleClass) {
    // GIVEN
    doReturn("Mocked string").when(simpleClass).callTestMethodOnMock();
    
    // WHEN
    String result = simpleClass.callTestMethodOnMock();
    
    // THEN
    assertEquals("Mocked string", result);
    verify(mockA, never()).test();
    verify(mockB, never()).test();
  }

  @Test
  public void testNotStubbingSpiedInstance(SimpleClass simpleClass) {
    // WHEN
    String result = simpleClass.callTestMethodOnMock();
    
    // THEN
    verify(mockA).test();
    verify(mockB).test();
    assertEquals("Default string", result);
  }

}
