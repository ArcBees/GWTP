/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gwtplatform.test;

import com.google.inject.AbstractModule;
import com.gwtplatform.dispatch.server.actionHandler.AbstractActionHandler;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

public abstract class MockHandlerModule extends AbstractModule {
	
	@Override
	protected void configure() {
		
		install(new TestDispatchModule());
		
		configureMockHandlers();
	}
	
	protected abstract void configureMockHandlers();

	protected <A extends Action<R>, R extends Result, H extends AbstractActionHandler<A, R>>void bindMockHandler(
			Class<H> handler,
			H mockHandler) {
		bind(handler).toProvider(new MockProvider<H>(mockHandler));
		
	}
}
