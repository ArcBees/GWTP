package com.gwtplatform.dispatch.server.guice;

import org.junit.runner.RunWith;

import com.gwtplatform.dispatch.server.actionhandler.TestActionHandler;
import com.gwtplatform.mvp.client.EventBus;
import com.gwtplatform.testing.GuiceMockitoJUnitRunner;
import com.gwtplatform.testing.TestModule;
import com.gwtplatform.testing.TestScope;

@RunWith(GuiceMockitoJUnitRunner.class)
public class ActionGuiceTest {

	public static class Env extends TestModule {

		@Override
		protected void configure() {
			bindMock(EventBus.class).in(TestScope.SINGLETON);
			bind(TestActionHandler.class).asEagerSingleton();
		}
	}

	//	@Inject
	//	Provider<> Provider;

	public void testConfiguration() {

	}
}