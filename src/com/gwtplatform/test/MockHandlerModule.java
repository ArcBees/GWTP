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

package com.gwtplatform.test;

import com.google.inject.AbstractModule;

import com.gwtplatform.dispatch.server.actionhandler.AbstractActionHandler;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Module for use in test cases when creating a guice injector that needs to
 * provide mock handlers.
 * 
 * Your injector must also have an class that subclasses {@link HandlerModule}
 * to bind Actions to ActionHandlers and ActionValidators.
 * 
 * You should subclass this module to register mock handlers in the
 * configureMockHandlers() method.
 * 
 * <pre>
 *  // create mock handler
 *  CreateFooActionHandler mockCreateFooActionHandler =
 *       mock(CreateFooActionHandler.class);
 * 
 *  // create injector
 *  Injector injector =
 *      Guice.createInjector(new MyHandlerModule(),
 *          new MockHandlerModule() {
 *            {@literal}@Override
 *            protected void configureMockHandlers() {
 *              bindMockHandler(
 *                  CreateFooActionHandler.class,
 *                  mockCreateFooActionHandler);
 *              }
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
 * </pre>
 * 
 * @author Brendan Doherty
 */
public abstract class MockHandlerModule extends AbstractModule {

  @Override
  protected void configure() {

    install(new TestDispatchModule());

    configureMockHandlers();
  }

  protected abstract void configureMockHandlers();

  protected <A extends Action<R>, R extends Result, H extends AbstractActionHandler<A, R>> void bindMockHandler(
      Class<H> handler, H mockHandler) {
    bind(handler).toProvider(new MockProvider<H>(mockHandler));
  }
}
