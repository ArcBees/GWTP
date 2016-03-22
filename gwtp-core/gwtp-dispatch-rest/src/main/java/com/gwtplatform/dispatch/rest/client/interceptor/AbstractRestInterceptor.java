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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gwtplatform.dispatch.rest.client.context.RestContext;
import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * Simple abstract super-class for {@link RestInterceptor} implementations that forces the action class to be passed in
 * as a constructor to the handler.
 */
public abstract class AbstractRestInterceptor implements RestInterceptor {
    private final List<RestContext> restContexts;

    protected AbstractRestInterceptor(
            RestContext context,
            RestContext... moreContexts) {
        List<RestContext> contexts = new ArrayList<>();
        contexts.add(context);

        if (moreContexts != null) {
            Collections.addAll(contexts, moreContexts);
        }

        restContexts = Collections.unmodifiableList(contexts);
    }

    public List<RestContext> getRestContexts() {
        return restContexts;
    }

    @Override
    public boolean canExecute(RestAction<?> action) {
        RestContext subjectContext = new RestContext.Builder(action).build();

        for (RestContext context : restContexts) {
            // Must have at least one supporting context
            if (context.equals(subjectContext)) {
                return true;
            }
        }
        return false;
    }
}
