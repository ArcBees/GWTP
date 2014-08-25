/**
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

import javax.inject.Inject;
import javax.inject.Named;

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
        PresenterWidgetA(final EventBus eventBus, @Named("A") final View view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetB extends PresenterWidgetSpy<View> {
        @Inject
        PresenterWidgetB(final EventBus eventBus, @Named("B") final View view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetC extends PresenterWidgetSpy<View> {
        @Inject
        PresenterWidgetC(final EventBus eventBus, @Named("C") final View view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetD extends PresenterWidget<View> {
        @Inject
        PresenterWidgetD(@Named("EventBusA") final EventBus eventBus, @Named("A") final PopupView view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetPopupB extends PresenterWidgetSpy<PopupView> {
        @Inject
        PresenterWidgetPopupB(final EventBus eventBus, @Named("PopupB") final PopupView view) {
            super(eventBus, view);
        }
    }

    @TestSingleton
    static class PresenterWidgetPopupC extends PresenterWidgetSpy<PopupView> {
        @Inject
        PresenterWidgetPopupC(final EventBus eventBus, @Named("PopupC") final PopupView view) {
            super(eventBus, view);
        }
    }

    // Simple subclasses of PresenterWidget
    abstract static class PresenterWidgetSpy<V extends View> extends PresenterWidget<V> {
        public int onHideMethodCalled;
        public int onResetMethodCalled;
        public int onRevealMethodCalled;

        PresenterWidgetSpy(final EventBus eventBus, final V view) {
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

    @Inject @Named("A") Widget widgetA;
    @Inject @Named("B") Widget widgetB;
    @Inject @Named("C") Widget widgetC;
    @Inject @Named("PopupB") Widget widgetPopupB;
    @Inject @Named("PopupC") Widget widgetPopupC;
    @Inject @Named("A") View viewA;
    @Inject @Named("B") View viewB;
    @Inject @Named("C") View viewC;
    @Inject @Named("PopupB") PopupView popupViewB;
    @Inject @Named("PopupC") PopupView popupViewC;
    @Inject @Named("EventBusA") EventBus eventBusA;
    @Inject GwtEvent.Type<EventHandler> typeA;
    @Inject EventHandler handlerA;
    @Inject HandlerRegistration registrationA;

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
    public void onRevealMakesPresenterWidgetVisible(final PresenterWidgetA presenterWidget) {
        // When
        presenterWidget.internalReveal();

        // Then
        assertTrue(presenterWidget.isVisible());
    }

    @Test
    public void presenterWidgetIsInitiallyNotVisible(final PresenterWidgetA presenterWidget) {
        // Then
        assertEquals(0, presenterWidget.onRevealMethodCalled);
        assertEquals(0, presenterWidget.onHideMethodCalled);
        assertFalse(presenterWidget.isVisible());
    }

    @Test
    public void shouldHidePopupWhenPopupPresenterRemoved(
            final PresenterWidgetA presenterWidgetA, final PresenterWidgetPopupB popupContentB) {

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
    public void testAddAndRemoveVisibleHandler(final PresenterWidgetD presenterWidgetD) {

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
    public void testAddPopupOnInitiallyInvisiblePresenter(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetPopupB popupContentB, final PresenterWidgetPopupC popupContentC) {
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
    public void testAddToSlotToSlot(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB contentB, final PresenterWidgetC contentC) {

        // Given
        final Object slotBC = new Object();
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
    public void testAddVisibleHandlerOnVisiblePresenter(final PresenterWidgetD presenterWidgetD) {

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
    public void testClearContentInSlot(final PresenterWidgetA presenterWidgetA, final PresenterWidgetB contentB) {
        // Given
        final Object slotB = new Object();
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
    public void testRemoveFromSlotFromSlot(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB contentB, final PresenterWidgetC contentC) {
        // Given
        final Object slotBC = new Object();
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
    public void testSetInSlotHierarchyInEmptySlotOnInitiallyInvisiblePresenter1(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB contentB, final PresenterWidgetC contentCinB) {
        // Given
        // slot is empty in presenterWidgets, and it is NOT visible
        final Object slotB = new Object();
        final Object slotC = new Object();
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
    public void testSetInSlotHierarchyInEmptySlotOnInitiallyInvisiblePresenter2(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB contentB, final PresenterWidgetC contentCinB) {
        // Given
        // slot is empty in presenterWidgets, and it is NOT visible
        final Object slotB = new Object();
        final Object slotC = new Object();
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
    public void testSetInSlotHierarchyInEmptySlotOnInitiallyVisiblePresenter(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB contentB, final PresenterWidgetC contentCinB) {

        // Given
        final Object slotB = new Object();
        final Object slotC = new Object();
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
    public void testSetInSlotInEmptySlotOnInitiallyInvisiblePresenter(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB contentB, final PresenterWidgetC contentC) {
        // Given
        // slot is empty in presenterWidget, and it is NOT visible
        final Object slotB = new Object();
        final Object slotC = new Object();
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
    public void testSetInSlotInEmptySlotOnInitiallyVisiblePresenter(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB contentB, final PresenterWidgetC contentC) {
        // Given
        final Object slotB = new Object();
        final Object slotC = new Object();
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
    public void testSetNullContentInSlot(final PresenterWidgetA presenterWidgetA, final PresenterWidgetB contentB) {
        // Given
        final Object slotB = new Object();
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
    public void testSwitchPopupToAnotherPresenter1(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB presenterWidgetB, final PresenterWidgetPopupC popupContentC) {
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
    public void testSwitchPopupToAnotherPresenter2(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB presenterWidgetB, final PresenterWidgetPopupC popupContentC) {
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
    public void testSwitchPresenterWidgetToAnotherPresenter1(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB presenterWidgetB, final PresenterWidgetC contentC) {
        // Given
        final Object slotCinA = new Object();
        final Object slotCinB = new Object();
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
    public void testSwitchPresenterWidgetToAnotherPresenter2(final PresenterWidgetA presenterWidgetA,
            final PresenterWidgetB presenterWidgetB, final PresenterWidgetC contentC) {
        // Given
        final Object slotCinA = new Object();
        final Object slotCinB = new Object();
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
