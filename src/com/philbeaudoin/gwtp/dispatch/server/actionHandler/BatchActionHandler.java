/**
 * Copyright 2010 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.dispatch.server.actionHandler;

import java.util.List;

import com.philbeaudoin.gwtp.dispatch.server.ExecutionContext;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.ActionException;
import com.philbeaudoin.gwtp.dispatch.shared.BatchAction;
import com.philbeaudoin.gwtp.dispatch.shared.BatchResult;
import com.philbeaudoin.gwtp.dispatch.shared.Result;
import com.philbeaudoin.gwtp.dispatch.shared.BatchAction.OnException;

/**
 * This handles {@link BatchAction} requests, which are a set of multiple
 * actions that need to all be executed successfully in sequence for the whole
 * action to succeed.
 * 
 * @author David Peterson
 */
public class BatchActionHandler extends AbstractActionHandler<BatchAction, BatchResult> {

    public BatchActionHandler() {
        super(BatchAction.class);
    }

    public BatchResult execute(BatchAction action, ExecutionContext context) throws ActionException {
        OnException onException = action.getOnException();
        List<Result> results = new java.util.ArrayList<Result>();
        for (Action<?> a : action.getActions()) {
            Result result = null;
            try {
                result = context.execute(a);
            } catch (Exception e) {
                if (onException == OnException.ROLLBACK) {
                    if (e instanceof ActionException)
                        throw (ActionException) e;
                    if (e instanceof RuntimeException)
                        throw (RuntimeException) e;
                    else
                        throw new ActionException(e);
                }
            }
            results.add(result);
        }

        return new BatchResult(results);
    }

    public void undo(BatchAction action, BatchResult result, ExecutionContext context) throws ActionException {
    // No action necessary - the sub actions should automatically rollback
    }

}
