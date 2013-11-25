/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.shared;

/**
 * Defines {@link RestAction}s to be used with {@link RestDispatch}. All interfaces extending {@link RestService} in
 * your classpathwill get their implementation generated at compile-time, making it possible to inject them with GIN.
 * <p/>
 * All methods defined here must return a {@link RestAction} and use
 * <a href="https://jax-rs-spec.java.net/nonav/2.0/apidocs/javax/ws/rs/package-summary.html">JAX-RS annotations</a>.
 */
public interface RestService {
}
