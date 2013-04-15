package com.gwtplatform.carstore.server.dispatch;

import com.google.inject.Inject;
import com.gwtplatform.carstore.server.authentication.Authenticator;
import com.gwtplatform.carstore.shared.dispatch.LogoutAction;
import com.gwtplatform.carstore.shared.dispatch.LogoutResult;
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
