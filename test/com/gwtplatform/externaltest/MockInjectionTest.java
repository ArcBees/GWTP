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

package com.gwtplatform.externaltest;

import com.google.inject.TypeLiteral;

import com.gwtplatform.mvp.client.DefaultEventBus;
import com.gwtplatform.mvp.client.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.tester.mockito.AutomockingModule;
import com.gwtplatform.tester.mockito.GuiceMockitoJUnitRunner;
import com.gwtplatform.tester.mockito.InjectTest;
import com.gwtplatform.tester.mockito.TestScope;

import org.junit.runner.RunWith;

/**
 * Test behaviour when a mock {@link PresenterWidget} is injected.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class MockInjectionTest {

  /**
   * Guice test environment.
   * 
   * @author Philippe Beaudoin
   */
  public static class Env extends AutomockingModule {
    @Override
    protected void configureTest() {
      bindNamedMock(new TypeLiteral<PresenterWidget<View>>() { }, "Sub").in(TestScope.SINGLETON);
      bind(EventBus.class).to(DefaultEventBus.class).in(TestScope.SINGLETON);
    }
  }

  @InjectTest
  public void settingSubPresenterShouldNotCrash(MainPresenter mainPresenter) {
    // When
    mainPresenter.setSubPresenter();

    // Then nothing should crash 
  }
}
