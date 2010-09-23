package com.gwtplatform.dispatch.server.guice;

import com.google.gwt.inject.client.AbstractGinModule;
import com.gwtplatform.dispatch.server.actionhandler.TestActionHandler;

public class ActionModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(TestActionHandler.class).asEagerSingleton();
	}
}