package com.gwtplatform.dispatch.server.actionhandler;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionHandler.AbstractActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.action.TestAction;
import com.gwtplatform.dispatch.shared.action.TestResult;

public class TestActionHandler extends AbstractActionHandler<TestAction, TestResult> {

	public TestActionHandler() {
		super(TestAction.class);
	}

	@Override
	public TestResult execute(TestAction action, ExecutionContext context) throws ActionException {
		if (action.getTestMessage().equals("test")) {
			return new TestResult(true);
		}
		return new TestResult(false);
	}

	@Override
	public void undo(TestAction action, TestResult result, ExecutionContext context) throws ActionException {
		//no undo support
	}
}