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

    private static final Type<GoBackHandler> TYPE = new Type<GoBackHandler>();

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
