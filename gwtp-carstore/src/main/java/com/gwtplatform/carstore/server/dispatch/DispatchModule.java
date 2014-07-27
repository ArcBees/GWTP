package com.gwtplatform.carstore.server.dispatch;

import com.gwtplatform.carstore.shared.dispatch.LogInAction;
import com.gwtplatform.dispatch.rpc.server.guice.HandlerModule;

public class DispatchModule extends HandlerModule {
    @Override
    protected void configureHandlers() {
        install(new com.gwtplatform.dispatch.rpc.server.guice.DispatchModule());

        bindHandler(LogInAction.class, LogInHandler.class);
    }
}
