package com.arcbees.carsample.server.authentication;

import javax.inject.Inject;

import com.gwtplatform.dispatch.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.Result;

public class LoggedInActionValidator implements ActionValidator {
    private final Authenticator authenticator;

    @Inject
    public LoggedInActionValidator(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public boolean isValid(Action<? extends Result> action) throws ActionException {
        return authenticator.isUserLoggedIn();
    }
}
