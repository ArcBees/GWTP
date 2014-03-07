/**
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

package com.gwtplatform.dispatch.rpc.server.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorClass;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorMapImpl;
import com.gwtplatform.dispatch.rpc.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.rpc.server.spring.actionvalidator.DefaultActionValidator;
import com.gwtplatform.dispatch.rpc.server.spring.utils.SpringUtils;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;

@Import({DispatchModule.class})
public abstract class HandlerModule {
    @Autowired
    protected ApplicationContext applicationContext;

    protected <A extends Action<R>, R extends Result> void bindHandler(Class<A> actionClass,
            Class<? extends ActionHandler<A, R>> handlerClass) {
        SpringUtils.registerBean(applicationContext, new ActionHandlerValidatorMapImpl<A, R>(actionClass,
                new ActionHandlerValidatorClass<A, R>(handlerClass,
                        DefaultActionValidator.class)));
    }

    protected <A extends Action<R>, R extends Result> void bindHandler(Class<A> actionClass,
            Class<? extends ActionHandler<A, R>> handlerClass,
            Class<? extends ActionValidator> actionValidator) {
        SpringUtils.registerBean(applicationContext, new ActionHandlerValidatorMapImpl<A, R>(actionClass,
                new ActionHandlerValidatorClass<A, R>(handlerClass,
                actionValidator)));
    }

    protected abstract void configureHandlers();
}
