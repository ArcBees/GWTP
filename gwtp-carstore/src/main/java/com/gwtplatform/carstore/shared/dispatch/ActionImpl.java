package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

public abstract class ActionImpl<T extends Result> implements Action<T> {
    @Override
    public String getServiceName() {
        return Action.DEFAULT_SERVICE_NAME + getClass().getName();
    }
}
