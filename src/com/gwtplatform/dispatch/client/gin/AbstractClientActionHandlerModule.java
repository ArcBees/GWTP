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

package com.gwtplatform.dispatch.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistryImpl;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Base module that will bind {@link Action}s to {@link ClientActionHandler}s.
 * Your own Gin modules should extend this class.
 * 
 * @author Brendan Doherty
 */
public abstract class AbstractClientActionHandlerModule extends
		AbstractGinModule {

	@Inject
	public static ClientActionHandlerRegistry registry;

	public AbstractClientActionHandlerModule() {
	}

	/**
	 * @param <A> Type of {@link Action}
	 * @param <R> Type of {@link Result}
	 * @param actionClass Implementation of {@link Action} to link and bind
	 * @param actionInterceptorClass Implementation of
	 *            {@link ClientActionHandler} to link and bind
	 */
	protected <A extends Action<R>, R extends Result> void bindClientActionHandler(
			Class<A> actionClass, ClientActionHandler<A, R> handler) {

		registry.addClientActionHandler(actionClass, handler);
	}

	@Override
	protected final void configure() {

		bind(ClientActionHandlerRegistry.class).to(
				ClientActionHandlerRegistryImpl.class).asEagerSingleton();

		//requestStaticInjection(ClientActionHandlerRegistry.class);

		// install(new DispatchAsyncModule());

		configureClientActionHandlers();
	}

	/**
	 * Override this method to configure your interceptors. Use calls to
	 * {@link #bindInterceptor()} to register action interceptors.
	 */
	protected abstract void configureClientActionHandlers();
}