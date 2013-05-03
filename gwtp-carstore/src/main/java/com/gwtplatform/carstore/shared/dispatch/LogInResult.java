package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.ActionType;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.dispatch.shared.Result;

public class LogInResult implements Result {
    ActionType actionType;
    CurrentUserDto currentUserDto;
    String loggedInCookie;

    protected LogInResult() {
        // Needed for serialization
    }

    public LogInResult(ActionType actionType, CurrentUserDto currentUserDto, String loggedInCookie) {
        this.actionType = actionType;
        this.currentUserDto = currentUserDto;
        this.loggedInCookie = loggedInCookie;
    }

    public CurrentUserDto getCurrentUserDto() {
        return currentUserDto;
    }

    public String getLoggedInCookie() {
        return loggedInCookie;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
