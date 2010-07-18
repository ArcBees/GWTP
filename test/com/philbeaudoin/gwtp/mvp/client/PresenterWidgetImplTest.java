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
      bindMock(PopupViewB.class).in(TestScope.SINGLETON);
      bindMock(PopupViewC.class).in(TestScope.SINGLETON);
      bind(PresenterWidgetA.class).in(TestScope.SINGLETON);
      bind(PresenterWidgetB.class).in(TestScope.SINGLETON);
      bind(PresenterWidgetC.class).in(TestScope.SINGLETON);
    }
  }
  
  interface ViewA extends View {}
  interface ViewB extends View {}
  interface ViewC extends View {}
  interface PopupViewB extends PopupView {}
  interface PopupViewC extends PopupView {}
  
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
  static class PopupPresenterWidgetB extends PresenterWidgetSpy<PopupViewB> {
    @Inject
    public PopupPresenterWidgetB(EventBus eventBus, PopupViewB view) {
      super(eventBus, view);
    }
  }
  static class PopupPresenterWidgetC extends PresenterWidgetSpy<PopupViewC> {
    @Inject
    public PopupPresenterWidgetC(EventBus eventBus, PopupViewC view) {
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
  @Inject Provider<PopupPresenterWidgetB> popupPresenterWidgetBProvider;
  @Inject Provider<PopupPresenterWidgetC> popupPresenterWidgetCProvider;

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

  @Test
  public void testAddCenteredPopupContentOnInitiallyInvisiblePresenter() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    PopupPresenterWidgetB popupContentB = popupPresenterWidgetBProvider.get();
    PopupPresenterWidgetC popupContentC = popupPresenterWidgetCProvider.get();
    
    // Given
    // presenterWidget is NOT visible
    assertFalse( presenterWidgetA.isVisible() );
    
    // When
    presenterWidgetA.addPopupContent(popupContentB);
    presenterWidgetA.addPopupContent(popupContentC);

    // Then
    verify( popupContentB.getView(), times(0) ).show();
    verify( popupContentC.getView(), times(0) ).show();
    verify( popupContentB.getView(), times(0) ).hide();
    verify( popupContentC.getView(), times(0) ).hide();
    verify( popupContentB.getView() ).center();
    verify( popupContentC.getView() ).center();
    
    assertEquals( 0, popupContentB.onRevealMethodCalled );
    assertEquals( 0, popupContentC.onRevealMethodCalled );
    
    // and then When
    presenterWidgetA.notifyReveal();
    
    // Then
    assertEquals( 1, popupContentB.onRevealMethodCalled );
    assertEquals( 1, popupContentC.onRevealMethodCalled );
    verify( popupContentB.getView() ).show();
    verify( popupContentC.getView() ).show();
    
    // and then When
    presenterWidgetA.notifyHide();
    
    // Then
    assertEquals( 1, popupContentB.onRevealMethodCalled );
    assertEquals( 1, popupContentC.onRevealMethodCalled );
    assertEquals( 1, popupContentB.onHideMethodCalled );
    assertEquals( 1, popupContentC.onHideMethodCalled );
    verify( popupContentB.getView() ).show();
    verify( popupContentC.getView() ).show();
    verify( popupContentB.getView() ).hide();
    verify( popupContentC.getView() ).hide();
  }

  @Test
  public void testAddUncenteredPopupContentOnInitiallyInvisiblePresenter() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    PopupPresenterWidgetB popupContentB = popupPresenterWidgetBProvider.get();
    PopupPresenterWidgetC popupContentC = popupPresenterWidgetCProvider.get();
    
    // Given
    // presenterWidget is NOT visible
    assertFalse( presenterWidgetA.isVisible() );
    
    // When
    presenterWidgetA.addPopupContent(popupContentB, false);
    presenterWidgetA.addPopupContent(popupContentC, false);

    // Then
    verify( popupContentB.getView(), times(0) ).show();
    verify( popupContentC.getView(), times(0) ).show();
    verify( popupContentB.getView(), times(0) ).hide();
    verify( popupContentC.getView(), times(0) ).hide();
    
    assertEquals( 0, popupContentB.onRevealMethodCalled );
    assertEquals( 0, popupContentC.onRevealMethodCalled );
    
    // and then When
    presenterWidgetA.notifyReveal();
    
    // Then
    assertEquals( 1, popupContentB.onRevealMethodCalled );
    assertEquals( 1, popupContentC.onRevealMethodCalled );
    verify( popupContentB.getView() ).show();
    verify( popupContentC.getView() ).show();
    
    // and then When
    presenterWidgetA.notifyHide();
    
    // Then
    assertEquals( 1, popupContentB.onRevealMethodCalled );
    assertEquals( 1, popupContentC.onRevealMethodCalled );
    assertEquals( 1, popupContentB.onHideMethodCalled );
    assertEquals( 1, popupContentC.onHideMethodCalled );
    verify( popupContentB.getView() ).show();
    verify( popupContentC.getView() ).show();
    verify( popupContentB.getView() ).hide();
    verify( popupContentC.getView() ).hide();
        
    verify( popupContentB.getView(), times(0) ).center();
    verify( popupContentC.getView(), times(0) ).center();
  }

  @Test
  public void testAddCenteredPopupContentOnInitiallyVisiblePresenter() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    PopupPresenterWidgetB popupContentB = popupPresenterWidgetBProvider.get();
    PopupPresenterWidgetC popupContentC = popupPresenterWidgetCProvider.get();
    
    // Given
    presenterWidgetA.notifyReveal();
    
    // When
    presenterWidgetA.addPopupContent(popupContentB);
    presenterWidgetA.addPopupContent(popupContentC);

    // Then
    verify( popupContentB.getView() ).show();
    verify( popupContentC.getView() ).show();
    verify( popupContentB.getView() ).center();
    verify( popupContentC.getView() ).center();
    
    assertEquals( 1, popupContentB.onRevealMethodCalled );
    assertEquals( 1, popupContentC.onRevealMethodCalled );
    assertTrue( popupContentB.isVisible() );
    assertTrue( popupContentC.isVisible() );
    
    // and then When
    presenterWidgetA.notifyHide();
    
    // Then
    assertEquals( 1, popupContentB.onRevealMethodCalled );
    assertEquals( 1, popupContentC.onRevealMethodCalled );
    assertEquals( 1, popupContentB.onHideMethodCalled );
    assertEquals( 1, popupContentC.onHideMethodCalled );
    verify( popupContentB.getView() ).show();
    verify( popupContentC.getView() ).show();
    verify( popupContentB.getView() ).hide();
    verify( popupContentC.getView() ).hide();    

    // and then When
    presenterWidgetA.notifyReveal();
    
    // Then
    assertEquals( 2, popupContentB.onRevealMethodCalled );
    assertEquals( 2, popupContentC.onRevealMethodCalled );
    assertEquals( 1, popupContentB.onHideMethodCalled );
    assertEquals( 1, popupContentC.onHideMethodCalled );
    verify( popupContentB.getView(), times(2) ).show();
    verify( popupContentC.getView(), times(2) ).show();    
  }
  
  @Test
  public void testSwitchPopupToAnotherPresenter1() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    PresenterWidgetB presenterWidgetB = presenterWidgetBProvider.get();
    PopupPresenterWidgetC popupContentC = popupPresenterWidgetCProvider.get();
    
    // Given
    presenterWidgetA.notifyReveal();
    presenterWidgetB.notifyReveal();
    presenterWidgetA.addPopupContent(popupContentC);
    
    // When
    presenterWidgetB.addPopupContent(popupContentC);
    presenterWidgetB.notifyHide();
    
    // Then
    assertFalse( popupContentC.isVisible() );
  }  
  
  @Test
  public void testSwitchPopupToAnotherPresenter2() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    PresenterWidgetB presenterWidgetB = presenterWidgetBProvider.get();
    PopupPresenterWidgetC popupContentC = popupPresenterWidgetCProvider.get();
    
    // Given
    presenterWidgetA.notifyReveal();
    presenterWidgetB.notifyReveal();
    presenterWidgetA.addPopupContent(popupContentC);
    
    // When
    presenterWidgetB.addPopupContent(popupContentC);
    presenterWidgetB.notifyHide();
    presenterWidgetA.addPopupContent(popupContentC);
    
    // Then
    assertTrue( popupContentC.isVisible() );
  }  

  @Test
  public void testSwitchPresenterWidgetToAnotherPresenter1() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    PresenterWidgetB presenterWidgetB = presenterWidgetBProvider.get();
    Object slotCinA = new Object();
    Object slotCinB = new Object();
    PresenterWidgetC contentC = presenterWidgetCProvider.get();
    
    // Given
    presenterWidgetA.notifyReveal();
    presenterWidgetB.notifyReveal();
    
    // When
    presenterWidgetA.setContent(slotCinA, contentC);
    presenterWidgetB.setContent(slotCinB, contentC);
    presenterWidgetB.notifyHide();
    
    // Then
    assertFalse( contentC.isVisible() );  
  }  

  @Test
  public void testSwitchPresenterWidgetToAnotherPresenter2() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    PresenterWidgetB presenterWidgetB = presenterWidgetBProvider.get();
    Object slotCinA = new Object();
    Object slotCinB = new Object();
    PresenterWidgetC contentC = presenterWidgetCProvider.get();
    
    // Given
    presenterWidgetA.notifyReveal();
    presenterWidgetB.notifyReveal();
    
    // When
    presenterWidgetA.setContent(slotCinA, contentC);
    presenterWidgetB.setContent(slotCinB, contentC);
    presenterWidgetB.notifyHide();
    presenterWidgetA.setContent(slotCinA, contentC);
    
    // Then
    assertTrue( contentC.isVisible() );  
  }  

}
