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

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.tester.mockito.GuiceMockitoJUnitRunner;
import com.gwtplatform.tester.mockito.InjectTest;
import com.gwtplatform.tester.mockito.TestModule;
import com.gwtplatform.tester.mockito.TestScope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;

/**
 * Unit tests for {@link Presenter}.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class PresenterTest {
  /**
   * Guice environment.
   */
  public static class Env extends TestModule {
    @Override
    protected void configure() {
      bindMock(EventBus.class).in(TestScope.SINGLETON);
      bindMock(View.class).in(TestScope.SINGLETON);
      bindMock(new TypeLiteral<Proxy<TestPresenter>>() { }).in(TestScope.SINGLETON);
            
      bind(TestPresenter.class).in(TestScope.SINGLETON);
    }
  }
  
  // Simple subclasses of PresenterWidgetImpl
  static class TestPresenter
      extends Presenter<View, Proxy<TestPresenter>> {
    public int revealInParentCalled;

    @Inject
    TestPresenter(EventBus eventBus, View view, Proxy<TestPresenter> proxy) {
      super(eventBus, view, proxy);
    }

    @Override
    public void revealInParent() {
      super.onReveal();
      revealInParentCalled++;
    }
  }

  @InjectTest
  public void forceRevealWhenPresenterIsNotVisible(
      TestPresenter presenter) {
    // Given
    assertFalse(presenter.isVisible());

    // When
    presenter.forceReveal();

    // Then
    assertEquals(1, presenter.revealInParentCalled);
  }

  @InjectTest
  public void forceRevealWhenPresenterIsVisible(
      TestPresenter presenter) {
    // Given
    presenter.reveal();
    assertTrue(presenter.isVisible());

    // When
    presenter.forceReveal();

    // Then
    assertEquals(0, presenter.revealInParentCalled);
  }
}
