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

package com.gwtplatform.dispatch.rest.client.interceptor;

import com.gwtplatform.dispatch.client.interceptor.AbstractInterceptor;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 * Simple abstract super-class for {@link RestInterceptor}
 * implementations that forces the action class to be passed in as a constructor to the handler.
 */
public abstract class AbstractRestInterceptor extends AbstractInterceptor<RestAction, Object> implements
        RestInterceptor {
    private final InterceptorContext[] interceptorContexts;

    protected AbstractRestInterceptor(InterceptorContext... interceptorContexts) {
        super(RestAction.class); // catch all

        this.interceptorContexts = interceptorContexts;
    }

    @Override
    public InterceptorContext[] getInterceptorContexts() {
        return interceptorContexts;
    }

    @Override
    public boolean canExecute(TypedAction action) {
        if (action instanceof RestAction) {
            RestAction restAction = (RestAction) action;
            InterceptorContext subjectContext = InterceptorContext.newContext(restAction);

            for (InterceptorContext context : interceptorContexts) {
                // Must have at least one supporting context
                if (context.equals(subjectContext)) {
                    return true;
                }
            }
        }
        return false;
    }
}
