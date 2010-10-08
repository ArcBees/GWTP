package com.gwtplatform.dispatch.server.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.gwtplatform.dispatch.server.actionValidator.ActionValidator;
import com.gwtplatform.dispatch.server.actionhandler.TestActionHandler;
import com.gwtplatform.dispatch.server.guice.actionValidator.DefaultActionValidator;
import com.gwtplatform.dispatch.server.spring.configuration.DefaultModule;
import com.gwtplatform.dispatch.shared.action.TestAction;

@Configuration
@Import({DefaultModule.class, DispatchModule.class})
public class ActionModule extends HandlerModule {

	public ActionModule() {
	}

	@Bean
	public TestActionHandler getTestActionHandler() {
		return new TestActionHandler();
	}

	@Bean
	public ActionValidator getDefaultActionValidator() {
		return new DefaultActionValidator();
	}

	protected void configureHandlers() {
		bindHandler(TestAction.class, TestActionHandler.class);
	}
}