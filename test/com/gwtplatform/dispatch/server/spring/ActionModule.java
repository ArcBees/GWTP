package com.gwtplatform.dispatch.server.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.gwtplatform.dispatch.server.actionhandler.TestActionHandler;
import com.gwtplatform.dispatch.server.spring.configuration.DefaultModule;

@Import(DefaultModule.class)
public class ActionModule {

	@Bean
	public TestActionHandler getTestActionHandler() {
		return new TestActionHandler();
	}

}