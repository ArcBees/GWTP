package com.gwtplatform.dispatch.server;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.NoResult;

public class SomeAction implements Action<NoResult> {
    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
