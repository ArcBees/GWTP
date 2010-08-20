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
import com.google.inject.Provider;

import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.testing.GuiceMockitoJUnitRunner;
import com.gwtplatform.testing.TestModule;
import com.gwtplatform.testing.TestScope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for {@link PresenterImpl}.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class PresenterImplTest {
  /**
   * Guice environment.
   */
  public static class Env extends TestModule {
    @Override
    protected void configure() {
      bindMock(EventBus.class).in(TestScope.SINGLETON);
      bindMock(ViewA.class).in(TestScope.SINGLETON);
      bindMock(ProxyA.class).in(TestScope.SINGLETON);
      bind(PresenterA.class).in(TestScope.SINGLETON);
    }
  }

  static class PresenterA extends PresenterSpy<ViewA, ProxyA> {
    @Inject
    public PresenterA(EventBus eventBus, ViewA view, ProxyA proxy) {
      super(eventBus, view, proxy);
    }
  }
  // Simple subclasses of PresenterWidgetImpl
  abstract static class PresenterSpy<V extends View, P extends Proxy<?>>
      extends Presenter<V, P> {
    public int revealInParentCalled;

    PresenterSpy(EventBus eventBus, V view, P proxy) {
      super(eventBus, view, proxy);
    }

    @Override
    public void revealInParent() {
      super.onReveal();
      revealInParentCalled++;
    }
  }

  interface ProxyA extends Proxy<PresenterA> {
  }

  interface ViewA extends View {
  }

  // Providers to use Guice injection
  @Inject
  Provider<EventBus> EventBusProvider;
  @Inject
  Provider<PresenterA> presenterAProvider;
  @Inject
  Provider<ProxyA> proxyAProvider;
  @Inject
  Provider<ViewA> viewAProvider;

  @Test
  public void forceRevealWhenPresenterIsNotVisible() {
    // Set-up
    PresenterA presenter = presenterAProvider.get();

    // Given
    assertFalse(presenter.isVisible());

    // When
    presenter.forceReveal();

    // Then
    assertEquals(1, presenter.revealInParentCalled);
  }

  @Test
  public void forceRevealWhenPresenterIsVisible() {
    // Set-up
    PresenterA presenter = presenterAProvider.get();

    // Given
    presenter.notifyReveal();
    assertTrue(presenter.isVisible());

    // When
    presenter.forceReveal();

    // Then
    assertEquals(0, presenter.revealInParentCalled);
  }
}
