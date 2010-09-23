/**
 * Copyright 2010 ArcBees Inc. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */

package com.gwtplatform.dispatch.server.guice.actionHandlerValidator;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.gwtplatform.dispatch.server.actionHandler.ActionHandler;
import com.gwtplatform.dispatch.server.actionHandlerValidator.ActionHandlerValidatorLinkerHelper;
import com.gwtplatform.dispatch.server.actionHandlerValidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.actionValidator.ActionValidator;
import com.gwtplatform.dispatch.server.guice.GuiceBeanProvider;

/**
 * This class links any registered {@link ActionHandler} and {@link ActionValidator} instances with the default
 * {@link ActionHandlerValidatorRegistry}
 * 
 * @author Christian Goudreau
 */
public class ActionHandlerValidatorLinker {

	@Inject
	public static void linkValidators(Injector injector, ActionHandlerValidatorRegistry registry) {
		ActionHandlerValidatorLinkerHelper.linkValidators(new GuiceBeanProvider(injector), registry);
	}

	private ActionHandlerValidatorLinker() {
	}
}
