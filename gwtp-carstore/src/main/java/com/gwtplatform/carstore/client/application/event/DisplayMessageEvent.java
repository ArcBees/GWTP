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

    private static final Type<DisplayMessageHandler> TYPE = new Type<DisplayMessageHandler>();

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
