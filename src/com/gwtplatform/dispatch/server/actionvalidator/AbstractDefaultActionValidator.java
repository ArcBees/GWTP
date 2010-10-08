package com.gwtplatform.dispatch.server.actionValidator;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * The default {@link ActionValidator} implementation. It'll accept every action.
 * 
 * @author Christian Goudreau
 */
public class AbstractDefaultActionValidator implements ActionValidator {

	@Override
	public boolean isValid(Action<? extends Result> action) {
		return true;
	}
}
