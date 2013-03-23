package com.arcbees.carsample.client.application.event;

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

    private static final Type<ActionBarVisibilityHandler> TYPE = new Type<ActionBarVisibilityHandler>();

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
