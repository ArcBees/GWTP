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

public class NavigationTabEvent extends GwtEvent<NavigationTabEvent.NavigationTabHandler> {
    public interface NavigationTabHandler extends EventHandler {
        public void onCloseTab(NavigationTab element);

        public void onRevealTab(NavigationTab element);
    }

    private static enum Action {
        REVEAL, CLOSE
    }

    private static Type<NavigationTabHandler> type;

    public static void fireClose(HasHandlers source, NavigationTab element) {
        if (type != null) {
            source.fireEvent(new NavigationTabEvent(Action.CLOSE, element));
        }
    }

    public static void fireReveal(HasHandlers source, NavigationTab element) {
        if (type != null) {
            source.fireEvent(new NavigationTabEvent(Action.REVEAL, element));
        }
    }

    public static Type<NavigationTabHandler> getType() {
        if (type == null) {
            type = new Type<NavigationTabHandler>();
        }
        return type;
    }

    private NavigationTab element;
    private Action action;

    public NavigationTabEvent(Action action, NavigationTab element) {
        this.element = element;
        this.action = action;
    }

    @Override
    protected void dispatch(NavigationTabHandler handler) {
        switch (action) {
            case REVEAL:
                handler.onRevealTab(element);
                break;
            case CLOSE:
                handler.onCloseTab(element);
                break;
        }
    }

    @Override
    public Type<NavigationTabHandler> getAssociatedType() {
        return getType();
    }
}
