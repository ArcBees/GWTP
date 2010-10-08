package com.gwtplatform.dispatch.server.guice;

import com.gwtplatform.dispatch.server.actionhandler.TestActionHandler;
import com.gwtplatform.dispatch.shared.action.TestAction;

public class ActionModule extends HandlerModule {

	@Override
	protected void configureHandlers() {
		bindHandler(TestAction.class, TestActionHandler.class);
	}
}