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

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import com.gwtplatform.tester.mockito.InjectBefore;
import com.gwtplatform.tester.mockito.GuiceMockitoJUnitRunner;
import com.gwtplatform.tester.mockito.InjectTest;
import com.gwtplatform.tester.mockito.TestModule;
import com.gwtplatform.tester.mockito.TestScope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * Unit tests for {@link PresenterWidget}.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class PresenterWidgetTest {
  // Guice environment
  /**
   * @author Philippe Beaudoin
   */
  public static class Env extends TestModule {
    @Override
    protected void configure() {
      bindMock(EventBus.class).in(TestScope.SINGLETON);
      bindNamedMock(View.class,"A").in(TestScope.SINGLETON);
      bindNamedMock(View.class,"B").in(TestScope.SINGLETON);
      bindNamedMock(View.class,"C").in(TestScope.SINGLETON);
      bindNamedMock(PopupView.class,"PopupB").in(TestScope.SINGLETON);
      bindNamedMock(PopupView.class,"PopupC").in(TestScope.SINGLETON);
      bind(PresenterWidgetA.class).in(TestScope.SINGLETON);
      bind(PresenterWidgetB.class).in(TestScope.SINGLETON);
      bind(PresenterWidgetC.class).in(TestScope.SINGLETON);
      bind(PresenterWidgetPopupB.class).in(TestScope.SINGLETON);
      bind(PresenterWidgetPopupC.class).in(TestScope.SINGLETON);
      bindNamedMock(Widget.class, "A").in(TestScope.SINGLETON);
      bindNamedMock(Widget.class, "B").in(TestScope.SINGLETON);
      bindNamedMock(Widget.class, "C").in(TestScope.SINGLETON);
      bindNamedMock(Widget.class, "PopupB").in(TestScope.SINGLETON);
      bindNamedMock(Widget.class, "PopupC").in(TestScope.SINGLETON);
    }
  }
  
  // Simple subclasses of PresenterWidgetImpl
  abstract static class PresenterWidgetSpy<V extends View> extends
      PresenterWidget<V> {
    public int onHideMethodCalled;
    public int onResetMethodCalled;
    public int onRevealMethodCalled;

    PresenterWidgetSpy(EventBus eventBus, V view) {
      super(eventBus, view);
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

    @Override
    protected void onReveal() {
      super.onReveal();
      onRevealMethodCalled++;
    }
  }
  
  static class PresenterWidgetA extends PresenterWidgetSpy<View> {
    @Inject
    PresenterWidgetA(EventBus eventBus, @Named("A") View view) {
      super(eventBus, view);
    }
  }
  
  static class PresenterWidgetB extends PresenterWidgetSpy<View> {
    @Inject
    PresenterWidgetB(EventBus eventBus, @Named("B") View view) {
      super(eventBus, view);
    }
  }
  
  static class PresenterWidgetC extends PresenterWidgetSpy<View> {
    @Inject
    PresenterWidgetC(EventBus eventBus, @Named("C") View view) {
      super(eventBus, view);
    }
  }
  static class PresenterWidgetPopupB extends PresenterWidgetSpy<PopupView> {
    @Inject
    PresenterWidgetPopupB(EventBus eventBus, @Named("PopupB") PopupView view) {
      super(eventBus, view);
    }
  }
  
  static class PresenterWidgetPopupC extends PresenterWidgetSpy<PopupView> {
    @Inject
    PresenterWidgetPopupC(EventBus eventBus, @Named("PopupC") PopupView view) {
      super(eventBus, view);
    }
  }
  
  @Before
  public void disarmGwt() {
    GWTMockUtilities.disarm();
  }

  @After
  public void rearmGwt() {
    GWTMockUtilities.restore();
  }
  
  @InjectBefore
  public void arrange(
      @Named("A") Widget widgetA,
      @Named("B") Widget widgetB,
      @Named("C") Widget widgetC,
      @Named("PopupB") Widget widgetPopupB,
      @Named("PopupC") Widget widgetPopupC,
      @Named("A") View viewA,
      @Named("B") View viewB,
      @Named("C") View viewC,
      @Named("PopupB") PopupView popupViewB,
      @Named("PopupC") PopupView popupViewC) {
    when(viewA.asWidget()).thenReturn(widgetA);
    when(viewB.asWidget()).thenReturn(widgetB);
    when(viewC.asWidget()).thenReturn(widgetC);
    when(popupViewB.asWidget()).thenReturn(widgetPopupB);
    when(popupViewC.asWidget()).thenReturn(widgetPopupC);
  }
  
  @InjectTest
  public void onRevealMakesPresenterWidgetVisible(
      PresenterWidgetA presenterWidget) {
    // When
    presenterWidget.reveal();

    // Then
    assertTrue(presenterWidget.isVisible());
  }

  @InjectTest
  public void presenterWidgetIsInitiallyNotVisible(
      PresenterWidgetA presenterWidget) {
    // Then
    assertEquals(0, presenterWidget.onRevealMethodCalled);
    assertEquals(0, presenterWidget.onHideMethodCalled);
    assertFalse(presenterWidget.isVisible());
  }

  @InjectTest
  public void testAddCenteredPopupOnInitiallyInvisiblePresenter(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetPopupB popupContentB,
      PresenterWidgetPopupC popupContentC) {

    // Given
    // presenterWidget is NOT visible
    assertFalse(presenterWidgetA.isVisible());

    // When
    presenterWidgetA.addToPopupSlot(popupContentB);
    presenterWidgetA.addToPopupSlot(popupContentC);

    // Then
    verify(popupContentB.getView(), times(0)).show();
    verify(popupContentC.getView(), times(0)).show();
    verify(popupContentB.getView(), times(0)).hide();
    verify(popupContentC.getView(), times(0)).hide();
    verify(popupContentB.getView()).center();
    verify(popupContentC.getView()).center();

    assertEquals(0, popupContentB.onRevealMethodCalled);
    assertEquals(0, popupContentC.onRevealMethodCalled);

    // and When
    presenterWidgetA.reveal();

    // Then
    assertEquals(1, popupContentB.onRevealMethodCalled);
    assertEquals(1, popupContentC.onRevealMethodCalled);
    verify(popupContentB.getView()).show();
    verify(popupContentC.getView()).show();

    // and When
    presenterWidgetA.hide();

    // Then
    assertEquals(1, popupContentB.onRevealMethodCalled);
    assertEquals(1, popupContentC.onRevealMethodCalled);
    assertEquals(1, popupContentB.onHideMethodCalled);
    assertEquals(1, popupContentC.onHideMethodCalled);
    verify(popupContentB.getView()).show();
    verify(popupContentC.getView()).show();
    verify(popupContentB.getView()).hide();
    verify(popupContentC.getView()).hide();
  }

  @InjectTest
  public void testAddCenteredPopupOnInitiallyVisiblePresenter(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetPopupB popupContentB,
      PresenterWidgetPopupC popupContentC) {

    // Given
    presenterWidgetA.reveal();

    // When
    presenterWidgetA.addToPopupSlot(popupContentB);
    presenterWidgetA.addToPopupSlot(popupContentC);

    // Then
    verify(popupContentB.getView()).show();
    verify(popupContentC.getView()).show();
    verify(popupContentB.getView()).center();
    verify(popupContentC.getView()).center();

    assertEquals(1, popupContentB.onRevealMethodCalled);
    assertEquals(1, popupContentC.onRevealMethodCalled);
    assertTrue(popupContentB.isVisible());
    assertTrue(popupContentC.isVisible());

    // and then When
    presenterWidgetA.hide();

    // Then
    assertEquals(1, popupContentB.onRevealMethodCalled);
    assertEquals(1, popupContentC.onRevealMethodCalled);
    assertEquals(1, popupContentB.onHideMethodCalled);
    assertEquals(1, popupContentC.onHideMethodCalled);
    verify(popupContentB.getView()).show();
    verify(popupContentC.getView()).show();
    verify(popupContentB.getView()).hide();
    verify(popupContentC.getView()).hide();

    // and then When
    presenterWidgetA.reveal();

    // Then
    assertEquals(2, popupContentB.onRevealMethodCalled);
    assertEquals(2, popupContentC.onRevealMethodCalled);
    assertEquals(1, popupContentB.onHideMethodCalled);
    assertEquals(1, popupContentC.onHideMethodCalled);
    verify(popupContentB.getView(), times(2)).show();
    verify(popupContentC.getView(), times(2)).show();
  }

  // TODO Make sure the calls happen in the right order
  // parent then child for onReveal and onReset
  // child then parent for onHide

  @InjectTest
  public void testAddToSlotToSlot(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB contentB,
      PresenterWidgetC contentC,
      @Named("A") View viewA,
      @Named("B") Widget widgetB,
      @Named("C") Widget widgetC) {

    // Given
    Object slotBC = new Object();
    presenterWidgetA.reveal();

    // When
    presenterWidgetA.addToSlot(slotBC, contentB);
    presenterWidgetA.addToSlot(slotBC, contentC);

    // Then
    verify(viewA).addToSlot(slotBC, widgetB);
    verify(viewA).addToSlot(slotBC, widgetC);

    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentC.onRevealMethodCalled);

    // and When
    presenterWidgetA.clearSlot(slotBC);

    // Then
    verify(viewA).setInSlot(slotBC, null);

    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentC.onHideMethodCalled);
  }

  @InjectTest
  public void testAddUncenteredPopupOnInitiallyInvisiblePresenter(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetPopupB popupContentB,
      PresenterWidgetPopupC popupContentC) {
    // Given
    // presenterWidget is NOT visible
    assertFalse(presenterWidgetA.isVisible());

    // When
    presenterWidgetA.addToPopupSlot(popupContentB, false);
    presenterWidgetA.addToPopupSlot(popupContentC, false);

    // Then
    verify(popupContentB.getView(), times(0)).show();
    verify(popupContentC.getView(), times(0)).show();
    verify(popupContentB.getView(), times(0)).hide();
    verify(popupContentC.getView(), times(0)).hide();

    assertEquals(0, popupContentB.onRevealMethodCalled);
    assertEquals(0, popupContentC.onRevealMethodCalled);

    // and then When
    presenterWidgetA.reveal();

    // Then
    assertEquals(1, popupContentB.onRevealMethodCalled);
    assertEquals(1, popupContentC.onRevealMethodCalled);
    verify(popupContentB.getView()).show();
    verify(popupContentC.getView()).show();

    // and then When
    presenterWidgetA.hide();

    // Then
    assertEquals(1, popupContentB.onRevealMethodCalled);
    assertEquals(1, popupContentC.onRevealMethodCalled);
    assertEquals(1, popupContentB.onHideMethodCalled);
    assertEquals(1, popupContentC.onHideMethodCalled);
    verify(popupContentB.getView()).show();
    verify(popupContentC.getView()).show();
    verify(popupContentB.getView()).hide();
    verify(popupContentC.getView()).hide();

    verify(popupContentB.getView(), times(0)).center();
    verify(popupContentC.getView(), times(0)).center();
  }

  @InjectTest
  public void testClearContentInSlot(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB contentB,
      @Named("A") View viewA) {
    // Given
    Object slotB = new Object();
    presenterWidgetA.reveal();
    presenterWidgetA.setInSlot(slotB, contentB);

    // When
    presenterWidgetA.clearSlot(slotB);

    // Then
    verify(viewA).setInSlot(slotB, null);

    assertEquals(1, contentB.onHideMethodCalled);

    // and then When
    presenterWidgetA.hide();

    // Then
    assertEquals(1, contentB.onHideMethodCalled);
  }

  @InjectTest
  public void testRemoveFromSlotFromSlot(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB contentB,
      PresenterWidgetC contentC,
      @Named("A") View viewA,
      @Named("B") Widget widgetB) {
    // Given
    Object slotBC = new Object();
    presenterWidgetA.reveal();
    presenterWidgetA.addToSlot(slotBC, contentB);
    presenterWidgetA.addToSlot(slotBC, contentC);

    // When
    presenterWidgetA.removeFromSlot(slotBC, contentB);

    // Then
    verify(viewA).removeFromSlot(slotBC, widgetB);

    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(0, contentC.onHideMethodCalled);
  }

  @InjectTest
  public void testSetInSlotHierarchyInEmptySlotOnInitiallyInvisiblePresenter1(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB contentB,
      PresenterWidgetC contentCinB,
      @Named("A") View viewA,
      @Named("B") View viewB,
      @Named("B") Widget widgetB,
      @Named("C") Widget widgetC) {
    // Given
    // slot is empty in presenterWidgets, and it is NOT visible
    Object slotB = new Object();
    Object slotC = new Object();
    assertFalse(presenterWidgetA.isVisible());
    assertFalse(contentB.isVisible());

    // When
    presenterWidgetA.setInSlot(slotB, contentB);
    contentB.setInSlot(slotC, contentCinB);

    // Then
    verify(viewA).setInSlot(slotB, widgetB);
    verify(viewB).setInSlot(slotC, widgetC);

    assertEquals(0, contentB.onRevealMethodCalled);
    assertEquals(0, contentCinB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.reveal();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.hide();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);
    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentCinB.onHideMethodCalled);
  }

  @InjectTest
  public void testSetInSlotHierarchyInEmptySlotOnInitiallyInvisiblePresenter2(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB contentB,
      PresenterWidgetC contentCinB,
      @Named("A") View viewA,
      @Named("B") View viewB,
      @Named("B") Widget widgetB,
      @Named("C") Widget widgetC) {
    // Given
    // slot is empty in presenterWidgets, and it is NOT visible
    Object slotB = new Object();
    Object slotC = new Object();
    assertFalse(presenterWidgetA.isVisible());
    assertFalse(contentB.isVisible());

    // When
    contentB.setInSlot(slotC, contentCinB);

    // Then
    verify(viewB).setInSlot(slotC, widgetC);
    assertEquals(0, contentCinB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.setInSlot(slotB, contentB);

    // Then
    verify(viewA).setInSlot(slotB, widgetB);
    assertEquals(0, contentB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.reveal();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.hide();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);
    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentCinB.onHideMethodCalled);
  }

  @InjectTest
  public void testSetInSlotHierarchyInEmptySlotOnInitiallyVisiblePresenter(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB contentB,
      PresenterWidgetC contentCinB,
      @Named("A") View viewA,
      @Named("B") View viewB,
      @Named("B") Widget widgetB,
      @Named("C") Widget widgetC) {

    // Given
    Object slotB = new Object();
    Object slotC = new Object();
    presenterWidgetA.reveal();

    // When
    presenterWidgetA.setInSlot(slotB, contentB);
    contentB.setInSlot(slotC, contentCinB);

    // Then
    verify(viewA).setInSlot(slotB, widgetB);
    verify(viewB).setInSlot(slotC, widgetC);

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.hide();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);
    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentCinB.onHideMethodCalled);
  }

  @InjectTest
  public void testSetInSlotInEmptySlotOnInitiallyInvisiblePresenter(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB contentB,
      PresenterWidgetC contentC,
      @Named("A") View viewA,
      @Named("B") View viewB,
      @Named("B") Widget widgetB,
      @Named("C") Widget widgetC) {
    // Given
    // slot is empty in presenterWidget, and it is NOT visible
    Object slotB = new Object();
    Object slotC = new Object();
    assertFalse(presenterWidgetA.isVisible());

    // When
    presenterWidgetA.setInSlot(slotB, contentB);
    presenterWidgetA.setInSlot(slotC, contentC);

    // Then
    verify(viewA).setInSlot(slotB, widgetB);
    verify(viewA).setInSlot(slotC, widgetC);

    assertEquals(0, contentB.onRevealMethodCalled);
    assertEquals(0, contentC.onRevealMethodCalled);

    // and then When
    presenterWidgetA.reveal();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentC.onRevealMethodCalled);

    // and then When
    presenterWidgetA.hide();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentC.onRevealMethodCalled);
    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentC.onHideMethodCalled);
  }

  @InjectTest
  public void testSetInSlotInEmptySlotOnInitiallyVisiblePresenter(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB contentB,
      PresenterWidgetC contentC,
      @Named("A") View viewA,
      @Named("B") Widget widgetB,
      @Named("C") Widget widgetC) {
    // Given
    Object slotB = new Object();
    Object slotC = new Object();
    presenterWidgetA.reveal();

    // When
    presenterWidgetA.setInSlot(slotB, contentB);
    presenterWidgetA.setInSlot(slotC, contentC);

    // Then
    verify(viewA).setInSlot(slotB, widgetB);
    verify(viewA).setInSlot(slotC, widgetC);

    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentC.onRevealMethodCalled);

    // and then When
    presenterWidgetA.hide();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentC.onRevealMethodCalled);
    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentC.onHideMethodCalled);
  }

  @InjectTest
  public void testSetNullContentInSlot(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB contentB,
      @Named("A") View viewA) {
    // Given
    Object slotB = new Object();
    presenterWidgetA.reveal();
    presenterWidgetA.setInSlot(slotB, contentB);

    // When
    presenterWidgetA.setInSlot(slotB, null);

    // Then
    verify(viewA).setInSlot(slotB, null);

    assertEquals(1, contentB.onHideMethodCalled);

    // and then When
    presenterWidgetA.hide();

    // Then
    assertEquals(1, contentB.onHideMethodCalled);
  }

  @InjectTest
  public void testSwitchPopupToAnotherPresenter1(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB presenterWidgetB,
      PresenterWidgetPopupC popupContentC) {
    // Given
    presenterWidgetA.reveal();
    presenterWidgetB.reveal();
    presenterWidgetA.addToPopupSlot(popupContentC);

    // When
    presenterWidgetB.addToPopupSlot(popupContentC);
    presenterWidgetB.hide();

    // Then
    assertFalse(popupContentC.isVisible());
  }

  @InjectTest
  public void testSwitchPopupToAnotherPresenter2(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB presenterWidgetB,
      PresenterWidgetPopupC popupContentC) {
    // Given
    presenterWidgetA.reveal();
    presenterWidgetB.reveal();
    presenterWidgetA.addToPopupSlot(popupContentC);

    // When
    presenterWidgetB.addToPopupSlot(popupContentC);
    presenterWidgetB.hide();
    presenterWidgetA.addToPopupSlot(popupContentC);

    // Then
    assertTrue(popupContentC.isVisible());
  }

  @InjectTest
  public void testSwitchPresenterWidgetToAnotherPresenter1(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB presenterWidgetB,
      PresenterWidgetC contentC) {
    // Given
    Object slotCinA = new Object();
    Object slotCinB = new Object();
    presenterWidgetA.reveal();
    presenterWidgetB.reveal();

    // When
    presenterWidgetA.setInSlot(slotCinA, contentC);
    presenterWidgetB.setInSlot(slotCinB, contentC);
    presenterWidgetB.hide();

    // Then
    assertFalse(contentC.isVisible());
  }

  @InjectTest
  public void testSwitchPresenterWidgetToAnotherPresenter2(
      PresenterWidgetA presenterWidgetA, 
      PresenterWidgetB presenterWidgetB,
      PresenterWidgetC contentC) {
    // Given
    Object slotCinA = new Object();
    Object slotCinB = new Object();
    presenterWidgetA.reveal();
    presenterWidgetB.reveal();

    // When
    presenterWidgetA.setInSlot(slotCinA, contentC);
    presenterWidgetB.setInSlot(slotCinB, contentC);
    presenterWidgetB.hide();
    presenterWidgetA.setInSlot(slotCinA, contentC);

    // Then
    assertTrue(contentC.isVisible());
  }

}
