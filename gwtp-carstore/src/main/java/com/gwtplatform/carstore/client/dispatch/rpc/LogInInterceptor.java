/**
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

package com.gwtplatform.carstore.client.dispatch.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.carstore.shared.dispatch.LogInAction;
import com.gwtplatform.carstore.shared.dispatch.LogInResult;
import com.gwtplatform.dispatch.client.interceptor.ExecuteCommand;
import com.gwtplatform.dispatch.rpc.client.interceptor.AbstractRpcInterceptor;
import com.gwtplatform.dispatch.rpc.client.interceptor.UndoCommand;
import com.gwtplatform.dispatch.shared.DispatchRequest;

public class LogInInterceptor extends AbstractRpcInterceptor<LogInAction, LogInResult> {
    LogInInterceptor() {
        super(LogInAction.class);
    }

    @Override
    public DispatchRequest undo(LogInAction action, LogInResult result, AsyncCallback<Void> callback,
            UndoCommand<LogInAction, LogInResult> undoCommand) {
        return undoCommand.undo(action, result, callback);
    }

    @Override
    public DispatchRequest execute(LogInAction action, AsyncCallback<LogInResult> resultCallback,
            ExecuteCommand<LogInAction, LogInResult> executeCommand) {
        return executeCommand.execute(action, resultCallback);
    }
}
