package com.arcbees.carsample.server.dispatch;

import com.arcbees.carsample.server.authentication.Authenticator;
import com.arcbees.carsample.shared.dispatch.LogoutAction;
import com.arcbees.carsample.shared.dispatch.LogoutResult;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class LogoutHandler extends AbstractActionHandler<LogoutAction, LogoutResult> {
    private final Authenticator authenticator;

    @Inject
    public LogoutHandler(final Authenticator authenticator) {
        super(LogoutAction.class);

        this.authenticator = authenticator;
    }

    @Override
    public LogoutResult execute(LogoutAction action, ExecutionContext context) throws ActionException {
        authenticator.logout();

        return new LogoutResult();
    }
}
