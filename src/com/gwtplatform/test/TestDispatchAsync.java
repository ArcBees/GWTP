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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.client.DispatchAsync;
import com.gwtplatform.dispatch.client.DispatchService;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

public class TestDispatchAsync implements DispatchAsync {

	private DispatchService service;
	
	@Inject
	public TestDispatchAsync(TestDispatchService service) {
		this.service = service;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <A extends Action<R>, R extends Result> void execute(A action,
			AsyncCallback<R> callback) {
		try {
			callback.onSuccess((R) service.execute("", action));
		} catch (Throwable caught) {
			callback.onFailure(caught);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public <A extends Action<R>, R extends Result> void undo(A action,
			R result, AsyncCallback<Void> callback) {
		try {
			service.undo("", (Action<Result>) action, result);
			callback.onSuccess(null);
		} catch (Throwable caught) {
			callback.onFailure(caught);
		}
	}

}
