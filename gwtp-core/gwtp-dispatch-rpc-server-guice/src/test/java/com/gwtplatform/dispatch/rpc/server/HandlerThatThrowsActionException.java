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

package com.gwtplatform.dispatch.rpc.server;

import com.gwtplatform.dispatch.rpc.server.actionhandler.AbstractActionHandler;
import com.gwtplatform.dispatch.rpc.shared.NoResult;
import com.gwtplatform.dispatch.shared.ActionException;

import jakarta.inject.Inject;

public class HandlerThatThrowsActionException extends AbstractActionHandler<SomeAction, NoResult> {
    @Inject
    HandlerThatThrowsActionException() {
        super(SomeAction.class);
    }

    @Override
    public NoResult execute(SomeAction action, ExecutionContext context) throws ActionException {
        throw new ActionExceptionThrownByHandler(new Exception());
    }

    @Override
    public void undo(SomeAction action, NoResult result, ExecutionContext context) throws ActionException {
    }
}
