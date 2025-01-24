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

package com.gwtplatform.mvp.client.proxy;

import java.util.Collections;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.tester.DeferredCommandManager;

import jakarta.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link PlaceManagerImpl}.
 */
@RunWith(JukitoRunner.class)
public class PlaceManagerImpl2Test {

    /**
     * Guice test module.
     */
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            GWTMockUtilities.disarm();
            bind(DeferredCommandManager.class).in(TestSingleton.class);
            bind(PlaceManager.class).to(PlaceManagerTestUtil.class).in(TestSingleton.class);
        }
    }

    // SUT
    @Inject
    PlaceManager placeManager;

    @Inject
    DeferredCommandManager deferredCommandManager;
    @Inject
    EventBus eventBus;
    @Inject
    PlaceManagerWindowMethodsTestUtil gwtWindowMethods;

    @Test
    public void placeManagerUserCallUpdateHistoryWhenRevealingPlace() {
        // Given
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            deferredCommandManager.addCommand(() -> placeManager.updateHistory(new PlaceRequest.Builder()
                    .nameToken("dummyNameToken").with("dummyParam", "dummyValue").build(), true));
            ((PlaceRequestInternalEvent) args[0]).setHandled();
            return null;
        }).when(eventBus).fireEventFromSource(isA(PlaceRequestInternalEvent.class), eq(placeManager));

        // When
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken("dummyNameToken").build());
        deferredCommandManager.pump();

        // Then
        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        assertEquals("dummyNameToken", placeRequest.getNameToken());
        assertEquals(1, placeRequest.getParameterNames().size());
        assertEquals("dummyValue", placeRequest.getParameter("dummyParam", null));

        verify(gwtWindowMethods).setBrowserHistoryToken(isNull(), eq(false));
    }

    @Test
    public void placeManagerRevealRelativePlaceWithZeroLevelShouldGoToDefaultPlace() {
        // Given
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            deferredCommandManager.addCommand(
                    () -> placeManager.updateHistory(new PlaceRequest.Builder().nameToken("defaultPlace").build(),
                            true));
            ((PlaceRequestInternalEvent) args[0]).setHandled();
            return null;
        }).when(eventBus).fireEventFromSource(isA(PlaceRequestInternalEvent.class), eq(placeManager));

        // When
        placeManager.revealRelativePlace(0);
        deferredCommandManager.pump();

        // Then
        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        assertEquals("defaultPlace", placeRequest.getNameToken());
        assertEquals(0, placeRequest.getParameterNames().size());

        verify(gwtWindowMethods).setBrowserHistoryToken(isNull(), eq(false));
    }

    @Test
    public void placeManagerRevealPlaceHierarchyWithEmptyHierarchyShouldGoToDefaultPlace() {
        // Given
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            deferredCommandManager.addCommand(
                    () -> placeManager.updateHistory(new PlaceRequest.Builder().nameToken("defaultPlace").build(),
                            true));
            ((PlaceRequestInternalEvent) args[0]).setHandled();
            return null;
        }).when(eventBus).fireEventFromSource(isA(PlaceRequestInternalEvent.class), eq(placeManager));

        // When
        placeManager.revealPlaceHierarchy(Collections.<PlaceRequest>emptyList());
        deferredCommandManager.pump();

        // Then
        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        assertEquals("defaultPlace", placeRequest.getNameToken());
        assertEquals(0, placeRequest.getParameterNames().size());

        verify(gwtWindowMethods).setBrowserHistoryToken(isNull(), eq(false));
    }
}
