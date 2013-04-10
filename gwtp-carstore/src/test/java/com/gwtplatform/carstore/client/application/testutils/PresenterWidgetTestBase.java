package com.gwtplatform.carstore.client.application.testutils;

import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import com.gwtplatform.dispatch.shared.DispatchAsync;

/**
 * Basic configuration for PresenterWidgetTest, you test must extends this
 * class, or don't forget to inject everything inside your presenter's test.
 * 
 * @author Christian Goudreau
 */
public abstract class PresenterWidgetTestBase {
    @Inject
    public EventBus eventBus;
    @Inject
    public RelayingDispatcher dispatcher;
    @Inject
    @Named("mock")
    public DispatchAsync mockDispatcher;
}
