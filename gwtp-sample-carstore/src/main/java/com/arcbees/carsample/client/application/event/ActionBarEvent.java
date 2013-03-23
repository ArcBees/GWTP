package com.arcbees.carsample.client.application.event;

import com.arcbees.carsample.client.application.event.ChangeActionBarEvent.ActionType;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

public class ActionBarEvent extends GwtEvent<ActionBarEvent.ActionBarHandler> {
    public interface ActionBarHandler extends EventHandler {
        void onActionEvent(ActionBarEvent event);
    }

    public static Type<ActionBarHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, ActionType actionType, String sourceToken) {
        source.fireEvent(new ActionBarEvent(actionType, sourceToken));
    }

    private static final Type<ActionBarHandler> TYPE = new Type<ActionBarHandler>();

    private final ActionType actionType;
    private final String sourceToken;

    public ActionBarEvent(final ActionType actionType, final String sourceToken) {
        this.actionType = actionType;
        this.sourceToken = sourceToken;
    }

    public Boolean isTheSameToken(String token) {
        return sourceToken.equals(token);
    }

    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public Type<ActionBarHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ActionBarHandler handler) {
        handler.onActionEvent(this);
    }
}
