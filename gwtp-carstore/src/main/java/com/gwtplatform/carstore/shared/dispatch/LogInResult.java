package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.dispatch.shared.Result;

public class LogInResult implements Result {
    private ActionType actiontype;
    private CurrentUserDto currentUserDto;
    private String loggedInCookie;

    protected LogInResult() {
        // Needed for serialization
    }

    public LogInResult(ActionType actiontype, CurrentUserDto currentUserDto, String loggedInCookie) {
        this.actiontype = actiontype;
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
        return actiontype;
    }
}
