package com.gwtplatform.carstore.client.dispatch;

import com.google.gwt.http.client.Response;
import com.gwtplatform.dispatch.client.DispatchHooks;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RestDispatchHooks implements com.gwtplatform.dispatch.rest.client.RestDispatchHooks {
    private static final Logger logger = Logger.getLogger(DispatchHooks.class.getName());

    @Override
    public void onExecute(RestAction action) {
        logger.log(Level.INFO, "Executing rest dispatch " + action.getPath() + " resource action");
    }

    @Override
    public void onSuccess(RestAction action, Response response, Object result) {
        logger.log(Level.INFO, "Successfully executed " + action.getPath() + ", result: " + response.getText());
    }

    @Override
    public void onFailure(RestAction action, Response response, Throwable caught) {
        logger.log(Level.INFO, "Failed to executed " + action.getPath() + ", result: "
            + response.getStatusText() + " " + response.getText() + " " + caught.getMessage());
    }
}
