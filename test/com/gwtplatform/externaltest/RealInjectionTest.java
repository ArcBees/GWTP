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

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import com.gwtplatform.mvp.client.DefaultEventBus;
import com.gwtplatform.mvp.client.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.tester.mockito.AutomockingModule;
import com.gwtplatform.tester.mockito.GuiceMockitoJUnitRunner;
import com.gwtplatform.tester.mockito.TestScope;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test behaviour when a real {@link com.gwtplatform.mvp.client.PresenterWidget} is injected.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class RealInjectionTest {

  /**
   * Guice test module.
   */
  public static class Module extends AutomockingModule {
    @Override
    protected void configureTest() {
      bindNamed(new TypeLiteral<PresenterWidget<? extends View>>() { }, "Sub").to(SubPresenterWidget.class)
        .in(TestScope.SINGLETON);
      bind(EventBus.class).to(DefaultEventBus.class).in(TestScope.SINGLETON);
    }
  }
  
  // SUT
  @Inject MainPresenter mainPresenter;

  @Test
  public void settingSubPresenterShouldNotCrash() {
    // When
    mainPresenter.setSubPresenter();

    // Then nothing should crash 
  }
}
