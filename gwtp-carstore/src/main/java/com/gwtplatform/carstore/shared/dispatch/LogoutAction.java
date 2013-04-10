package com.gwtplatform.carstore.shared.dispatch;

public class LogoutAction extends ActionImpl<LogoutResult> {
    public LogoutAction() {
        // Needed for serialization
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
