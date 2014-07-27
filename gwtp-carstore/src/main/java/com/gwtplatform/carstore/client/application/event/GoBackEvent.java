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

public class GoBackEvent extends GwtEvent<GoBackEvent.GoBackHandler> {
    public interface GoBackHandler extends EventHandler {
        void onGoBack(GoBackEvent event);
    }

    public static Type<GoBackHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source) {
        source.fireEvent(new GoBackEvent());
    }

    private static final Type<GoBackHandler> TYPE = new Type<>();

    public GoBackEvent() {
    }

    @Override
    public Type<GoBackHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(GoBackHandler handler) {
        handler.onGoBack(this);
    }
}
