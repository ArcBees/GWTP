package com.gwtplatform.dispatch.rest.client;

import com.google.gwt.http.client.Response;
import com.gwtplatform.dispatch.rest.shared.RestAction;

public class DefaultRestDispatchHooks implements RestDispatchHooks {
    @Override
    public void onExecute(RestAction action) {
    }

    @Override
    public void onSuccess(RestAction action, Response response, Object result) {
    }

    @Override
    public void onFailure(RestAction action, Response response, Throwable caught) {
    }
}
