/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.filter;

import java.util.Iterator;
import java.util.Map.Entry;

import com.gwtplatform.dispatch.client.ExecuteCommand;
import com.gwtplatform.dispatch.rest.client.RestCallback;
import com.gwtplatform.dispatch.rest.client.context.RestContext;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;

import jakarta.inject.Inject;

public class DefaultRestFilterChain implements RestFilterChain {
    private final Iterator<Entry<RestContext, RestFilter>> filterIterator;

    @Inject
    protected DefaultRestFilterChain(RestFilterRegistry filterRegistry) {
        filterIterator = filterRegistry.iterator();
    }

    @Override
    public <R> DispatchRequest doFilter(
            RestAction<R> action,
            RestCallback<R> resultCallback,
            ExecuteCommand<RestAction<R>, RestCallback<R>> executeCommand) {

        if (filterIterator.hasNext()) {
            Entry<RestContext, RestFilter> filterEntry = filterIterator.next();
            RestContext subjectContext = new RestContext.Builder(action).build();

            if (filterEntry.getKey().equals(subjectContext)) {
                return filterEntry.getValue().filter(action, resultCallback, executeCommand, this);
            } else {
                return doFilter(action, resultCallback, executeCommand);
            }
        } else {
            return executeCommand.execute(action, resultCallback);
        }
    }
}
