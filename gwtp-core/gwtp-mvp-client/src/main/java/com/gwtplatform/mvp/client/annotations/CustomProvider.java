/*
 * Copyright 2011 ArcBees Inc.
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
package com.gwtplatform.mvp.client.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.gwtplatform.common.client.IndirectProvider;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Use this annotation with a {@link com.gwtplatform.mvp.client.proxy.Proxy Proxy} to specify a custom {@linkplain
 * com.gwtplatform.common.client.IndirectProvider provider} which is used to load the presenter behind the proxy. Using
 * a custom provider enables you to add steps which should happen <b>before</b> the presenter is loaded, instantiated
 * and processed by GIN.
 * <p>
 * The class implementing {@code IndirectProvider} must provide a constructor which takes the original provider as the
 * single argument. For presenters which use {@link ProxyStandard @ProxyStandard} this is {@link jakarta.inject.Provider
 * Provider&lt;T&gt;}; for presenters which use {@link ProxyCodeSplit @ProxyCodeSplit} or {@link ProxyCodeSplitBundle}
 * this is {@link com.google.gwt.inject.client.AsyncProvider AsyncProvider&lt;T&gt;}
 * <p>
 * Here is an example use of {@code @CustomProvider}:
 * <p>
 * <pre>
 * &#064;ProxyCodeSplit
 * &#064;CustomProvider(SecurityContextProvider.class)
 * interface MyProxy extends ProxyPlace&lt;MyPresenter&gt; {
 * }
 *
 * ...
 *
 * public class SecurityContextProvider implements IndirectProvider&lt;MyPresenter&gt; {
 *     private final AsyncProvider&lt;MyPresenter&gt; provider;
 *
 *     public SecurityContextProvider(AsyncProvider&lt;MyPresenter&gt; provider) {
 *         this.provider = provider;
 *     }
 *
 *     &#064;Override
 *     public void get(AsyncCallback&lt;MyPresenter&gt; callback) {
 *         // Place your custom logic here, e.g. fetch data from the server.
 *         // If everything is ready, call
 *         provider.get(callback);
 *     }
 * }
 * </pre>
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface CustomProvider {
    Class<? extends IndirectProvider<?>> value();
}
