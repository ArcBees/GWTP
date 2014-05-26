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
import com.gwtplatform.carstore.client.application.widget.message.Message;

public class DisplayMessageEvent extends GwtEvent<DisplayMessageEvent.DisplayMessageHandler> {
    public interface DisplayMessageHandler extends EventHandler {
        void onDisplayMessage(DisplayMessageEvent event);
    }

    public static Type<DisplayMessageHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, Message message) {
        source.fireEvent(new DisplayMessageEvent(message));
    }

    private static final Type<DisplayMessageHandler> TYPE = new Type<>();

    private Message message;

    public DisplayMessageEvent(Message message) {
        this.message = message;
    }

    @Override
    public Type<DisplayMessageHandler> getAssociatedType() {
        return TYPE;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    protected void dispatch(DisplayMessageHandler handler) {
        handler.onDisplayMessage(this);
    }
}
