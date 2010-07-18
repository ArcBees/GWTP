package com.philbeaudoin.gwtp.mvp.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.philbeaudoin.gwtp.mvp.client.proxy.Proxy;
import com.philbeaudoin.gwtp.testing.GuiceMockitoJUnitRunner;
import com.philbeaudoin.gwtp.testing.TestModule;
import com.philbeaudoin.gwtp.testing.TestScope;

/**
 * Unit tests for {@link PresenterImpl}.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class PresenterImplTest {
  // Guice environment
  public static class Env extends TestModule {
    @Override
    protected void configure() {
      bindMock(EventBus.class).in(TestScope.SINGLETON);
      bindMock(ViewA.class).in(TestScope.SINGLETON);
      bindMock(ProxyA.class).in(TestScope.SINGLETON);
      bind(PresenterA.class).in(TestScope.SINGLETON);
    }
  }

  interface ViewA extends View {}
  interface ProxyA extends Proxy<PresenterA> {}

  // Simple subclasses of PresenterWidgetImpl
  static abstract class PresenterSpy<V extends View,P extends Proxy<?>> extends PresenterImpl<V,P> {
    public int revealInParentCalled = 0;

    PresenterSpy(EventBus eventBus, V view, P proxy) {
      super(eventBus, view, proxy);
    }

    @Override
    public void revealInParent() { 
      super.onReveal(); 
      revealInParentCalled++;
    }
  }

  static class PresenterA extends PresenterSpy<ViewA, ProxyA> {
    @Inject
    public PresenterA(EventBus eventBus, ViewA view, ProxyA proxy) {
      super(eventBus, view, proxy);
    }
  }  

  // Providers to use Guice injection
  @Inject Provider<EventBus> EventBusProvider;
  @Inject Provider<ViewA> viewAProvider;
  @Inject Provider<ProxyA> proxyAProvider;
  @Inject Provider<PresenterA> presenterAProvider;  

  @Test
  public void forceRevealWhenPresenterIsNotVisible() {
    // Set-up
    PresenterA presenter = presenterAProvider.get();
    
    // Given
    assertFalse( presenter.isVisible() );  
    
    // When
    presenter.forceReveal();
    
    // Then
    assertEquals( 1, presenter.revealInParentCalled );
  }

  @Test
  public void forceRevealWhenPresenterIsVisible() {
    // Set-up
    PresenterA presenter = presenterAProvider.get();
    
    // Given
    presenter.notifyReveal();
    assertTrue( presenter.isVisible() );  
    
    // When
    presenter.forceReveal();
    
    // Then
    assertEquals( 0, presenter.revealInParentCalled );
  }
}
