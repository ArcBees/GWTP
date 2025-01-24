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

import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Injector;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorClass;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorInstance;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.LazyActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.rpc.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * This is a lazy-loading implementation of the registry. It will only create action handlers and validators when they
 * are first used. All {@link com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler} and
 * {@link ActionValidator} implementations <b>must</b> have a public, default constructor.
 */
@Singleton
public class LazyActionHandlerValidatorRegistryImpl implements
        LazyActionHandlerValidatorRegistry {
    private final ConcurrentHashMap<Class<? extends Action<?>>, ActionHandlerValidatorClass<? extends Action<?>,
            ? extends Result>> actionHandlerValidatorClasses;
    private final ConcurrentHashMap<Class<? extends Action<?>>,
            ActionHandlerValidatorInstance> actionHandlerValidatorInstances;
    private final Injector injector;
    private final ConcurrentHashMap<Class<? extends ActionValidator>, ActionValidator> validators;

    @Inject
    LazyActionHandlerValidatorRegistryImpl(Injector injector) {
        this.injector = injector;
        actionHandlerValidatorClasses = new ConcurrentHashMap<Class<? extends Action<?>>,
                ActionHandlerValidatorClass<? extends Action<?>, ? extends Result>>();
        actionHandlerValidatorInstances = new ConcurrentHashMap<Class<? extends Action<?>>,
                ActionHandlerValidatorInstance>();
        validators = new ConcurrentHashMap<Class<? extends ActionValidator>, ActionValidator>();
    }

    @Override
    public <A extends Action<R>, R extends Result> void addActionHandlerValidatorClass(
            Class<A> actionClass,
            ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass) {
        actionHandlerValidatorClasses.putIfAbsent(actionClass, actionHandlerValidatorClass);
    }

    @Override
    public void clearActionHandlerValidators() {
        actionHandlerValidatorInstances.clear();
        validators.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends Action<R>, R extends Result> ActionHandlerValidatorInstance findActionHandlerValidator(
            A action) {

        ActionHandlerValidatorInstance actionHandlerValidatorInstance = actionHandlerValidatorInstances.get(action
                .getClass());

        if (actionHandlerValidatorInstance == null) {
            ActionHandlerValidatorClass<? extends Action<?>, ? extends Result> actionHandlerValidatorClass =
                    actionHandlerValidatorClasses.get(action.getClass());
            if (actionHandlerValidatorClass != null) {
                actionHandlerValidatorInstance = createInstance(actionHandlerValidatorClass);
                if (actionHandlerValidatorInstance != null) {
                    actionHandlerValidatorInstances.putIfAbsent(
                            (Class<? extends Action<?>>) action.getClass(),
                            actionHandlerValidatorInstance);
                }
            }
        }

        return actionHandlerValidatorInstance;
    }

    @Override
    public ActionValidator findActionValidator(
            Class<? extends ActionValidator> actionValidatorClass) {
        return validators.get(actionValidatorClass);
    }

    @Override
    public <A extends Action<R>, R extends Result> void removeActionHandlerValidatorClass(
            Class<A> actionClass,
            ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass) {

        ActionHandlerValidatorClass<?, ?> oldActionHandlerValidatorClass = actionHandlerValidatorClasses.get(
                actionClass);

        if (oldActionHandlerValidatorClass == actionHandlerValidatorClass) {
            actionHandlerValidatorClasses.remove(actionClass);
            ActionHandlerValidatorInstance instance = actionHandlerValidatorInstances.remove(actionClass);

            if (!containValidator(instance.getActionValidator())) {
                validators.remove(instance.getActionValidator().getClass());
            }
        }
    }

    private boolean containValidator(ActionValidator actionValidator) {
        for (ActionHandlerValidatorInstance validator : actionHandlerValidatorInstances.values()) {
            if (validator.getActionValidator().getClass().equals(
                    actionValidator.getClass())) {
                return true;
            }
        }

        return false;
    }

    private ActionHandlerValidatorInstance createInstance(
            ActionHandlerValidatorClass<? extends Action<?>, ? extends Result> actionHandlerValidatorClass) {

        ActionHandlerValidatorInstance actionHandlerValidatorInstance;
        ActionValidator actionValidator = findActionValidator(actionHandlerValidatorClass.getActionValidatorClass());

        if (actionValidator == null) {
            actionValidator = injector.getInstance(actionHandlerValidatorClass.getActionValidatorClass());

            actionHandlerValidatorInstance = new ActionHandlerValidatorInstance(
                    actionValidator,
                    injector.getInstance(actionHandlerValidatorClass.getActionHandlerClass()));

            validators.putIfAbsent(actionValidator.getClass(), actionValidator);
        } else {
            actionHandlerValidatorInstance = new ActionHandlerValidatorInstance(
                    actionValidator,
                    injector.getInstance(actionHandlerValidatorClass.getActionHandlerClass()));
        }

        if (actionHandlerValidatorInstance.getActionHandler() == null
                || actionHandlerValidatorInstance.getActionValidator() == null) {
            return null;
        }

        return actionHandlerValidatorInstance;
    }
}
