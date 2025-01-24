/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client;

import com.google.gwt.http.client.Response;
import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * Hooks to be executed on every {@link com.gwtplatform.dispatch.client.DispatchCall}.
 * <p>
 * {@link #onExecute(RestAction)} will be called just before any action is attempted.<br>
 * {@link #onSuccess(RestAction, Response, Object)} will be called when any action succeeds.<br>
 * {@link #onFailure(RestAction, Response, Throwable)} will be called when any action fails.
 */
public interface RestDispatchHooks {
    void onExecute(RestAction<?> action);

    void onSuccess(RestAction<?> action, Response response, Object result);

    void onFailure(RestAction<?> action, Response response, Throwable caught);
}
