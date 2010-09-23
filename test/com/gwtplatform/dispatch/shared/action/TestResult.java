package com.gwtplatform.dispatch.shared.action;

import com.gwtplatform.dispatch.shared.Result;

public class TestResult implements Result {

	private Boolean result;

	/**
	 * Serialiation purposes only
	 */
	@SuppressWarnings("unused")
	private TestResult() {
	}

	public TestResult(Boolean result) {
		this.result = result;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}
}
