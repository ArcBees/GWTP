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

public class ActionBarVisibilityEvent extends GwtEvent<ActionBarVisibilityEvent.ActionBarVisibilityHandler> {
    public interface ActionBarVisibilityHandler extends EventHandler {
        void onActionBarVisible(ActionBarVisibilityEvent event);
    }

    public static Type<ActionBarVisibilityHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, Boolean visible) {
        source.fireEvent(new ActionBarVisibilityEvent(visible));
    }

    private static final Type<ActionBarVisibilityHandler> TYPE = new Type<>();

    private final Boolean visible;

    public ActionBarVisibilityEvent(final Boolean visible) {
        this.visible = visible;
    }


    public Boolean isVisible() {
        return visible;
    }

    @Override
    public Type<ActionBarVisibilityHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ActionBarVisibilityHandler handler) {
        handler.onActionBarVisible(this);
    }
}
