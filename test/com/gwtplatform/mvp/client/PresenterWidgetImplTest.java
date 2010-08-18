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
import com.google.inject.Provider;

import com.gwtplatform.testing.GuiceMockitoJUnitRunner;
import com.gwtplatform.testing.TestModule;
import com.gwtplatform.testing.TestScope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for {@link PresenterWidgetImpl}.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class PresenterWidgetImplTest {
  // Guice environment
  /**
   * @author Philippe Beaudoin
   */
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

  static class PopupPresenterWidgetB extends PresenterWidgetSpy<PopupViewB> implements PopupPresenter<PopupViewB> {
    @Inject
    public PopupPresenterWidgetB(EventBus eventBus, PopupViewB view) {
      super(eventBus, view);
    }
  }
  static class PopupPresenterWidgetC extends PresenterWidgetSpy<PopupViewC> implements PopupPresenter<PopupViewC> {
    @Inject
    public PopupPresenterWidgetC(EventBus eventBus, PopupViewC view) {
      super(eventBus, view);
    }
  }
  interface PopupViewB extends PopupView {
  }
  interface PopupViewC extends PopupView {
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
  // Simple subclasses of PresenterWidgetImpl
  abstract static class PresenterWidgetSpy<V extends View> extends
      PresenterWidgetImpl<V> {
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
  interface ViewA extends View {
  }
  interface ViewB extends View {
  }
  interface ViewC extends View {
  }

  // Providers to use Guice injection
  @Inject
  Provider<EventBus> EventBusProvider;
  @Inject
  Provider<PopupPresenterWidgetB> popupPresenterWidgetBProvider;
  @Inject
  Provider<PopupPresenterWidgetC> popupPresenterWidgetCProvider;
  @Inject
  Provider<PopupViewB> popupViewBProvider;
  @Inject
  Provider<PopupViewC> popupViewCProvider;
  @Inject
  Provider<PresenterWidgetA> presenterWidgetAProvider;
  @Inject
  Provider<PresenterWidgetB> presenterWidgetBProvider;
  @Inject
  Provider<PresenterWidgetC> presenterWidgetCProvider;
  @Inject
  Provider<ViewA> viewAProvider;
  @Inject
  Provider<ViewB> viewBProvider;
  @Inject
  Provider<ViewC> viewCProvider;

  Widget widgetA;
  Widget widgetB;
  Widget widgetC;
  Widget widgetPopupB;
  Widget widgetPopupC;

  @Before
  public void arrange() {
    GWTMockUtilities.disarm();
    widgetA = mock(Widget.class);
    widgetB = mock(Widget.class);
    widgetC = mock(Widget.class);
    widgetPopupB = mock(Widget.class);
    widgetPopupC = mock(Widget.class);
    when(viewAProvider.get().asWidget()).thenReturn(widgetA);
    when(viewBProvider.get().asWidget()).thenReturn(widgetB);
    when(viewCProvider.get().asWidget()).thenReturn(widgetC);
    when(popupViewBProvider.get().asWidget()).thenReturn(widgetPopupB);
    when(popupViewCProvider.get().asWidget()).thenReturn(widgetPopupC);
  }

  @Test
  public void onRevealMakesPresenterWidgetVisible() {
    // Set-up
    PresenterWidgetA presenterWidget = presenterWidgetAProvider.get();

    // Given, When
    presenterWidget.notifyReveal();

    // Then
    assertTrue(presenterWidget.isVisible());
  }

  @Test
  public void presenterWidgetIsInitiallyNotVisible() {
    // Set-up
    PresenterWidgetA presenterWidget = presenterWidgetAProvider.get();

    // Given, When, Then
    assertEquals(0, presenterWidget.onRevealMethodCalled);
    assertEquals(0, presenterWidget.onHideMethodCalled);
    assertFalse(presenterWidget.isVisible());
  }

  @After
  public void reEnableWidgets() {
    GWTMockUtilities.restore();
  }

  @Test
  public void testAddCenteredPopupContentOnInitiallyInvisiblePresenter() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    PopupPresenterWidgetB popupContentB = popupPresenterWidgetBProvider.get();
    PopupPresenterWidgetC popupContentC = popupPresenterWidgetCProvider.get();

    // Given
    // presenterWidget is NOT visible
    assertFalse(presenterWidgetA.isVisible());

    // When
    presenterWidgetA.addPopupContent(popupContentB);
    presenterWidgetA.addPopupContent(popupContentC);

    // Then
    verify(popupContentB.getView(), times(0)).show();
    verify(popupContentC.getView(), times(0)).show();
    verify(popupContentB.getView(), times(0)).hide();
    verify(popupContentC.getView(), times(0)).hide();
    verify(popupContentB.getView()).center();
    verify(popupContentC.getView()).center();

    assertEquals(0, popupContentB.onRevealMethodCalled);
    assertEquals(0, popupContentC.onRevealMethodCalled);

    // and then When
    presenterWidgetA.notifyReveal();

    // Then
    assertEquals(1, popupContentB.onRevealMethodCalled);
    assertEquals(1, popupContentC.onRevealMethodCalled);
    verify(popupContentB.getView()).show();
    verify(popupContentC.getView()).show();

    // and then When
    presenterWidgetA.notifyHide();

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
    verify(popupContentB.getView()).show();
    verify(popupContentC.getView()).show();
    verify(popupContentB.getView()).center();
    verify(popupContentC.getView()).center();

    assertEquals(1, popupContentB.onRevealMethodCalled);
    assertEquals(1, popupContentC.onRevealMethodCalled);
    assertTrue(popupContentB.isVisible());
    assertTrue(popupContentC.isVisible());

    // and then When
    presenterWidgetA.notifyHide();

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
    presenterWidgetA.notifyReveal();

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

  @Test
  public void testAddContentToSlot() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    Object slotBC = new Object();
    PresenterWidgetB contentB = presenterWidgetBProvider.get();
    PresenterWidgetC contentC = presenterWidgetCProvider.get();

    // Given
    presenterWidgetA.notifyReveal();

    // When
    presenterWidgetA.addContent(slotBC, contentB);
    presenterWidgetA.addContent(slotBC, contentC);

    // Then
    verify(viewAProvider.get()).addContent(slotBC, widgetB);
    verify(viewAProvider.get()).addContent(slotBC, widgetC);

    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentC.onRevealMethodCalled);

    // and then When
    presenterWidgetA.clearContent(slotBC);

    // Then
    verify(viewAProvider.get()).setContent(slotBC, null);

    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentC.onHideMethodCalled);
  }

  @Test
  public void testAddUncenteredPopupContentOnInitiallyInvisiblePresenter() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    PopupPresenterWidgetB popupContentB = popupPresenterWidgetBProvider.get();
    PopupPresenterWidgetC popupContentC = popupPresenterWidgetCProvider.get();

    // Given
    // presenterWidget is NOT visible
    assertFalse(presenterWidgetA.isVisible());

    // When
    presenterWidgetA.addPopupContent(popupContentB, false);
    presenterWidgetA.addPopupContent(popupContentC, false);

    // Then
    verify(popupContentB.getView(), times(0)).show();
    verify(popupContentC.getView(), times(0)).show();
    verify(popupContentB.getView(), times(0)).hide();
    verify(popupContentC.getView(), times(0)).hide();

    assertEquals(0, popupContentB.onRevealMethodCalled);
    assertEquals(0, popupContentC.onRevealMethodCalled);

    // and then When
    presenterWidgetA.notifyReveal();

    // Then
    assertEquals(1, popupContentB.onRevealMethodCalled);
    assertEquals(1, popupContentC.onRevealMethodCalled);
    verify(popupContentB.getView()).show();
    verify(popupContentC.getView()).show();

    // and then When
    presenterWidgetA.notifyHide();

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

  @Test
  public void testClearContentInSlot() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    Object slotB = new Object();
    PresenterWidgetB contentB = presenterWidgetBProvider.get();

    // Given
    presenterWidgetA.notifyReveal();
    presenterWidgetA.setContent(slotB, contentB);

    // When
    presenterWidgetA.clearContent(slotB);

    // Then
    verify(viewAProvider.get()).setContent(slotB, null);

    assertEquals(1, contentB.onHideMethodCalled);

    // and then When
    presenterWidgetA.notifyHide();

    // Then
    assertEquals(1, contentB.onHideMethodCalled);
  }

  @Test
  public void testRemoveContentFromSlot() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    Object slotBC = new Object();
    PresenterWidgetB contentB = presenterWidgetBProvider.get();
    PresenterWidgetC contentC = presenterWidgetCProvider.get();

    // Given
    presenterWidgetA.notifyReveal();
    presenterWidgetA.addContent(slotBC, contentB);
    presenterWidgetA.addContent(slotBC, contentC);

    // When
    presenterWidgetA.removeContent(slotBC, contentB);

    // Then
    verify(viewAProvider.get()).removeContent(slotBC, widgetB);

    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(0, contentC.onHideMethodCalled);
  }

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
    assertFalse(presenterWidgetA.isVisible());
    assertFalse(contentB.isVisible());

    // When
    presenterWidgetA.setContent(slotB, contentB);
    contentB.setContent(slotC, contentCinB);

    // Then
    verify(viewAProvider.get()).setContent(slotB, widgetB);
    verify(viewBProvider.get()).setContent(slotC, widgetC);

    assertEquals(0, contentB.onRevealMethodCalled);
    assertEquals(0, contentCinB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.notifyReveal();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.notifyHide();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);
    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentCinB.onHideMethodCalled);
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
    assertFalse(presenterWidgetA.isVisible());
    assertFalse(contentB.isVisible());

    // When
    contentB.setContent(slotC, contentCinB);

    // Then
    verify(viewBProvider.get()).setContent(slotC, widgetC);
    assertEquals(0, contentCinB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.setContent(slotB, contentB);

    // Then
    verify(viewAProvider.get()).setContent(slotB, widgetB);
    assertEquals(0, contentB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.notifyReveal();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.notifyHide();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);
    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentCinB.onHideMethodCalled);
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
    verify(viewAProvider.get()).setContent(slotB, widgetB);
    verify(viewBProvider.get()).setContent(slotC, widgetC);

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);

    // and then When
    presenterWidgetA.notifyHide();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentCinB.onRevealMethodCalled);
    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentCinB.onHideMethodCalled);
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
    assertFalse(presenterWidgetA.isVisible());

    // When
    presenterWidgetA.setContent(slotB, contentB);
    presenterWidgetA.setContent(slotC, contentC);

    // Then
    verify(viewAProvider.get()).setContent(slotB, widgetB);
    verify(viewAProvider.get()).setContent(slotC, widgetC);

    assertEquals(0, contentB.onRevealMethodCalled);
    assertEquals(0, contentC.onRevealMethodCalled);

    // and then When
    presenterWidgetA.notifyReveal();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentC.onRevealMethodCalled);

    // and then When
    presenterWidgetA.notifyHide();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentC.onRevealMethodCalled);
    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentC.onHideMethodCalled);
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
    verify(viewAProvider.get()).setContent(slotB, widgetB);
    verify(viewAProvider.get()).setContent(slotC, widgetC);

    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentC.onRevealMethodCalled);

    // and then When
    presenterWidgetA.notifyHide();

    // Then
    assertEquals(1, contentB.onRevealMethodCalled);
    assertEquals(1, contentC.onRevealMethodCalled);
    assertEquals(1, contentB.onHideMethodCalled);
    assertEquals(1, contentC.onHideMethodCalled);
  }

  @Test
  public void testSetNullContentInSlot() {
    // Set-up
    PresenterWidgetA presenterWidgetA = presenterWidgetAProvider.get();
    Object slotB = new Object();
    PresenterWidgetB contentB = presenterWidgetBProvider.get();

    // Given
    presenterWidgetA.notifyReveal();
    presenterWidgetA.setContent(slotB, contentB);

    // When
    presenterWidgetA.setContent(slotB, null);

    // Then
    verify(viewAProvider.get()).setContent(slotB, null);

    assertEquals(1, contentB.onHideMethodCalled);

    // and then When
    presenterWidgetA.notifyHide();

    // Then
    assertEquals(1, contentB.onHideMethodCalled);
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
    assertFalse(popupContentC.isVisible());
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
    assertTrue(popupContentC.isVisible());
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
    assertFalse(contentC.isVisible());
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
    assertTrue(contentC.isVisible());
  }

}
