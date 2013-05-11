/*
 * Copyright 2013 ArcBees Inc.
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

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

public class ChangeActionBarEvent extends GwtEvent<ChangeActionBarEvent.ChangeActionBarHandler> {
    public enum ActionType {
        DONE, UPDATE, DELETE, ADD
    }

    public interface ChangeActionBarHandler extends EventHandler {
        void onChangeActionBar(ChangeActionBarEvent event);
    }

    public static Type<ChangeActionBarHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, List<ActionType> actions, Boolean tabsVisible) {
        source.fireEvent(new ChangeActionBarEvent(actions, tabsVisible));
    }

    private static final Type<ChangeActionBarHandler> TYPE = new Type<ChangeActionBarHandler>();

    private List<ActionType> actions;
    private Boolean tabsVisible;

    public ChangeActionBarEvent(List<ActionType> actions, Boolean tabsVisible) {
        this.actions = actions;
        this.tabsVisible = tabsVisible;
    }

    @Override
    public Type<ChangeActionBarHandler> getAssociatedType() {
        return TYPE;
    }

    public List<ActionType> getActions() {
        return actions;
    }

    public Boolean getTabsVisible() {
        return tabsVisible;
    }

    @Override
    protected void dispatch(ChangeActionBarHandler handler) {
        handler.onChangeActionBar(this);
    }
}
