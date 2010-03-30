package com.philbeaudoin.gwtp.mvp.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.philbeaudoin.gwtp.testing.GuiceMockitoJUnitRunner;
import com.philbeaudoin.gwtp.testing.TestModule;
import com.philbeaudoin.gwtp.testing.TestScope;

/**
 * Unit tests for {@link PresenterWidgetImpl}.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class PresenterWidgetImplTest {
  // Guice environment
  public static class Env extends TestModule {
    @Override
    protected void configure() {
      bindMock(EventBus.class).in(TestScope.SINGLETON);
      bindMock(ViewA.class).in(TestScope.SINGLETON);
      bindMock(ViewB.class).in(TestScope.SINGLETON);
      bindMock(ViewC.class).in(TestScope.SINGLETON);
      bind(PresenterWidgetA.class).in(TestScope.SINGLETON);
      bind(PresenterWidgetB.class).in(TestScope.SINGLETON);
      bind(PresenterWidgetC.class).in(TestScope.SINGLETON);
    }
  }
  
  interface ViewA extends View {}
  interface ViewB extends View {}
  interface ViewC extends View {}
  
  // Two simple subclasses of PresenterWidgetImpl
  static class PresenterWidgetA extends PresenterWidgetImpl<ViewA> {
    @Inject
    public PresenterWidgetA(EventBus eventBus, ViewA view) {
      super(eventBus, view);
    }
  }
  static class PresenterWidgetB extends PresenterWidgetImpl<ViewB> {
    @Inject
    public PresenterWidgetB(EventBus eventBus, ViewB view) {
      super(eventBus, view);
    }
  }
  static class PresenterWidgetC extends PresenterWidgetImpl<ViewC> {
    @Inject
    public PresenterWidgetC(EventBus eventBus, ViewC view) {
      super(eventBus, view);
    }
  }
  
  // Providers to use Guice injection
  @Inject Provider<EventBus> EventBusProvider;
  @Inject Provider<ViewA> viewAProvider;
  @Inject Provider<ViewB> viewBProvider;
  @Inject Provider<ViewC> viewCProvider;
  @Inject Provider<PresenterWidgetA> presenterWidgetAProvider;
  @Inject Provider<PresenterWidgetB> presenterWidgetBProvider;
  @Inject Provider<PresenterWidgetC> presenterWidgetCProvider;

  @Test
  public void presenterWidgetIsInitiallyNotVisible() {
    // Set-up
    PresenterWidget presenterWidget = presenterWidgetAProvider.get();
    
    // Given, When, Then
    assertFalse( presenterWidget.isVisible() );    
  }

  @Test
  public void onRevealMakesPresenterWidgetVisible() {
    // Set-up
    PresenterWidget presenterWidget = presenterWidgetAProvider.get();
    
    // Given, When
    presenterWidget.onReveal();
    
    // Then
    assertTrue( presenterWidget.isVisible() );    
  }
    
  @Test
  public void testSetContentInEmptySlotWhenNotVisible() {
    // Set-up
    PresenterWidget presenterWidgetA = presenterWidgetAProvider.get();
    Object slotB = new Object();
    Object slotC = new Object();
    PresenterWidget contentB = presenterWidgetBProvider.get();
    PresenterWidget contentC = presenterWidgetCProvider.get();
    
    // Given
    // slot is empty in presenterWidget, and it is NOT visible
    assertFalse( presenterWidgetA.isVisible() );
    
    // When
    presenterWidgetA.setContent(slotB, contentB);
    presenterWidgetA.setContent(slotC, contentC);

    // Then
    verify(viewAProvider.get()).setContent( slotB, null );
    verify(viewAProvider.get()).setContent( slotC, null );
    
  }

  // TODO Many more tests

}
