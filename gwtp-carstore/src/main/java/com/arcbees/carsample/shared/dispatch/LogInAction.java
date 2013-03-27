    package com.arcbees.carsample.shared.dispatch;

public class LogInAction extends ActionImpl<LogInResult> {
    private ActionType actionType;
    private String username;
    private String password;
    private String loggedInCookie;

    protected LogInAction() { 
    }

    public LogInAction(String username, String password) {
        actionType = ActionType.VIA_CREDENTIALS;
        this.password = password;
        this.username = username;
    }

    public LogInAction(String loggedInCookie) {
        actionType = ActionType.VIA_COOKIE;
        this.loggedInCookie = loggedInCookie;
    }

    @Override
    public boolean isSecured() {
        return false;
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
