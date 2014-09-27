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

package com.gwtplatform.dispatch.rest.client.actionhandler;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Provider;
import com.gwtplatform.common.client.CodeSplitBundleProvider;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.common.client.ProviderBundle;
import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * The rest implementation of {@link com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry}
 * that if bound will not load any client-side action handlers.
 * </p>
 * To register client-side action handlers, extend this class and call {@link #register} in the constructor.
 * <p/>
 * <h3><u>Example</u></h3>
 * <p/>
 * <pre>
 * <code>
 * public class MyRestActionHandlerRegistry extends RestActionHandlerRegistry {
 *   {@literal}@Inject
 *   public MyActionHandlerRegistry(
 *       RetrieveFooRestActionHandler handler,
 *       Provider&lt;ListItemsRestActionHandler&gt; provider,
 *       AsyncProvider&lt;UpdateItemRestActionHandler&gt; asyncProvider,
 *       AsyncProvider&lt;CreateItemBundle&gt; fooCreateBundle) {
 *
 *     register(handler);
 *     register(new RestHandlerIndex("/items", HttpMethod.GET), provider);
 *     register(new RestHandlerIndex("/items", HttpMethod.PUT), asyncProvider);
 *     register(new RestHandlerIndex("/items", HttpMethod.POST), itemsBundle,
 *       ItemsBundle.ID_CreateItemRestActionHandler);
 * }
 *
 * // Provider Bundle that will try to combine the presenter and client action handler into the same split point.
 * public class ItemsBundle extends ProviderBundle {
 *   public static final int ID_CreateItemPresenter = 0;
 *   public static final int ID_CreateItemRestActionHandler = 1;
 *
 *   {@literal}@Inject
 *   public ItemsBundle(
 *       Provider&lt;CreateItemPresenterImpl&gt; presenter,
 *       Provider&lt;CreateItemRestActionHandler&gt; restActionHandler) {
 *     super(2);
 *     providers[ID_CreateItemPresenter] = presenter;
 *     providers[ID_CreateItemRestActionHandler] = restActionHandler;
 *   }
 * }
 * </code>
 * </pre>
 */
public class DefaultRestActionHandlerRegistry implements RestActionHandlerRegistry {
    private Map<RestHandlerIndex, IndirectProvider<RestActionHandler>> restActionHandlers;

    /**
     * Register a instance of a client-side action handler.
     *
     * @param handler The {@link RestActionHandler};
     */
    protected void register(final RestActionHandler handler) {
        register(handler.getIndex(),
                new IndirectProvider<RestActionHandler>() {
                    @Override
                    public void get(AsyncCallback<RestActionHandler> callback) {
                        callback.onSuccess(handler);
                    }
                });
    }

    /**
     * Register a {@link javax.inject.Provider} of a client-side action handler.
     *
     * @param index          The {@link RestHandlerIndex} for the rest action handler.
     * @param handlerProvider The {@link com.google.inject.Provider} of the handler.
     */
    protected void register(RestHandlerIndex index,
                            final Provider<RestActionHandler> handlerProvider) {
        register(index,
                new IndirectProvider<RestActionHandler>() {
                    @Override
                    public void get(AsyncCallback<RestActionHandler> callback) {
                        callback.onSuccess(handlerProvider.get());
                    }
                });
    }

    /**
     * Register an {@link com.google.gwt.inject.client.AsyncProvider} of a client-side action handler.
     *
     * @param index           The {@link RestHandlerIndex} for the rest action handler.
     * @param handlerProvider The {@link com.google.gwt.inject.client.AsyncProvider} of the handler.
     */
    protected void register(RestHandlerIndex index,
                            final AsyncProvider<RestActionHandler> handlerProvider) {
        register(index,
                new IndirectProvider<RestActionHandler>() {
                    @Override
                    public void get(AsyncCallback<RestActionHandler> callback) {
                        handlerProvider.get(callback);
                    }
                });
    }

    /**
     * Register a client-side action handler that is part of a {@link com.gwtplatform.common.client.ProviderBundle}.
     *
     * @param index          The {@link RestHandlerIndex} for the rest action handler.
     * @param bundleProvider The {@link javax.inject.Provider} of the
     *                       {@link com.gwtplatform.common.client.ProviderBundle}.
     * @param providerId     The id of the client-side action handler provider.
     */
    protected <B extends ProviderBundle> void register(RestHandlerIndex index,
                                                       AsyncProvider<B> bundleProvider,
                                                       int providerId) {
        register(index, new CodeSplitBundleProvider<RestActionHandler, B>(bundleProvider, providerId));
    }

    /**
     * Register an {@link com.gwtplatform.common.client.IndirectProvider} of a client-side action handler.
     *
     * @param handlerProvider The {@link com.gwtplatform.common.client.IndirectProvider}.
     */
    protected void register(RestHandlerIndex index,
                            IndirectProvider<RestActionHandler> handlerProvider) {
        if (restActionHandlers == null) {
            restActionHandlers = Maps.newHashMap();
        }

        restActionHandlers.put(index, handlerProvider);
    }

    @Override
    public <A> IndirectProvider<RestActionHandler> find(A action) {
        if (restActionHandlers != null && action instanceof RestAction) {
            RestAction restAction = (RestAction) action;
            return restActionHandlers.get(new RestHandlerIndex(restAction.getPath(),
                restAction.getHttpMethod(), restAction.getQueryParams().size()));
        } else {
            return null;
        }
    }
}
