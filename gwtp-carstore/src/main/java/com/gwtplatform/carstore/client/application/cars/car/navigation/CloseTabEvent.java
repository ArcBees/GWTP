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

package com.gwtplatform.carstore.client.application.cars.car.navigation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

public class CloseTabEvent extends GwtEvent<CloseTabEvent.CloseTabHandler> {
    public interface CloseTabHandler extends EventHandler {
        void onCloseTab(CloseTabEvent event);
    }

    public interface HasCloseTabHandlers extends HasHandlers {
        com.google.web.bindery.event.shared.HandlerRegistration addCloseTabHandler(CloseTabHandler handler);
    }

    private static Type<CloseTabHandler> TYPE = new Type<>();

    public static void fire(HasHandlers source) {
        if (TYPE != null) {
            source.fireEvent(new CloseTabEvent());
        }
    }

    public static Type<CloseTabHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<CloseTabHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CloseTabHandler handler) {
        handler.onCloseTab(this);
    }
}
