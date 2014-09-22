package com.gwtplatform.carstore.client.dispatch;

import com.gwtplatform.dispatch.client.DispatchHooks;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RpcDispatchHooks implements com.gwtplatform.dispatch.rpc.client.RpcDispatchHooks {
    private static final Logger logger = Logger.getLogger(DispatchHooks.class.getName());

    @Override
    public void onExecute(Action action, boolean undo) {
        logger.log(Level.INFO, "Executing rpc dispatch " + action.getServiceName()
            + " resource action (undo: " + String.valueOf(undo) + ")");
    }

    @Override
    public void onSuccess(Action action, Result result, boolean undo) {
        logger.log(Level.INFO, "Successfully executed " + action.getServiceName()
            + (" undo: " + String.valueOf(undo) + ")"));
    }

    @Override
    public void onFailure(Action action, Throwable caught, boolean undo) {
        logger.log(Level.INFO, "Failed to executed " + action.getServiceName() + " "
            + caught.getMessage() + (" undo: " + String.valueOf(undo) + ")"));
    }
}
