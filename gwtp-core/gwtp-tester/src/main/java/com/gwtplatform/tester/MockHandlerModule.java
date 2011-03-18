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

package com.gwtplatform.tester;

import com.google.inject.AbstractModule;
import com.google.inject.internal.UniqueAnnotations;

import com.gwtplatform.dispatch.client.actionhandler.AbstractClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Module for use in test cases when creating a guice injector that needs to
 * provide mock handlers.
 * 
 * Your injector must also have an class that subclasses {@link com.gwtplatform.dispatch.server.guice.HandlerModule}
 * to bind Actions to ActionHandlers and ActionValidators.
 * <p/>
 * You should subclass this module and use the {@link #configureMockHandlers()}
 * method to:
 * <ul>
 * <li>register mock server-side action handlers with
 * {@link #bindMockActionHandler(Class, ActionHandler)}.</li>
 * <li>register mock client-side action handlers with
 * {@link #bindMockClientActionHandler(Class, AbstractClientActionHandler)}.</li>
 * </ul>
 * 
 * <h3>Unit Testing Example</h3>
 * 
 * <pre>
 *  // create mock handlers
 *  CreateFooActionHandler mockCreateFooActionHandler =
 *       mock(CreateFooActionHandler.class);
 * 
 *  GeocodeAddressClientActionHandler geocodeAddressClientActionHandler =
 *       mock(GeocodeAddressClientActionHandler.class);
 *
 *  // create injector
 *  Injector injector =
 *      Guice.createInjector(new MyHandlerModule(),
 *          new MockHandlerModule() {
 *            {@literal}@Override
 *            protected void configureMockHandlers() {
 *              bindMockActionHandler(
 *                  CreateFooActionHandler.class,
 *                  mockCreateFooActionHandler);
 *              }
 *              bindMockClientActionHandler(
 *                  GeocodeAddressAction.class,
 *                  geocodeAddressClientActionHandler);
 *            });
 *            
 *  // get dispatcher
 *  DispatchAsync dispatcher = injector.getInstance(DispatchAsync.class);
 *
 *  // create mock result
 *  final CreateFooResult result =
 *      new CreateFooResult(new Key<Foo>(Foo.class, 1));
 *      
 *  // configure mockito to return mock result on specific action
 *  when(
 *    businessCreateActionHandler.execute(
 *        eq(new CreateFooAction("Bar")),
 *          any(ExecutionContext.class))).thenReturn(result);
 *            
 *  // configuring mockito to return result for clent action handler
 *  // is a bit more complex
 *  final GeocodeAddressResult geocodeAddressResult = new GeocodeAddressResult(...);
 *  doAnswer(new Answer&lt;Object&gt;() {
 *    {@literal @SuppressWarnings("unchecked")}
 *    public Object answer(InvocationOnMock invocation) {
 *      AsyncCallback&lt;GeocodeAddressResult&gt; callback
 *          = (AsyncCallback&lt;GeocodeAddressResult&gt;) invocation.getArguments()[1];
 *      callback.onSuccess(new GeocodeAddressResult(geocodeAddressResult));
 *      return null;
 *    }
 *  }).when(geocodeAddressClientActionHandler)
 *       .execute(
 *           eq(new GeocodeAddressAction("2 Google Way, New Zealand",
 *               "nz")), any(AsyncCallback.class),
 *           any(ClientDispatchRequest.class), any(ExecuteCommand.class));
 * 
 * </pre>
 * 
 * @author Brendan Doherty
 */
public abstract class MockHandlerModule extends AbstractModule {

  private static class MockClientActionHandlerMapImpl<A extends Action<R>, R extends Result>
      implements MockClientActionHandlerMap {

    private final Class<A> actionClass;
    private final ClientActionHandler<A, R> clientActionHandler;

    public MockClientActionHandlerMapImpl(final Class<A> actionClass,
        final ClientActionHandler<A, R> clientActionHandler) {
      this.actionClass = actionClass;
      this.clientActionHandler = clientActionHandler;
    }

    @Override
    public Class<A> getActionClass() {
      return actionClass;
    }

    @Override
    public ClientActionHandler<A, R> getClientActionHandler() {
      return clientActionHandler;
    }
  }

  @Override
  protected void configure() {

    install(new TestDispatchModule());

    configureMockHandlers();
  }

  protected abstract void configureMockHandlers();

  /**
   * Use bindMockActionHandler instead.
   */
  @Deprecated
  protected <A extends Action<R>, R extends Result, H extends ActionHandler<A, R>> void bindMockHandler(
      Class<H> handler, H mockHandler) {
    bindMockActionHandler(handler, mockHandler);
  }

  /**
   * Registers a mock server-side action handlers.
   * <p/>
   * This mock server-side action handler will be executed when the class under
   * test calls {@link com.gwtplatform.dispatch.client.DispatchAsync#execute
   * DispatchAsync#execute()} or
   * {@link com.gwtplatform.dispatch.client.DispatchAsync#undo
   * DispatchAsync#undo()}.
   * 
   * @param <A> Type of {@link Action} that will be executed by mock handler
   * @param <R> Type of {@link Result} that will be returned by mock handler
   * @param <H> Type of the mock handler that extends
   *          {@link ActionHandler}
   * @param handlerClass The type of the mock server-side handler
   * @param mockHandler Instance of the {@link ActionHandler} to execute
   *          actions of type {@literal <A>}
   * 
   */
  protected <A extends Action<R>, R extends Result, H extends ActionHandler<A, R>> void bindMockActionHandler(
      Class<H> handlerClass, H mockHandler) {
    bind(handlerClass).toProvider(new MockProvider<H>(mockHandler));
  }

  /**
   * Registers a mock client-side action handlers.
   * <p/>
   * This mock client-side action handler will be executed when the class under
   * test calls {@link com.gwtplatform.dispatch.client.DispatchAsync#execute
   * DispatchAsync#execute()} or
   * {@link com.gwtplatform.dispatch.client.DispatchAsync#undo
   * DispatchAsync#undo()}.
   * <p/>
   * 
   * If both mock client and mock server action handlers have been registered,
   * the server side action handler will only be called if the mock client side
   * action handler calls
   * {@link com.gwtplatform.dispatch.client.actionhandler.ExecuteCommand#execute
   * ExecuteCommand#execute()} or
   * {@link com.gwtplatform.dispatch.client.actionhandler.UndoCommand#undo
   * UndoCommand#undo()}
   * 
   * @param <A> Type of {@link Action}
   * @param <R> Type of {@link Result}
   * @param <H> Type of {@link AbstractClientActionHandler}
   * @param actionClass Implementation of {@link ActionHandler} to link
   *          and bind
   * @param mockHandler Instance of the {@link ActionHandler} to execute
   *          actions of type {@literal <A>}
   */
  protected <A extends Action<R>, R extends Result, H extends AbstractClientActionHandler<A, R>> void bindMockClientActionHandler(
      Class<A> actionClass, H mockHandler) {
    bind(MockClientActionHandlerMap.class).annotatedWith(
        UniqueAnnotations.create()).toInstance(
        new MockClientActionHandlerMapImpl<A, R>(actionClass, mockHandler));
  }
}
