package com.gwtplatform.carstore.client.dispatch;

import com.gwtplatform.dispatch.client.DispatchHooks;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RpcDispatchHooks implements com.gwtplatform.dispatch.rpc.client.RpcDispatchHooks {
    private static final Logger logger = Logger.getLogger(DispatchHooks.class.getName());

    @Override
    public void onExecute(Action action) {
        logger.log(Level.INFO, "Executing rest dispatch " + action.getServiceName() + " resource action");
    }

    @Override
    public void onSuccess(Action action, Result result) {
        logger.log(Level.INFO, "Successfully executed " + action.getServiceName());
    }

    @Override
    public void onFailure(Action action, Throwable caught) {
        logger.log(Level.INFO, "Failed to executed " + action.getServiceName() + " " + caught.getMessage());
    }
}
