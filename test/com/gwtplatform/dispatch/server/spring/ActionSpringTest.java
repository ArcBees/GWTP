package com.gwtplatform.dispatch.server.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gwtplatform.dispatch.server.AbstractActionTest;
import com.gwtplatform.dispatch.server.Dispatch;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.ServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:test/com/gwtplatform/dispatch/server/spring/spring-test-context.xml"})
public class ActionSpringTest extends AbstractActionTest {

	@Autowired
	private Dispatch dispatchService;

	@Test
	public void testAction() throws ActionException, ServiceException {
		super.testAction(dispatchService);
	}
}