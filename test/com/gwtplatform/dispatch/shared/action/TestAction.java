package com.gwtplatform.dispatch.shared.action;

import com.gwtplatform.dispatch.shared.UnsecuredActionImpl;

public class TestAction extends UnsecuredActionImpl<TestResult> {

	private String testMessage;

	/**
	 * Serialization purposes
	 */
	@SuppressWarnings("unused")
	private TestAction() {
	}

	public TestAction(String testMessage) {
		this.testMessage = testMessage;
	}

	public String getTestMessage() {
		return testMessage;
	}

	public void setTestMessage(String testMessage) {
		this.testMessage = testMessage;
	}
}