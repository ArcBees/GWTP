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

package com.gwtplatform.mvp.client.proxy;

import java.util.Collections;

import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;

import com.gwtplatform.tester.DeferredCommandManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestSingleton;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Unit tests for {@link PlaceManagerImpl}.
 *
 * @author Philippe Beaudoin
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
  @Inject PlaceManager placeManager;

  @Inject DeferredCommandManager deferredCommandManager;
  @Inject EventBus eventBus;
  @Inject PlaceManagerWindowMethodsTestUtil gwtWindowMethods;

  @Test
  public void placeManagerUserCallUpdateHistoryWhenRevealingPlace() {
    // Given
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        deferredCommandManager.addCommand(new Command() {
          @Override
          public void execute() {
            placeManager.updateHistory(new PlaceRequest("dummyNameToken").with("dummyParam", "dummyValue"), true);
          } });
        ((PlaceRequestInternalEvent) args[0]).setHandled();
        return null;
      }
    }).when(eventBus).fireEventFromSource(isA(PlaceRequestInternalEvent.class), eq(placeManager));

    // When
    placeManager.revealPlace(new PlaceRequest("dummyNameToken"));
    deferredCommandManager.pump();

    // Then
    PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
    assertEquals("dummyNameToken", placeRequest.getNameToken());
    assertEquals(1, placeRequest.getParameterNames().size());
    assertEquals("dummyValue", placeRequest.getParameter("dummyParam", null));

    verify(gwtWindowMethods).setBrowserHistoryToken(any(String.class), eq(false));
  }

  @Test
  public void placeManagerRevealRelativePlaceWithZeroLevelShouldGoToDefaultPlace() {
    // Given
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        deferredCommandManager.addCommand(new Command() {
          @Override
          public void execute() {
            placeManager.updateHistory(new PlaceRequest("defaultPlace"), true);
          } });
        ((PlaceRequestInternalEvent) args[0]).setHandled();
        return null;
      }
    }).when(eventBus).fireEventFromSource(isA(PlaceRequestInternalEvent.class), eq(placeManager));

    // When
    placeManager.revealRelativePlace(0);
    deferredCommandManager.pump();

    // Then
    PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
    assertEquals("defaultPlace", placeRequest.getNameToken());
    assertEquals(0, placeRequest.getParameterNames().size());

    verify(gwtWindowMethods).setBrowserHistoryToken(any(String.class), eq(false));
  }

  @Test
  public void placeManagerRevealPlaceHierarchyWithEmptyHierarchyShouldGoToDefaultPlace() {
    // Given
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        deferredCommandManager.addCommand(new Command() {
          @Override
          public void execute() {
            placeManager.updateHistory(new PlaceRequest("defaultPlace"), true);
          } });
        ((PlaceRequestInternalEvent) args[0]).setHandled();
        return null;
      }
    }).when(eventBus).fireEventFromSource(isA(PlaceRequestInternalEvent.class), eq(placeManager));

    // When
    placeManager.revealPlaceHierarchy(Collections.<PlaceRequest>emptyList());
    deferredCommandManager.pump();

    // Then
    PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
    assertEquals("defaultPlace", placeRequest.getNameToken());
    assertEquals(0, placeRequest.getParameterNames().size());

    verify(gwtWindowMethods).setBrowserHistoryToken(any(String.class), eq(false));
  }
}
