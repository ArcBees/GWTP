package com.philbeaudoin.gwtp.mvp.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.philbeaudoin.gwtp.testing.InjectingMockitoJUnitRunner;

@RunWith(InjectingMockitoJUnitRunner.class)
public class HandlerContainerImplTest {

  // A subclass of HandlerContainerImpl that does not use autobinding
  // and checks ist contract-method invocations
  static class NonAutoboundHandlerContainer extends HandlerContainerImpl {
    public boolean onBindMethodCalled = false;
    public boolean onUnbindMethodCalled = false;

    @Inject 
    NonAutoboundHandlerContainer() { 
      super(false); 
    }

    @Override
    protected void onBind() { 
      super.onBind(); 
      onBindMethodCalled = true; 
    }

    @Override
    protected void onUnbind() { 
      super.onUnbind(); 
      onUnbindMethodCalled = true; 
    }  
  }

  // Providers for injection
  @Inject HandlerContainer defaultHandlerContainer;
  @Inject Provider<HandlerContainer> defaultHandlerContainerProvider;
  @Inject Provider<NonAutoboundHandlerContainer> nonAutoboundHandlerContainerProvider;

  @Test
  public void shouldBindDefaultHandlerContainerOnInjection() {
    // Set-up
    HandlerContainer handlerContainer = defaultHandlerContainer; //defaultHandlerContainerProvider.get();

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
    assertTrue(handlerContainer.onBindMethodCalled);
  }

  @Test
  public void callingUnbindShouldInvokeOnUnbind() {
    // Setup
    NonAutoboundHandlerContainer handlerContainer = nonAutoboundHandlerContainerProvider.get();

    // Given
    handlerContainer.unbind();

    // When, Then
    assertTrue(handlerContainer.onUnbindMethodCalled);
  }
  
  @Test
  public void unbindingRemoveHandlers() {
    // Setup
    HandlerContainerImpl handlerContainer = (HandlerContainerImpl) defaultHandlerContainerProvider.get();
    HandlerRegistration mockHandlerRegistration = mock(HandlerRegistration.class);

    // Given
    handlerContainer.registerHandler(mockHandlerRegistration);

    // When
    handlerContainer.unbind();
    
    // Then
    verify(mockHandlerRegistration).removeHandler();
  }

  @Test
  public void unbindingMultipleTimesRemoveHandlersOnlyOnce() {
    // Setup
    HandlerContainerImpl handlerContainer = (HandlerContainerImpl) defaultHandlerContainerProvider.get();
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

  public static class MyModule extends AbstractModule {
    protected void configure() {
      bind(HandlerContainer.class).to(HandlerContainerImpl.class);
      bind(NonAutoboundHandlerContainer.class);
    }
  }
}
