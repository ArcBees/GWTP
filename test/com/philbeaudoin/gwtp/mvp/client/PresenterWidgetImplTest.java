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
  
  // Simple subclasses of PresenterWidgetImpl
  static abstract class PresenterWidgetSpy<V extends View> extends PresenterWidgetImpl<V> {
    public int onRevealMethodCalled = 0;
    public int onHideMethodCalled = 0;
    public int onResetMethodCalled = 0;

    PresenterWidgetSpy(EventBus eventBus, V view) {
      super(eventBus, view);
    }

    @Override
    protected void onReveal() { 
      super.onReveal(); 
      onRevealMethodCalled++;
    }

    @Override
    protected void onHide() { 
      super.onHide(); 
      onHideMethodCalled++;
    }  
    
    @Override
    protected void onReset() { 
      super.onReset(); 
      onResetMethodCalled++;
    }  
  }
  
  static class PresenterWidgetA extends PresenterWidgetSpy<ViewA> {
    @Inject
    public PresenterWidgetA(EventBus eventBus, ViewA view) {
      super(eventBus, view);
    }
  }
  static class PresenterWidgetB extends PresenterWidgetSpy<ViewB> {
    @Inject
    public PresenterWidgetB(EventBus eventBus, ViewB view) {
      super(eventBus, view);
    }
  }
  static class PresenterWidgetC extends PresenterWidgetSpy<ViewC> {
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
    PresenterWidgetA presenterWidget = presenterWidgetAProvider.get();
    
    // Given, When, Then
    assertEquals( 0, presenterWidget.onRevealMethodCalled );
    assertEquals( 0, presenterWidget.onHideMethodCalled );
    assertFalse( presenterWidget.isVisible() );    
  }

  @Test
  public void onRevealMakesPresenterWidgetVisible() {
    // Set-up
    PresenterWidgetA presenterWidget = presenterWidgetAProvider.get();
    
    // Given, When
    presenterWidget.notifyReveal();
    
    // Then
    assertTrue( presenterWidget.isVisible() );    
  }
    
  @Test
  public void testSetContentInEmptySlotOnInitiallyInvisiblePresenter() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    Object slotB = new Object();
    Object slotC = new Object();
    PresenterWidgetB contentB = presenterWidgetBProvider.get();
    PresenterWidgetC contentC = presenterWidgetCProvider.get();
    
    // Given
    // slot is empty in presenterWidget, and it is NOT visible
    assertFalse( presenterWidgetA.isVisible() );
    
    // When
    presenterWidgetA.setContent(slotB, contentB);
    presenterWidgetA.setContent(slotC, contentC);

    // Then
    verify(viewAProvider.get()).setContent( slotB, null );
    verify(viewAProvider.get()).setContent( slotC, null );
    
    assertEquals( 0, contentB.onRevealMethodCalled );
    assertEquals( 0, contentC.onRevealMethodCalled );
    
    // and then When
    presenterWidgetA.notifyReveal();
    
    // Then
    assertEquals( 1, contentB.onRevealMethodCalled );
    assertEquals( 1, contentC.onRevealMethodCalled );
    
    // and then When
    presenterWidgetA.notifyHide();
    
    // Then
    assertEquals( 1, contentB.onRevealMethodCalled );
    assertEquals( 1, contentC.onRevealMethodCalled );
    assertEquals( 1, contentB.onHideMethodCalled );
    assertEquals( 1, contentC.onHideMethodCalled );
  }
  
  @Test
  public void testSetContentInEmptySlotOnInitiallyVisiblePresenter() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    Object slotB = new Object();
    Object slotC = new Object();
    PresenterWidgetB contentB = presenterWidgetBProvider.get();
    PresenterWidgetC contentC = presenterWidgetCProvider.get();
    
    // Given
    presenterWidgetA.notifyReveal();
    
    // When
    presenterWidgetA.setContent(slotB, contentB);
    presenterWidgetA.setContent(slotC, contentC);

    // Then
    verify(viewAProvider.get()).setContent( slotB, null );
    verify(viewAProvider.get()).setContent( slotC, null );
    
    assertEquals( 1, contentB.onRevealMethodCalled );
    assertEquals( 1, contentC.onRevealMethodCalled );
    
    // and then When
    presenterWidgetA.notifyHide();
    
    // Then
    assertEquals( 1, contentB.onRevealMethodCalled );
    assertEquals( 1, contentC.onRevealMethodCalled );
    assertEquals( 1, contentB.onHideMethodCalled );
    assertEquals( 1, contentC.onHideMethodCalled );
  }
  
  // TODO  Make sure the calls happen in the right order
  //       parent then child for onReveal and onReset
  //       child then parent for onHide
  
  @Test
  public void testSetContentHierarchyInEmptySlotOnInitiallyInvisiblePresenter1() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    Object slotB = new Object();
    Object slotC = new Object();
    PresenterWidgetB contentB = presenterWidgetBProvider.get();
    PresenterWidgetC contentCinB = presenterWidgetCProvider.get();
    
    // Given
    // slot is empty in presenterWidgets, and it is NOT visible
    assertFalse( presenterWidgetA.isVisible() );
    assertFalse( contentB.isVisible() );
    
    // When
    presenterWidgetA.setContent(slotB, contentB);
    contentB.setContent(slotC, contentCinB);

    // Then
    verify(viewAProvider.get()).setContent( slotB, null );
    verify(viewBProvider.get()).setContent( slotC, null );
    
    assertEquals( 0, contentB.onRevealMethodCalled );
    assertEquals( 0, contentCinB.onRevealMethodCalled );
    
    // and then When
    presenterWidgetA.notifyReveal();
    
    // Then
    assertEquals( 1, contentB.onRevealMethodCalled );
    assertEquals( 1, contentCinB.onRevealMethodCalled );
    
    // and then When
    presenterWidgetA.notifyHide();
    
    // Then
    assertEquals( 1, contentB.onRevealMethodCalled );
    assertEquals( 1, contentCinB.onRevealMethodCalled );
    assertEquals( 1, contentB.onHideMethodCalled );
    assertEquals( 1, contentCinB.onHideMethodCalled );
  }

  @Test
  public void testSetContentHierarchyInEmptySlotOnInitiallyInvisiblePresenter2() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    Object slotB = new Object();
    Object slotC = new Object();
    PresenterWidgetB contentB = presenterWidgetBProvider.get();
    PresenterWidgetC contentCinB = presenterWidgetCProvider.get();
    
    // Given
    // slot is empty in presenterWidgets, and it is NOT visible
    assertFalse( presenterWidgetA.isVisible() );
    assertFalse( contentB.isVisible() );
    
    // When
    contentB.setContent(slotC, contentCinB);

    // Then
    verify(viewBProvider.get()).setContent( slotC, null );    
    assertEquals( 0, contentCinB.onRevealMethodCalled );

    // and then When
    presenterWidgetA.setContent(slotB, contentB);

    // Then
    verify(viewAProvider.get()).setContent( slotB, null );
    assertEquals( 0, contentB.onRevealMethodCalled );
    
    // and then When
    presenterWidgetA.notifyReveal();
    
    // Then
    assertEquals( 1, contentB.onRevealMethodCalled );
    assertEquals( 1, contentCinB.onRevealMethodCalled );
    
    // and then When
    presenterWidgetA.notifyHide();
    
    // Then
    assertEquals( 1, contentB.onRevealMethodCalled );
    assertEquals( 1, contentCinB.onRevealMethodCalled );
    assertEquals( 1, contentB.onHideMethodCalled );
    assertEquals( 1, contentCinB.onHideMethodCalled );
  }
  
  @Test
  public void testSetContentHierarchyInEmptySlotOnInitiallyVisiblePresenter() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    Object slotB = new Object();
    Object slotC = new Object();
    PresenterWidgetB contentB = presenterWidgetBProvider.get();
    PresenterWidgetC contentCinB = presenterWidgetCProvider.get();
    
    // Given
    presenterWidgetA.notifyReveal();
    
    // When
    presenterWidgetA.setContent(slotB, contentB);
    contentB.setContent(slotC, contentCinB);

    // Then
    verify(viewAProvider.get()).setContent( slotB, null );
    verify(viewBProvider.get()).setContent( slotC, null );
    
    // Then
    assertEquals( 1, contentB.onRevealMethodCalled );
    assertEquals( 1, contentCinB.onRevealMethodCalled );
    
    // and then When
    presenterWidgetA.notifyHide();
    
    // Then
    assertEquals( 1, contentB.onRevealMethodCalled );
    assertEquals( 1, contentCinB.onRevealMethodCalled );
    assertEquals( 1, contentB.onHideMethodCalled );
    assertEquals( 1, contentCinB.onHideMethodCalled );
  }

}
