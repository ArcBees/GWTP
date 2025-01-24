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

package com.gwtplatform.dispatch.rpc.client.interceptor;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Provider;
import com.gwtplatform.common.client.CodeSplitBundleProvider;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.common.client.ProviderBundle;

/**
 * The default implementation that {@link RpcInterceptorRegistry} that if bound will not load any client-side
 * interceptors.
 * <p> To register client-side interceptors, extend this class and call {@link #register} in the
 * constructor.
 * </p>
 * <h2><u>Example</u></h2>
 * <p>
 * <pre>
 * <code>
 * public class MyRpcInterceptorRegistry extends DefaultClientInterceptorRegistry {
 *   {@literal}@Inject
 *   public MyRpcInterceptorRegistry(
 *       RetrieveFooClientInterceptor interceptor,
 *       Provider&lt;ListFooClientInterceptor&gt; provider,
 *       AsyncProvider&lt;UpdateFooClientInterceptor&gt; asyncProvider,
 *       AsyncProvider&lt;CreateFooBundle&gt; fooCreateBundle) {
 *
 *     register(interceptor);
 *     register(ListFooClientAction.class, provider);
 *     register(UpdateFooClientAction.class, asyncProvider);
 *     register(CreateFooClientAction.class, fooCreateBundle, CreateFooBundle.ID_CreateFooClientInterceptor);
 * }
 *
 * // Provider Bundle that will try to combine the presenter and client action interceptor into the same split point.
 * public class CreateFooBundle extends ProviderBundle {
 *   public static final int ID_CreateFooPresenter = 0;
 *   public static final int ID_CreateFooClientInterceptor = 1;
 *
 *   {@literal}@Inject
 *   public CreateFooBundle(
 *       Provider&lt;CreateFooPresenterImpl&gt; presenter,
 *       Provider&lt;CreateFooClientInterceptor&gt; clientInterceptor) {
 *     super(2);
 *     providers[ID_CreateFooPresenter] = presenter;
 *     providers[ID_CreateFooClientInterceptor] = clientInterceptor;
 *   }
 * }
 * </code>
 * </pre>
 */
public class DefaultRpcInterceptorRegistry implements RpcInterceptorRegistry {
    private final Map<Class<?>, IndirectProvider<RpcInterceptor<?, ?>>> interceptors =
            new HashMap<Class<?>, IndirectProvider<RpcInterceptor<?, ?>>>();

    @Override
    public <A> IndirectProvider<RpcInterceptor<?, ?>> find(A action) {
        return interceptors.get(action.getClass());
    }

    /**
     * Register a instance of a client-side interceptor.
     *
     * @param handler The {@link RpcInterceptor};
     */
    protected void register(final RpcInterceptor<?, ?> handler) {
        register(handler.getActionType(),
                new IndirectProvider<RpcInterceptor<?, ?>>() {
                    @Override
                    public void get(AsyncCallback<RpcInterceptor<?, ?>> callback) {
                        callback.onSuccess(handler);
                    }
                });
    }

    /**
     * Register a {@link jakarta.inject.Provider} of a client-side interceptor.
     *
     * @param actionType The type of action that the client-side interceptor supports.
     * @param handlerProvider The {@link com.google.inject.Provider} of the handler.
     */
    protected void register(Class<?> actionType,
            final Provider<? extends RpcInterceptor<?, ?>> handlerProvider) {
        register(actionType,
                new IndirectProvider<RpcInterceptor<?, ?>>() {
                    @Override
                    public void get(AsyncCallback<RpcInterceptor<?, ?>> callback) {
                        callback.onSuccess(handlerProvider.get());
                    }
                });
    }

    /**
     * Register an {@link com.google.gwt.inject.client.AsyncProvider} of a client-side interceptor.
     *
     * @param actionType The type of that the client-side interceptor supports.
     * @param handlerProvider The {@link com.google.gwt.inject.client.AsyncProvider} of the handler.
     */
    protected void register(Class<?> actionType,
            final AsyncProvider<? extends RpcInterceptor<?, ?>> handlerProvider) {
        register(actionType,
                new IndirectProvider<RpcInterceptor<?, ?>>() {
                    @Override
                    public void get(AsyncCallback<RpcInterceptor<?, ?>> callback) {
                        handlerProvider.get(callback);
                    }
                });
    }

    /**
     * Register a client-side interceptor that is part of a {@link com.gwtplatform.common.client.ProviderBundle}.
     *
     * @param actionType The type of that the client-side interceptor supports.
     * @param bundleProvider The {@link jakarta.inject.Provider} of the {@link com.gwtplatform.common.client
     * .ProviderBundle}.
     * @param providerId The id of the client-side interceptor provider.
     */
    protected <B extends ProviderBundle> void register(Class<?> actionType,
            AsyncProvider<B> bundleProvider,
            int providerId) {
        register(actionType, new CodeSplitBundleProvider<RpcInterceptor<?, ?>, B>(bundleProvider, providerId));
    }

    /**
     * Register an {@link com.gwtplatform.common.client.IndirectProvider} of a client-side interceptor.
     *
     * @param handlerProvider The {@link com.gwtplatform.common.client.IndirectProvider}.
     */
    protected void register(Class<?> actionType,
            IndirectProvider<RpcInterceptor<?, ?>> handlerProvider) {
        interceptors.put(actionType, handlerProvider);
    }
}
