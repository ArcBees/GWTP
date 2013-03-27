package com.arcbees.carsample.client.application.cars.car.navigation;

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

    private static Type<CloseTabHandler> TYPE = new Type<CloseTabHandler>();

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
