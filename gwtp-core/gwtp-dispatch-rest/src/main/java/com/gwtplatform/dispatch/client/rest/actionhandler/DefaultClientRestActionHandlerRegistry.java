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

package com.gwtplatform.dispatch.client.rest.actionhandler;

import java.util.Map;

import javax.inject.Provider;

import com.google.common.collect.Maps;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.common.client.CodeSplitBundleProvider;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.common.client.ProviderBundle;
import com.gwtplatform.dispatch.shared.rest.RestAction;

/**
 * The default implementation that {@link ClientRestActionHandlerRegistry} that if
 * bound will not load any client-side action handlers. </p>
 * To register client-side action handlers, extend this class and call {@link #register}
 * in the constructor.
 * <p/>
 * <h3><u>Example</u></h3>
 * <p/>
 * <pre>
 * <code>
 * public class MyActionHandlerRegistry extends DefaultClientRestActionHandlerRegistry {
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
 */
public class DefaultClientRestActionHandlerRegistry implements ClientRestActionHandlerRegistry {
    private Map<Class<? extends RestAction<?>>, IndirectProvider<ClientRestActionHandler<?, ?>>> clientActionHandlers;

    /**
     * Register a instance of a client-side action handler.
     *
     * @param handler The {@link ClientRestActionHandler};
     */
    protected void register(final ClientRestActionHandler<?, ?> handler) {
        register(handler.getActionType(),
                new IndirectProvider<ClientRestActionHandler<?, ?>>() {
                    @Override
                    public void get(AsyncCallback<ClientRestActionHandler<?, ?>> callback) {
                        callback.onSuccess(handler);
                    }
                });
    }

    /**
     * Register a {@link javax.inject.Provider} of a client-side action handler.
     *
     * @param actionType      The type of {@link com.gwtplatform.dispatch.shared.Action} that the
     *                        client-side action handler supports.
     * @param handlerProvider The {@link com.google.inject.Provider} of the handler.
     */
    protected void register(Class<? extends RestAction<?>> actionType,
                            final Provider<? extends ClientRestActionHandler<?, ?>> handlerProvider) {
        register(actionType,
                new IndirectProvider<ClientRestActionHandler<?, ?>>() {
                    @Override
                    public void get(AsyncCallback<ClientRestActionHandler<?, ?>> callback) {
                        callback.onSuccess(handlerProvider.get());
                    }
                });
    }

    /**
     * Register an {@link com.google.gwt.inject.client.AsyncProvider} of a client-side action handler.
     *
     * @param actionType      The type of {@link com.gwtplatform.dispatch.shared.Action} that the
     *                        client-side action handler supports.
     * @param handlerProvider The {@link com.google.gwt.inject.client.AsyncProvider} of the handler.
     */
    protected void register(Class<? extends RestAction<?>> actionType,
                            final AsyncProvider<? extends ClientRestActionHandler<?, ?>> handlerProvider) {
        register(actionType,
                new IndirectProvider<ClientRestActionHandler<?, ?>>() {
                    @Override
                    public void get(AsyncCallback<ClientRestActionHandler<?, ?>> callback) {
                        handlerProvider.get(callback);
                    }
                });
    }

    /**
     * Register a client-side action handler that is part of a {@link com.gwtplatform.common.client.ProviderBundle}.
     *
     * @param actionType     The type of {@link RestAction} that the client-side action handler supports.
     * @param bundleProvider The {@link Provider} of the {@link com.gwtplatform.common.client.ProviderBundle}.
     * @param providerId     The id of the client-side action handler provider.
     */
    protected <B extends ProviderBundle> void register(Class<? extends RestAction<?>> actionType,
                                                       AsyncProvider<B> bundleProvider,
                                                       int providerId) {
        register(actionType, new CodeSplitBundleProvider<ClientRestActionHandler<?, ?>, B>(bundleProvider, providerId));
    }

    /**
     * Register an {@link com.gwtplatform.common.client.IndirectProvider} of a client-side action handler.
     *
     * @param handlerProvider The {@link com.gwtplatform.common.client.IndirectProvider}.
     */
    protected void register(Class<? extends RestAction<?>> actionType,
                            IndirectProvider<ClientRestActionHandler<?, ?>> handlerProvider) {
        if (clientActionHandlers == null) {
            clientActionHandlers = Maps.newHashMap();
        }

        clientActionHandlers.put(actionType, handlerProvider);
    }

    @Override
    public <A extends RestAction<?>> IndirectProvider<ClientRestActionHandler<?, ?>> find(Class<A> actionClass) {
        if (clientActionHandlers == null) {
            return null;
        } else {
            return clientActionHandlers.get(actionClass);
        }
    }
}
