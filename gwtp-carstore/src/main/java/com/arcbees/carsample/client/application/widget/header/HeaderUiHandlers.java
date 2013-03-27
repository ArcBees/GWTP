package com.arcbees.carsample.client.application.widget.header;

import com.arcbees.carsample.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.mvp.client.UiHandlers;

public interface HeaderUiHandlers extends UiHandlers {
    void logout();

    void onAction(ActionType actionType);

    void onGoBack();
}
