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

package com.gwtplatform.carstore.client.application.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;

public class ActionBarEvent extends GwtEvent<ActionBarEvent.ActionBarHandler> {
    public interface ActionBarHandler extends EventHandler {
        void onActionEvent(ActionBarEvent event);
    }

    public static Type<ActionBarHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, ActionType actionType, String sourceToken) {
        source.fireEvent(new ActionBarEvent(actionType, sourceToken));
    }

    private static final Type<ActionBarHandler> TYPE = new Type<>();

    private final ActionType actionType;
    private final String sourceToken;

    public ActionBarEvent(final ActionType actionType, final String sourceToken) {
        this.actionType = actionType;
        this.sourceToken = sourceToken;
    }

    public Boolean isTheSameToken(String token) {
        return sourceToken.equals(token);
    }

    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public Type<ActionBarHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ActionBarHandler handler) {
        handler.onActionEvent(this);
    }
}
