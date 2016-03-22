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

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.inject.Provider;
import com.gwtplatform.common.client.CodeSplitBundleProvider;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.common.client.ProviderBundle;
import com.gwtplatform.dispatch.rest.client.context.RestContext;
import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * The rest implementation of {@link com.gwtplatform.dispatch.rest.client.interceptor.RestInterceptorRegistry}
 * that if bound will not load any client-side interceptors.
 * </p>
 * To register client-side interceptors, extend this class and call {@link #register} in the constructor.
 * <h3><u>Example</u></h3>
 * <pre>
 * <code>
 * public class MyRestInterceptorRegistry extends RestInterceptorRegistry {
 *   {@literal}@Inject
 *   public MyRestInterceptorRegistry(
 *       RetrieveFooRestInterceptor interceptor,
 *       Provider&lt;ListItemsRestInterceptor&gt; provider,
 *       AsyncProvider&lt;UpdateItemRestInterceptor&gt; asyncProvider,
 *       AsyncProvider&lt;CreateItemBundle&gt; fooCreateBundle) {
 *
 *     register(interceptor);
 *     register(new InterceptorContext("/items", HttpMethod.GET, -1), provider);
 *     register(new InterceptorContext("/items", HttpMethod.PUT, -1), asyncProvider);
 *     register(new InterceptorContext("/items", HttpMethod.POST, -1), itemsBundle,
 *       ItemsBundle.ID_CreateItemRestInterceptor);
 * }
 *
 * // Provider Bundle that will try to combine the presenter and client interceptor into the same split point.
 * public class ItemsBundle extends ProviderBundle {
 *   public static final int ID_CreateItemPresenter = 0;
 *   public static final int ID_CreateItemRestInterceptor = 1;
 *
 *   {@literal}@Inject
 *   public ItemsBundle(
 *       Provider&lt;CreateItemPresenterImpl&gt; presenter,
 *       Provider&lt;CreateItemRestInterceptor&gt; restInterceptor) {
 *     super(2);
 *     providers[ID_CreateItemPresenter] = presenter;
 *     providers[ID_CreateItemRestInterceptor] = restInterceptor;
 *   }
 * }
 * </code>
 * </pre>
 */
public class DefaultRestInterceptorRegistry implements RestInterceptorRegistry {
    private final Map<RestContext, IndirectProvider<RestInterceptor>> interceptors =
            new LinkedHashMap<>();

    @Override
    public <A> IndirectProvider<RestInterceptor> find(A action) {
        if (action instanceof RestAction) {
            RestContext subjectContext = new RestContext
                    .Builder((RestAction) action).build();

            for (Map.Entry<RestContext, IndirectProvider<RestInterceptor>> entry : interceptors.entrySet()) {
                RestContext context = entry.getKey();

                if (context.equals(subjectContext)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Register a instance of a client-side interceptor.
     *
     * @param interceptor The {@link RestInterceptor};
     */
    protected void register(final RestInterceptor interceptor) {
        for (RestContext context : interceptor.getRestContexts()) {
            register(context,
                    (IndirectProvider<RestInterceptor>) callback -> callback.onSuccess(interceptor));
        }
    }

    /**
     * Register a {@link javax.inject.Provider} of a client-side interceptor.
     *
     * @param context         The {@link RestContext} for the rest interceptor.
     * @param handlerProvider The {@link com.google.inject.Provider} of the handler.
     */
    protected void register(RestContext context,
                            final Provider<RestInterceptor> handlerProvider) {
        register(context,
                (IndirectProvider<RestInterceptor>) callback -> callback.onSuccess(handlerProvider.get()));
    }

    /**
     * Register an {@link com.google.gwt.inject.client.AsyncProvider} of a client-side interceptor.
     *
     * @param context         The {@link RestContext} for the rest interceptor.
     * @param handlerProvider The {@link com.google.gwt.inject.client.AsyncProvider} of the handler.
     */
    protected void register(RestContext context,
                            final AsyncProvider<RestInterceptor> handlerProvider) {
        register(context,
                (IndirectProvider<RestInterceptor>) handlerProvider::get);
    }

    /**
     * Register a client-side interceptor that is part of a {@link com.gwtplatform.common.client.ProviderBundle}.
     *
     * @param context        The {@link RestContext} for the rest interceptor.
     * @param bundleProvider The {@link javax.inject.Provider} of the
     *                       {@link com.gwtplatform.common.client.ProviderBundle}.
     * @param providerId     The id of the client-side interceptor provider.
     */
    protected <B extends ProviderBundle> void register(RestContext context,
                                                       AsyncProvider<B> bundleProvider,
                                                       int providerId) {
        register(context, new CodeSplitBundleProvider<>(bundleProvider, providerId));
    }

    /**
     * Register an {@link com.gwtplatform.common.client.IndirectProvider} of a client-side interceptor.
     *
     * @param context         The {@link RestContext} for the rest interceptor.
     * @param handlerProvider The {@link com.gwtplatform.common.client.IndirectProvider}.
     */
    protected void register(RestContext context,
                            IndirectProvider<RestInterceptor> handlerProvider) {
        if (containsContext(context)) {
            throw new DuplicateInterceptorContextException(context.getPath(), context.getHttpMethod(),
                    context.getQueryCount());
        }
        interceptors.put(context, handlerProvider);
    }

    protected boolean containsContext(RestContext context) {
        // This is currently finding a context based on whether or not it can intercept
        // an action with the same properties as the subject context provided. How a context is
        // indexed may need to change in the near future.
        for (Map.Entry<RestContext, IndirectProvider<RestInterceptor>> entry : interceptors.entrySet()) {
            if (entry.getKey().equals(context)) {
                return true;
            }
        }
        return false;
    }

    protected int getRegistryCount() {
        return interceptors.size();
    }
}
