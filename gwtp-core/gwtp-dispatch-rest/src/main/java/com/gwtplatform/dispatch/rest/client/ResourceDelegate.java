/**
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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * Delegate used to build and call HTTP resources. You can inject this interface instead of injecting your resource
 * interface and {@link RestDispatch} to simplify your code. Note that if your resource interfaces don't return {@link
 * com.gwtplatform.dispatch.rest.shared.RestAction RestAction&lt;?&gt;}s but the result type directly, you will need to
 * inject a {@link ResourceDelegate} to use your resource.
 * <p/>
 * This delegate will not send the HTTP request until you call a method, that is not a sub-resource, from the underlying
 * resource. The underlying resource is returned when either {@link #withoutCallback()} or {@link
 * #withCallback(AsyncCallback)} are called.
 * <p/>
 * The following example shows how to retrieve the {@link DispatchRequest} and delete a potential car:
 * <pre><code>
 * {@literal @}Path("/cars")
 * public interface CarsResource {
 *      CarResource car(int id);
 * }
 *
 * public interface CarResource {
 *      {@literal @}DELETE
 *      void delete();
 * }
 *
 * public class CarPresenter {
 *     private final ResourceDelegate&lt;CarsResource&gt; carsResourceDelegate;
 *
 *     {@literal @}Inject
 *     CarPresenter(ResourceDelegate&lt;CarsResource&gt; carsResourceDelegate) {
 *         this.carsResourceDelegate = carsResourceDelegate;
 *     }
 *
 *     {@literal @}Override
 *     public void onReveal() {
 *         DelegatingDispatchRequest dispatchRequest = new DelegatingDispatchRequest();
 *
 *         carsResourceDelegate
 *                 .withDelegatingDispatchRequest(dispatchRequest)
 *                 .withCallback(new AsyncCallback&lt;Void&gt;() {/{@literal * snip *}/});
 *                 .car(8)
 *                 .delete();
 *     }
 * }
 * </code></pre>
 *
 * @param <T> The type of the resource used by this delegate.
 */
public interface ResourceDelegate<T> {
    /**
     * Used as a mean to access the {@link DispatchRequest} instance returned by the underlying HTTP call. This may be
     * useful for canceling a long running call. {@code delegatingDispatchRequest} will be populated when the HTTP call
     * is sent, that is when you call any method from the service used by this delegate.
     *
     * @param delegatingDispatchRequest the {@link DelegatingDispatchRequest} to populate when the HTTP call is sent.
     *
     * @return a copy of this {@link ResourceDelegate} using the provided {@link DelegatingDispatchRequest}.
     */
    ResourceDelegate<T> withDelegatingDispatchRequest(DelegatingDispatchRequest delegatingDispatchRequest);

    /**
     * Provide the callback when the HTTP call returns or if any error occur.
     *
     * @param callback The callback to use when the HTTP call returns or if any error occur.
     *
     * @return the service wrapped by this delegate.
     */
    T withCallback(AsyncCallback<?> callback);

    /**
     * This method is the same as colling {@link #withCallback(AsyncCallback)} with a no-op callback.
     */
    T withoutCallback();
}
