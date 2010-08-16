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

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.gwtplatform.testing.GuiceMockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for {@link HandlerContainerImpl}.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(GuiceMockitoJUnitRunner.class)
public class HandlerContainerImplTest {

  // A subclass of HandlerContainerImpl that does not use autobinding
  // and counts its inherited method invocations
  static class NonAutoboundHandlerContainer extends HandlerContainerImpl {
    public int onBindMethodCalled = 0;
    public int onUnbindMethodCalled = 0;

    @Inject
    NonAutoboundHandlerContainer() {
      super(false);
    }

    @Override
    protected void onBind() {
      super.onBind();
      onBindMethodCalled++;
    }

    @Override
    protected void onUnbind() {
      super.onUnbind();
      onUnbindMethodCalled++;
    }
  }

  // Providers to use Guice injection
  @Inject
  Provider<HandlerContainerImpl> defaultHandlerContainerProvider;
  @Inject
  Provider<NonAutoboundHandlerContainer> nonAutoboundHandlerContainerProvider;

  @Test
  public void callingBindShouldInvokeOnBind() {
    // Setup
    NonAutoboundHandlerContainer handlerContainer = nonAutoboundHandlerContainerProvider.get();

    // Given
    handlerContainer.bind();

    // When, Then
    assertEquals(1, handlerContainer.onBindMethodCalled);
  }

  @Test
  public void callingUnbindShouldInvokeOnUnbind() {
    // Setup
    NonAutoboundHandlerContainer handlerContainer = nonAutoboundHandlerContainerProvider.get();

    // Given
    handlerContainer.bind();
    handlerContainer.unbind();

    // When, Then
    assertEquals(1, handlerContainer.onUnbindMethodCalled);
  }

  @Test
  public void callingUnbindWhenUnboundShouldNotInvokeOnUnbind() {
    // Setup
    NonAutoboundHandlerContainer handlerContainer = nonAutoboundHandlerContainerProvider.get();

    // Given
    handlerContainer.bind();
    handlerContainer.unbind();
    handlerContainer.unbind();
    handlerContainer.bind();
    handlerContainer.unbind();
    handlerContainer.unbind();

    // When, Then
    assertEquals(2, handlerContainer.onUnbindMethodCalled);
  }

  @Test
  public void shouldBindDefaultHandlerContainerOnInjection() {
    // Set-up
    HandlerContainer handlerContainer = defaultHandlerContainerProvider.get();

    // Given
    // HandlerContainerImpl is injected

    // When, Then
    // the bind method should be injected at creation time
    assertTrue(handlerContainer.isBound());
  }

  @Test
  public void shouldNotBindNonAutoboundHandlerContainerOnInjection() {
    // Set-up
    HandlerContainer handlerContainer = nonAutoboundHandlerContainerProvider.get();

    // Given
    // A non-autobound subclass is injected

    // When, Then
    // the bind method should NOT be invoked at creation time
    assertFalse(handlerContainer.isBound());
  }

  @Test
  public void unbindingMultipleTimesRemoveHandlersOnlyOnce() {
    // Setup
    HandlerContainerImpl handlerContainer = defaultHandlerContainerProvider.get();
    HandlerRegistration mockHandlerRegistration = mock(HandlerRegistration.class);

    // Given
    handlerContainer.registerHandler(mockHandlerRegistration);

    // When
    handlerContainer.unbind();
    handlerContainer.bind();
    handlerContainer.unbind();
    handlerContainer.unbind();

    // Then
    verify(mockHandlerRegistration).removeHandler();
  }

  @Test
  public void unbindingRemoveHandlers() {
    // Setup
    HandlerContainerImpl handlerContainer = defaultHandlerContainerProvider.get();
    HandlerRegistration mockHandlerRegistration1 = mock(HandlerRegistration.class);
    HandlerRegistration mockHandlerRegistration2 = mock(HandlerRegistration.class);

    // Given
    handlerContainer.registerHandler(mockHandlerRegistration1);
    handlerContainer.registerHandler(mockHandlerRegistration2);

    // When
    handlerContainer.unbind();

    // Then
    verify(mockHandlerRegistration1).removeHandler();
    verify(mockHandlerRegistration2).removeHandler();
  }

}
