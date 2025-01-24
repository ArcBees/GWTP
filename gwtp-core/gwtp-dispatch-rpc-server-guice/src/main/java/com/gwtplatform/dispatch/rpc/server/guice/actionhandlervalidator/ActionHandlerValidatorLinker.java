/*
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.dispatch.rpc.server.guice.actionhandlervalidator;

import com.google.inject.Injector;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorLinkerHelper;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.rpc.server.guice.GuiceBeanProvider;

import jakarta.inject.Inject;

/**
 * This class links any registered {@link com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler ActionHandler}
 * and {@link com.gwtplatform.dispatch.rpc.server.actionvalidator.ActionValidator ActionValidator}
 * instances with the default {@link ActionHandlerValidatorRegistry}.
 */
public class ActionHandlerValidatorLinker {

    private ActionHandlerValidatorLinker() {
    }

    @Inject
    public static void linkValidators(Injector injector, ActionHandlerValidatorRegistry registry) {
        ActionHandlerValidatorLinkerHelper.linkValidators(new GuiceBeanProvider(injector), registry);
    }
}
