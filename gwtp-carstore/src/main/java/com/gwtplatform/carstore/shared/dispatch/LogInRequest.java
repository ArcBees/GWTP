package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.ActionType;

public class LogInRequest {
    ActionType actionType;
    String username;
    String password;
    String loggedInCookie;

    protected LogInRequest() {
    }

    public LogInRequest(String username, String password) {
        actionType = ActionType.VIA_CREDENTIALS;
        this.password = password;
        this.username = username;
    }

    public LogInRequest(String loggedInCookie) {
        actionType = ActionType.VIA_COOKIE;
        this.loggedInCookie = loggedInCookie;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getLoggedInCookie() {
        return loggedInCookie;
    }
}
