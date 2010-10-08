package com.gwtplatform.dispatch.server;

import junit.framework.Assert;

import com.gwtplatform.dispatch.server.actionhandler.TestActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.ServiceException;
import com.gwtplatform.dispatch.shared.action.TestAction;
import com.gwtplatform.dispatch.shared.action.TestResult;

public class AbstractActionTest {

	protected void testAction(Dispatch dispatch) throws ActionException, ServiceException {
		TestAction action = new TestAction(TestActionHandler.MESSAGE);
		TestResult result = dispatch.execute(action);
		Assert.assertTrue("Invalid action result! Processing error occured", result.getResult());
	}
}