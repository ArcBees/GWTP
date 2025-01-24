/*
 * Copyright 2011 ArcBees Inc.
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

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestSingleton;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.presenter.slots.Slot;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PresenterWidget}.
 */
@RunWith(JukitoRunner.class)
public class PresenterWidgetTest {

    /**
     * Guice test module.
     */
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            GWTMockUtilities.disarm();
            forceMock(Widget.class);
        }
    }

    @TestSingleton
    static class PresenterWidgetA extends PresenterWidgetSpy<View> {
        @Inject
        PresenterWidgetA(EventBus eventBus, @Named("A") View view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetB extends PresenterWidgetSpy<View> {
        @Inject
        PresenterWidgetB(EventBus eventBus, @Named("B") View view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetC extends PresenterWidgetSpy<View> {
        @Inject
        PresenterWidgetC(EventBus eventBus, @Named("C") View view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetD extends PresenterWidget<View> {
        @Inject
        PresenterWidgetD(@Named("EventBusA") EventBus eventBus, @Named("A") PopupView view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetE extends PresenterWidgetSpy<View> {
        @Inject
        PresenterWidgetE(EventBus eventBus, @Named("E") View view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetF extends PresenterWidgetSpy<View> {
        @Inject
        PresenterWidgetF(EventBus eventBus, @Named("F") View view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetPopupB extends PresenterWidgetSpy<PopupView> {
        @Inject
        PresenterWidgetPopupB(EventBus eventBus, @Named("PopupB") PopupView view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetPopupC extends PresenterWidgetSpy<PopupView> {
        @Inject
        PresenterWidgetPopupC(EventBus eventBus, @Named("PopupC") PopupView view) {
            super(eventBus, view);
        }
    }

    // Simple subclasses of PresenterWidget
    abstract static class PresenterWidgetSpy<V extends View> extends PresenterWidget<V> {
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

    @Inject
    @Named("A")
    Widget widgetA;
    @Inject
    @Named("B")
    Widget widgetB;
    @Inject
    @Named("C")
    Widget widgetC;
    @Inject
    @Named("PopupB")
    Widget widgetPopupB;
    @Inject
    @Named("PopupC")
    Widget widgetPopupC;
    @Inject
    @Named("A")
    View viewA;
    @Inject
    @Named("B")
    View viewB;
    @Inject
    @Named("C")
    View viewC;
    @Inject
    @Named("PopupB")
    PopupView popupViewB;
    @Inject
    @Named("PopupC")
    PopupView popupViewC;
    @Inject
    @Named("EventBusA")
    EventBus eventBusA;
    @Inject
    GwtEvent.Type<EventHandler> typeA;
    @Inject
    EventHandler handlerA;
    @Inject
    HandlerRegistration registrationA;

    @Before
    public void arrange() {
        when(viewA.asWidget()).thenReturn(widgetA);
        when(viewB.asWidget()).thenReturn(widgetB);
        when(viewC.asWidget()).thenReturn(widgetC);
        when(popupViewB.asWidget()).thenReturn(widgetPopupB);
        when(popupViewC.asWidget()).thenReturn(widgetPopupC);
        when(eventBusA.addHandler(typeA, handlerA)).thenReturn(registrationA);
    }

    @Test
    public void onRevealMakesPresenterWidgetVisible(
            PresenterWidgetA presenterWidget) {
        // When
        presenterWidget.internalReveal();

        // Then
        assertTrue(presenterWidget.isVisible());
    }

    @Test
    public void presenterWidgetIsInitiallyNotVisible(
            PresenterWidgetA presenterWidget) {
        // Then
        assertEquals(0, presenterWidget.onRevealMethodCalled);
        assertEquals(0, presenterWidget.onHideMethodCalled);
        assertFalse(presenterWidget.isVisible());
    }

    @Test
    public void shouldHidePopupWhenPopupPresenterRemoved(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetPopupB popupContentB) {

        // Given
        presenterWidgetA.internalReveal();

        // When
        presenterWidgetA.addToPopupSlot(popupContentB);

        // Then
        verify(popupContentB.getView()).showAndReposition();

        assertEquals(1, popupContentB.onRevealMethodCalled);
        assertTrue(popupContentB.isVisible());

        // and When
        presenterWidgetA.removeFromPopupSlot(popupContentB);

        // Then
        verify(popupContentB.getView()).hide();
        assertEquals(1, popupContentB.onHideMethodCalled);
        assertFalse(popupContentB.isVisible());
    }

    @Test
    public void testAddAndRemoveVisibleHandler(
            PresenterWidgetD presenterWidgetD) {

        // Given
        assertFalse(presenterWidgetD.isVisible());
        presenterWidgetD.addVisibleHandler(typeA, handlerA);

        // when
        presenterWidgetD.internalReveal();

        // Then
        verify(eventBusA).addHandler(typeA, handlerA);

        // and then When
        presenterWidgetD.internalHide();

        // Then
        verify(registrationA).removeHandler();
    }

    @Test
    public void testAddPopupOnInitiallyInvisiblePresenter(
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
        verify(popupContentB.getView(), times(0)).showAndReposition();
        verify(popupContentC.getView(), times(0)).showAndReposition();
        verify(popupContentB.getView(), times(0)).hide();
        verify(popupContentC.getView(), times(0)).hide();

        assertEquals(0, popupContentB.onRevealMethodCalled);
        assertEquals(0, popupContentC.onRevealMethodCalled);

        // and then When
        presenterWidgetA.internalReveal();

        // Then
        assertEquals(1, popupContentB.onRevealMethodCalled);
        assertEquals(1, popupContentC.onRevealMethodCalled);
        verify(popupContentB.getView()).showAndReposition();
        verify(popupContentC.getView()).showAndReposition();

        // and then When
        presenterWidgetA.internalHide();

        // Then
        assertEquals(1, popupContentB.onRevealMethodCalled);
        assertEquals(1, popupContentC.onRevealMethodCalled);
        assertEquals(1, popupContentB.onHideMethodCalled);
        assertEquals(1, popupContentC.onHideMethodCalled);
        verify(popupContentB.getView()).showAndReposition();
        verify(popupContentC.getView()).showAndReposition();
        verify(popupContentB.getView()).hide();
        verify(popupContentC.getView()).hide();
    }

    // TODO Make sure the calls happen in the right order
    // parent then child for onReveal and onReset
    // child then parent for onHide

    @Test
    public void testAddToSlotToSlot(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB contentB,
            PresenterWidgetC contentC) {

        // Given
        Slot<PresenterWidget<?>> slotBC = new Slot<>();
        presenterWidgetA.internalReveal();

        // When
        presenterWidgetA.addToSlot(slotBC, contentB);
        presenterWidgetA.addToSlot(slotBC, contentC);

        // Then
        verify(viewA).addToSlot(slotBC, contentB);
        verify(viewA).addToSlot(slotBC, contentC);

        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentC.onRevealMethodCalled);

        // and When
        presenterWidgetA.clearSlot(slotBC);

        // Then
        verify(viewA).setInSlot(slotBC, null);

        assertEquals(1, contentB.onHideMethodCalled);
        assertEquals(1, contentC.onHideMethodCalled);
    }

    @Test
    public void testAddVisibleHandlerOnVisiblePresenter(
            PresenterWidgetD presenterWidgetD) {

        // Given
        assertFalse(presenterWidgetD.isVisible());
        // and
        presenterWidgetD.internalReveal();

        // when
        presenterWidgetD.addVisibleHandler(typeA, handlerA);

        // Then
        verify(eventBusA).addHandler(typeA, handlerA);

        // and then When
        presenterWidgetD.internalHide();

        // Then
        verify(registrationA).removeHandler();
    }

    @Test
    public void testClearContentInSlot(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB contentB) {
        // Given
        Slot<PresenterWidget<?>> slotB = new Slot<>();
        presenterWidgetA.internalReveal();
        presenterWidgetA.setInSlot(slotB, contentB);

        // When
        presenterWidgetA.clearSlot(slotB);

        // Then
        verify(viewA).setInSlot(slotB, null);

        assertEquals(1, contentB.onHideMethodCalled);

        // and then When
        presenterWidgetA.internalHide();

        // Then
        assertEquals(1, contentB.onHideMethodCalled);
    }

    @Test
    public void testPresenterWidgetCannotBeInMultipleSlots(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB contentB) {
        // Given
        Slot<PresenterWidget<?>> slotA = new Slot<>();
        Slot<PresenterWidget<?>> slotB = new Slot<>();
        presenterWidgetA.internalReveal();

        // When
        presenterWidgetA.setInSlot(slotA, contentB);
        presenterWidgetA.setInSlot(slotB, contentB);
        presenterWidgetA.clearSlot(slotB);
        contentB.internalReveal();
        presenterWidgetA.internalHide();

        // Then
        assertTrue(contentB.isVisible());
    }

    @Test
    public void testRemoveFromSlotFromSlot(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB contentB,
            PresenterWidgetC contentC) {
        // Given
        Slot<PresenterWidget<?>> slotBC = new Slot<>();
        presenterWidgetA.internalReveal();
        presenterWidgetA.addToSlot(slotBC, contentB);
        presenterWidgetA.addToSlot(slotBC, contentC);

        // When
        presenterWidgetA.removeFromSlot(slotBC, contentB);

        // Then
        verify(viewA).removeFromSlot(slotBC, contentB);

        assertEquals(1, contentB.onHideMethodCalled);
        assertEquals(0, contentC.onHideMethodCalled);
    }

    @Test
    public void testSetInSlotHierarchyInEmptySlotOnInitiallyInvisiblePresenter1(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB contentB,
            PresenterWidgetC contentCinB) {
        // Given
        // slot is empty in presenterWidgets, and it is NOT visible
        Slot<PresenterWidget<?>> slotB = new Slot<>();
        Slot<PresenterWidget<?>> slotC = new Slot<>();
        assertFalse(presenterWidgetA.isVisible());
        assertFalse(contentB.isVisible());

        // When
        presenterWidgetA.setInSlot(slotB, contentB);
        contentB.setInSlot(slotC, contentCinB);

        // Then
        verify(viewA).setInSlot(slotB, contentB);
        verify(viewB).setInSlot(slotC, contentCinB);

        assertEquals(0, contentB.onRevealMethodCalled);
        assertEquals(0, contentCinB.onRevealMethodCalled);

        // and then When
        presenterWidgetA.internalReveal();

        // Then
        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentCinB.onRevealMethodCalled);

        // and then When
        presenterWidgetA.internalHide();

        // Then
        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentCinB.onRevealMethodCalled);
        assertEquals(1, contentB.onHideMethodCalled);
        assertEquals(1, contentCinB.onHideMethodCalled);
    }

    @Test
    public void testSetInSlotHierarchyInEmptySlotOnInitiallyInvisiblePresenter2(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB contentB,
            PresenterWidgetC contentCinB) {
        // Given
        // slot is empty in presenterWidgets, and it is NOT visible
        Slot<PresenterWidget<?>> slotB = new Slot<>();
        Slot<PresenterWidget<?>> slotC = new Slot<>();
        assertFalse(presenterWidgetA.isVisible());
        assertFalse(contentB.isVisible());

        // When
        contentB.setInSlot(slotC, contentCinB);

        // Then
        verify(viewB).setInSlot(slotC, contentCinB);
        assertEquals(0, contentCinB.onRevealMethodCalled);

        // and then When
        presenterWidgetA.setInSlot(slotB, contentB);

        // Then
        verify(viewA).setInSlot(slotB, contentB);
        assertEquals(0, contentB.onRevealMethodCalled);

        // and then When
        presenterWidgetA.internalReveal();

        // Then
        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentCinB.onRevealMethodCalled);

        // and then When
        presenterWidgetA.internalHide();

        // Then
        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentCinB.onRevealMethodCalled);
        assertEquals(1, contentB.onHideMethodCalled);
        assertEquals(1, contentCinB.onHideMethodCalled);
    }

    @Test
    public void testSetInSlotHierarchyInEmptySlotOnInitiallyVisiblePresenter(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB contentB,
            PresenterWidgetC contentCinB) {

        // Given
        Slot<PresenterWidget<?>> slotB = new Slot<>();
        Slot<PresenterWidget<?>> slotC = new Slot<>();
        presenterWidgetA.internalReveal();

        // When
        presenterWidgetA.setInSlot(slotB, contentB);
        contentB.setInSlot(slotC, contentCinB);

        // Then
        verify(viewA).setInSlot(slotB, contentB);
        verify(viewB).setInSlot(slotC, contentCinB);

        // Then
        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentCinB.onRevealMethodCalled);

        // and then When
        presenterWidgetA.internalHide();

        // Then
        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentCinB.onRevealMethodCalled);
        assertEquals(1, contentB.onHideMethodCalled);
        assertEquals(1, contentCinB.onHideMethodCalled);
    }

    @Test
    public void testSetInSlotInEmptySlotOnInitiallyInvisiblePresenter(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB contentB,
            PresenterWidgetC contentC) {
        // Given
        // slot is empty in presenterWidget, and it is NOT visible
        Slot<PresenterWidget<?>> slotB = new Slot<>();
        Slot<PresenterWidget<?>> slotC = new Slot<>();
        assertFalse(presenterWidgetA.isVisible());

        // When
        presenterWidgetA.setInSlot(slotB, contentB);
        presenterWidgetA.setInSlot(slotC, contentC);

        // Then
        verify(viewA).setInSlot(slotB, contentB);
        verify(viewA).setInSlot(slotC, contentC);

        assertEquals(0, contentB.onRevealMethodCalled);
        assertEquals(0, contentC.onRevealMethodCalled);

        // and then When
        presenterWidgetA.internalReveal();

        // Then
        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentC.onRevealMethodCalled);

        // and then When
        presenterWidgetA.internalHide();

        // Then
        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentC.onRevealMethodCalled);
        assertEquals(1, contentB.onHideMethodCalled);
        assertEquals(1, contentC.onHideMethodCalled);
    }

    @Test
    public void testSetInSlotInEmptySlotOnInitiallyVisiblePresenter(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB contentB,
            PresenterWidgetC contentC) {
        // Given
        Slot<PresenterWidget<?>> slotB = new Slot<>();
        Slot<PresenterWidget<?>> slotC = new Slot<>();
        presenterWidgetA.internalReveal();

        // When
        presenterWidgetA.setInSlot(slotB, contentB);
        presenterWidgetA.setInSlot(slotC, contentC);

        // Then
        verify(viewA).setInSlot(slotB, contentB);
        verify(viewA).setInSlot(slotC, contentC);

        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentC.onRevealMethodCalled);

        // and then When
        presenterWidgetA.internalHide();

        // Then
        assertEquals(1, contentB.onRevealMethodCalled);
        assertEquals(1, contentC.onRevealMethodCalled);
        assertEquals(1, contentB.onHideMethodCalled);
        assertEquals(1, contentC.onHideMethodCalled);
    }

    @Test
    public void testSetNullContentInSlot(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB contentB) {
        // Given
        Slot<PresenterWidget<?>> slotB = new Slot<>();
        presenterWidgetA.internalReveal();
        presenterWidgetA.setInSlot(slotB, contentB);

        // When
        presenterWidgetA.setInSlot(slotB, null);

        // Then
        verify(viewA).setInSlot(slotB, null);

        assertEquals(1, contentB.onHideMethodCalled);

        // and then When
        presenterWidgetA.internalHide();

        // Then
        assertEquals(1, contentB.onHideMethodCalled);
    }

    @Test
    public void testSwitchPopupToAnotherPresenter1(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB presenterWidgetB,
            PresenterWidgetPopupC popupContentC) {
        // Given
        presenterWidgetA.internalReveal();
        presenterWidgetB.internalReveal();
        presenterWidgetA.addToPopupSlot(popupContentC);

        // When
        presenterWidgetB.addToPopupSlot(popupContentC);
        presenterWidgetB.internalHide();

        // Then
        assertFalse(popupContentC.isVisible());
    }

    @Test
    public void testSwitchPopupToAnotherPresenter2(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB presenterWidgetB,
            PresenterWidgetPopupC popupContentC) {
        // Given
        presenterWidgetA.internalReveal();
        presenterWidgetB.internalReveal();
        presenterWidgetA.addToPopupSlot(popupContentC);

        // When
        presenterWidgetB.addToPopupSlot(popupContentC);
        presenterWidgetB.internalHide();
        presenterWidgetA.addToPopupSlot(popupContentC);

        // Then
        assertTrue(popupContentC.isVisible());
    }

    @Test
    public void testSwitchPresenterWidgetToAnotherPresenter1(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB presenterWidgetB,
            PresenterWidgetC contentC) {
        // Given
        Slot<PresenterWidget<?>> slotCinA = new Slot<>();
        Slot<PresenterWidget<?>> slotCinB = new Slot<>();
        presenterWidgetA.internalReveal();
        presenterWidgetB.internalReveal();

        // When
        presenterWidgetA.setInSlot(slotCinA, contentC);
        presenterWidgetB.setInSlot(slotCinB, contentC);
        presenterWidgetB.internalHide();

        // Then
        assertFalse(contentC.isVisible());
    }

    @Test
    public void testSwitchPresenterWidgetToAnotherPresenter2(
            PresenterWidgetA presenterWidgetA,
            PresenterWidgetB presenterWidgetB,
            PresenterWidgetC contentC) {
        // Given
        Slot<PresenterWidget<?>> slotCinA = new Slot<>();
        Slot<PresenterWidget<?>> slotCinB = new Slot<>();
        presenterWidgetA.internalReveal();
        presenterWidgetB.internalReveal();

        // When
        presenterWidgetA.setInSlot(slotCinA, contentC);
        presenterWidgetB.setInSlot(slotCinB, contentC);
        presenterWidgetB.internalHide();
        presenterWidgetA.setInSlot(slotCinA, contentC);

        // Then
        assertTrue(contentC.isVisible());
    }
}
