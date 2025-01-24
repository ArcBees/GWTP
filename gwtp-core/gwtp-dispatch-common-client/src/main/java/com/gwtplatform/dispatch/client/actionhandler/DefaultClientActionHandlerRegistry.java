/*
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

package com.gwtplatform.dispatch.client.actionhandler;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Provider;
import com.gwtplatform.common.client.CodeSplitBundleProvider;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.common.client.ProviderBundle;

/**
 * The default implementation that {@link ClientActionHandlerRegistry} that if bound will not load any client-side
 * action handlers.
 * <p>
 * To register client-side action handlers, extend this class and call {@link #register} in the constructor.
 * </p>
 * <h2><u>Example</u></h2>
 * <p>
 * <pre>
 * <code>
 * public class MyActionHandlerRegistry extends DefaultClientActionHandlerRegistry {
 *   {@literal}@Inject
 *   public DefaultClientRestActionHandlerRegistry(
 *       RetrieveFooClientActionHandler handler,
 *       Provider&lt;ListFooClientActionHandler&gt; provider,
 *       AsyncProvider&lt;UpdateFooClientActionHandler&gt; asyncProvider,
 *       AsyncProvider&lt;CreateFooBundle&gt; fooCreateBundle) {
 *
 *     register(handler);
 *     register(ListFooClientAction.class, provider);
 *     register(UpdateFooClientAction.class, asyncProvider);
 *     register(CreateFooClientAction.class, fooCreateBundle, CreateFooBundle.ID_CreateFooClientActionHandler);
 * }
 *
 * // Provider Bundle that will try to combine the presenter and client action handler into the same split point.
 * public class CreateFooBundle extends ProviderBundle {
 *   public static final int ID_CreateFooPresenter = 0;
 *   public static final int ID_CreateFooClientActionHandler = 1;
 *
 *   {@literal}@Inject
 *   public CreateFooBundle(
 *       Provider&lt;CreateFooPresenterImpl&gt; presenter,
 *       Provider&lt;CreateFooClientActionHandler&gt; clientActionHandler) {
 *     super(2);
 *     providers[ID_CreateFooPresenter] = presenter;
 *     providers[ID_CreateFooClientActionHandler] = clientActionHandler;
 *   }
 * }
 * </code>
 * </pre>
 *
 * @deprecated Since 1.4. Use com.gwtplatform.dispatch.rpc.client.interceptor.DefaultRpcInterceptorRegistry
 */
@Deprecated
public class DefaultClientActionHandlerRegistry implements ClientActionHandlerRegistry {
    private Map<Class<?>, IndirectProvider<ClientActionHandler<?, ?>>> clientActionHandlers;

    /**
     * Register a instance of a client-side action handler.
     *
     * @param handler The {@link ClientActionHandler};
     */
    protected void register(final ClientActionHandler<?, ?> handler) {
        register(handler.getActionType(),
                new IndirectProvider<ClientActionHandler<?, ?>>() {
                    @Override
                    public void get(AsyncCallback<ClientActionHandler<?, ?>> callback) {
                        callback.onSuccess(handler);
                    }
                });
    }

    /**
     * Register a {@link jakarta.inject.Provider} of a client-side action handler.
     *
     * @param actionType      The type of action that the client-side action handler supports.
     * @param handlerProvider The {@link com.google.inject.Provider} of the handler.
     */
    protected void register(Class<?> actionType,
                            final Provider<? extends ClientActionHandler<?, ?>> handlerProvider) {
        register(actionType,
                new IndirectProvider<ClientActionHandler<?, ?>>() {
                    @Override
                    public void get(AsyncCallback<ClientActionHandler<?, ?>> callback) {
                        callback.onSuccess(handlerProvider.get());
                    }
                });
    }

    /**
     * Register an {@link com.google.gwt.inject.client.AsyncProvider} of a client-side action handler.
     *
     * @param actionType      The type of that the client-side action handler supports.
     * @param handlerProvider The {@link com.google.gwt.inject.client.AsyncProvider} of the handler.
     */
    protected void register(Class<?> actionType,
                            final AsyncProvider<? extends ClientActionHandler<?, ?>> handlerProvider) {
        register(actionType,
                new IndirectProvider<ClientActionHandler<?, ?>>() {
                    @Override
                    public void get(AsyncCallback<ClientActionHandler<?, ?>> callback) {
                        handlerProvider.get(callback);
                    }
                });
    }

    /**
     * Register a client-side action handler that is part of a {@link com.gwtplatform.common.client.ProviderBundle}.
     *
     * @param actionType     The type of that the client-side action handler supports.
     * @param bundleProvider The {@link jakarta.inject.Provider} of the
     *                       {@link com.gwtplatform.common.client.ProviderBundle}.
     * @param providerId     The id of the client-side action handler provider.
     */
    protected <B extends ProviderBundle> void register(Class<?> actionType,
                                                       AsyncProvider<B> bundleProvider,
                                                       int providerId) {
        register(actionType, new CodeSplitBundleProvider<ClientActionHandler<?, ?>, B>(bundleProvider, providerId));
    }

    /**
     * Register an {@link com.gwtplatform.common.client.IndirectProvider} of a client-side action handler.
     *
     * @param handlerProvider The {@link com.gwtplatform.common.client.IndirectProvider}.
     */
    protected void register(Class<?> actionType,
                            IndirectProvider<ClientActionHandler<?, ?>> handlerProvider) {
        if (clientActionHandlers == null) {
            clientActionHandlers = new HashMap<Class<?>, IndirectProvider<ClientActionHandler<?, ?>>>();
        }

        clientActionHandlers.put(actionType, handlerProvider);
    }

    @Override
    public <A> IndirectProvider<ClientActionHandler<?, ?>> find(Class<A> actionClass) {
        if (clientActionHandlers == null) {
            return null;
        } else {
            return clientActionHandlers.get(actionClass);
        }
    }
}
