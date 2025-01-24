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

package com.gwtplatform.dispatch.client.actionhandler.caching;

/**
 * Default implementation of {@link AbstractCachingClientActionHandler}. It supports action caching
 *
 * @deprecated Since 1.4. Use com.gwtplatform.dispatch.rpc.client.interceptor.caching.CachingInterceptor
 *
 * @param <A> The type of the action.
 * @param <R> The type of the result.
 */
@Deprecated
public class ActionCachingHandler<A, R> extends AbstractCachingClientActionHandler<A, R> {
    // TODO Add support for timeout based auto-expiry of cached results?

    public ActionCachingHandler(Class<A> actionType, Cache cache) {
        super(actionType, cache);
    }

    @Override
    protected void postfetch(A action, R result) {
        if (result == null) {
            getCache().remove(action);
        } else {
            getCache().put(action, result);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected R prefetch(A action) {
        try {
            Object value = getCache().get(action);
            if (value != null) {
                return (R) value;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
