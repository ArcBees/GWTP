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

package com.gwtplatform.dispatch.rest.client.interceptor;

import java.util.List;

import com.gwtplatform.dispatch.client.interceptor.Interceptor;
import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * Instances of this interface will handle specific types of action classes on the client.
 * <p>
 * When a call is executed, the {@link Interceptor} that has been registered with the bound
 * {@link com.gwtplatform.dispatch.client.interceptor.InterceptorRegistry InterceptorRegistry} is called and
 * {@code com.gwtplatform.dispatch.rest.shared.RestDispatch} does not automatically
 * send the command over HTTP to the server.
 * <p>
 * Rest Interceptors provide a number of flexible options:
 * <ul>
 * <li>The action can be modified before sending the action to the server.</li>
 * <li>A result can be returned without contacting the server.</li>
 * <li>The result can be modified or processed after it is returned from the server.</li>
 * <li>The {@link Interceptor} can take over and communicate directly with the server, possibly using a
 * different mechanism.</li>
 * </ul>
 * <p>
 * <b>Important!</b> If your interceptor makes asynchronous calls, be careful with your use of fields as a second
 * call your interceptor could be made while it is waiting for the asynchronous call to return.
 * <p>
 * <b>Example:</b>
 * <pre>
 *   public AddItemInterceptor extends AbstractRestInterceptor {
 *      public AddItemInterceptor() {
 *          super(new InterceptorContext("/items", HttpMethod.POST, -1, false));
 *      }
 *
 *      {@literal @}Override
 *      public DispatchRequest execute(RestAction action, AsyncCallback{@literal <}Object{@literal >} resultCallback,
 *              ExecuteCommand&lt;RestAction, Object&gt; executeCommand) {
 *          // process stuff here!
 *          return executeCommand.execute(action, resultCallback);
 *      }
 *   }
 * </pre>
 */
public interface RestInterceptor extends Interceptor<RestAction, Object> {
    /**
     * Get rest interceptor contexts.
     */
    List<InterceptorContext> getInterceptorContexts();
}
