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

package com.gwtplatform.mvp.client;

import com.gwtplatform.tester.mockito.AutomockingModule;
import com.gwtplatform.tester.mockito.GuiceMockitoJUnitRunner;
import com.gwtplatform.tester.mockito.InjectTest;

import static org.junit.Assert.assertFalse;

import org.junit.runner.RunWith;

/**
 * Unit tests for {@link HandlerContainerImpl}.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class HandlerContainerImpl2Test {
  
  /**
   * Guice test module.
   */
  public static class Module extends AutomockingModule {
    @Override
    protected void configureTest() {
      disableAutobinding();
    }
  }

  @InjectTest
  public void shouldNotBindDefaultHandlerContainerOnInjection(
      HandlerContainerImpl handlerContainer) {
    // Given
    // HandlerContainerImpl is injected

    // When, Then
    // the bind method should be injected at creation time
    assertFalse(handlerContainer.isBound());
  }
}
