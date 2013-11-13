package com.gwtplatform.dispatch.server;

import com.gwtplatform.dispatch.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.Result;

public class ActionValidatorThatThrows implements ActionValidator {
    @Override
    public boolean isValid(Action<? extends Result> action) throws ActionException {
        throw new ActionExceptionThrownByValidator();
    }
}
