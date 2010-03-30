package com.philbeaudoin.gwtp.mvp.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.philbeaudoin.gwtp.testing.GuiceMockitoJUnitRunner;

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
  @Inject Provider<HandlerContainerImpl> defaultHandlerContainerProvider;
  @Inject Provider<NonAutoboundHandlerContainer> nonAutoboundHandlerContainerProvider;

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

}
