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

package com.gwtplatform.mvp.client.proxy;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.gwtplatform.mvp.client.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImplTest.GwtWindowMethods;
import com.gwtplatform.test.DeferredCommandManager;
import com.gwtplatform.test.mockito.GuiceMockitoJUnitRunner;
import com.gwtplatform.test.mockito.TestModule;
import com.gwtplatform.test.mockito.TestScope;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
/**
 * Unit tests for {@link PlaceManagerImpl}.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class PlaceManagerImpl2Test {

  // Guice environment
  /**
   * @author Philippe Beaudoin
   */
  public static class Env extends TestModule {
    @Override
    protected void configure() {
      GWTMockUtilities.disarm();
      bind(DeferredCommandManager.class).in(TestScope.SINGLETON);
      bindMock(EventBus.class).in(TestScope.SINGLETON);
      bindMock(TokenFormatter.class).in(TestScope.SINGLETON);
      bindMock(ProxyFailureHandler.class).in(TestScope.SINGLETON);
      bindMock(GwtWindowMethods.class).in(TestScope.SINGLETON);
      bind(PlaceManager.class).to(PlaceManagerImplTest.MyPlaceManager.class).in(TestScope.SINGLETON);
    }
  }
  
  // Providers to use Guice injection
  @Inject
  Provider<DeferredCommandManager> deferredCommandManagerProvider;
  @Inject
  Provider<PlaceManager> placeManagerProvider;
  @Inject
  Provider<EventBus> eventBusProvider;
  @Inject
  Provider<GwtWindowMethods> gwtWindowMethodsProvider;
  
  @Test
  public void placeManagerUserCallUpdateHistoryWhenRevealingPlace() {
    // Given
    final DeferredCommandManager deferredCommandManager = deferredCommandManagerProvider.get();
    final PlaceManager placeManager = placeManagerProvider.get();
    EventBus eventBus = eventBusProvider.get();
    GwtWindowMethods gwtWindowMethods = gwtWindowMethodsProvider.get();
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        deferredCommandManager.addCommand(new Command() {
          @Override
          public void execute() {
            placeManager.updateHistory(new PlaceRequest("dummyNameToken").with("dummyParam", "dummyValue"));
          } });
        ((PlaceRequestInternalEvent) args[1]).setHandled();
        return null;
      }
    }).when(eventBus).fireEvent(eq(placeManager), isA(PlaceRequestInternalEvent.class));
    
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
}
