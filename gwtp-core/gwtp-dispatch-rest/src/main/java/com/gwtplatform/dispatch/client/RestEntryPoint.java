package com.gwtplatform.dispatch.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.gwtplatform.dispatch.client.rest.RestDispatcherController;

public class RestEntryPoint implements EntryPoint {
    private static final RestDispatcherController controller = GWT.create(RestDispatcherController.class);

    @Override
    public void onModuleLoad() {
    }
}
