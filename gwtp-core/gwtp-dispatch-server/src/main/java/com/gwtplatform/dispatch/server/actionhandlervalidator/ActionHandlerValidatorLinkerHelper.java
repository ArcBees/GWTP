/**
 * Copyright 2010 ArcBees Inc.
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

package com.gwtplatform.dispatch.server.actionhandlervalidator;

import java.util.Iterator;

import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorLinkerHelper.BeanProvider.BindingDescriptor;
import com.gwtplatform.dispatch.server.actionvalidator.ActionValidator;

/**
 * @author Peter Simun (simun@seges.sk)
 */
public class ActionHandlerValidatorLinkerHelper {

  /**
   * @author Peter Simun (simun@seges.sk)
   */
  public static interface BeanProvider {

    <B> B getInstance(Class<B> clazz);

    <B> Iterator<BindingDescriptor<B>> getBindings(Class<B> clazz);

    /**
     * @author Peter Simun (simun@seges.sk)
     *
     * @param <B>
     */
    public static interface BindingDescriptor<B> {

      String getBeanName();

      B getBean();
    }
  }

  /**
   * BingingDescriptor implementation for the Guice/Spring. This allows us to obtain bindings from guice injector/or
   * from Spring application context
   *
   * @author Peter Simun (simun@seges.sk)
   */
  public static class CommonBindingDescriptor<B> implements BindingDescriptor<B> {

    private String name;
    private B bean;

    public CommonBindingDescriptor(B bean, String name) {
      this.name = name;
      this.bean = bean;
    }

    @Override
    public String getBeanName() {
      return name;
    }

    @Override
    public B getBean() {
      return bean;
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static void linkValidators(BeanProvider beanProvider, ActionHandlerValidatorRegistry registry) {
    Iterator<BindingDescriptor<ActionHandlerValidatorMap>> bindings = beanProvider.getBindings(ActionHandlerValidatorMap.class);

    if (registry instanceof EagerActionHandlerValidatorRegistry) {
      EagerActionHandlerValidatorRegistry instanceRegistry = (EagerActionHandlerValidatorRegistry) registry;

      while (bindings.hasNext()) {
        BindingDescriptor<ActionHandlerValidatorMap> binding = bindings.next();

        Class<? extends ActionValidator> actionValidatorClass = binding.getBean().getActionHandlerValidatorClass().getActionValidatorClass();
        Class<? extends ActionHandler<?, ?>> handlerClass = binding.getBean().getActionHandlerValidatorClass().getActionHandlerClass();

        ActionHandlerValidatorInstance actionHandlerValidatorInstance = null;
        ActionValidator actionValidator = instanceRegistry.findActionValidator(actionValidatorClass);

        if (actionValidator == null) {
          actionValidator = beanProvider.getInstance(actionValidatorClass);
        }

        actionHandlerValidatorInstance = new ActionHandlerValidatorInstance(actionValidator, beanProvider.getInstance(handlerClass));

        instanceRegistry.addActionHandlerValidator(binding.getBean().getActionClass(), actionHandlerValidatorInstance);
      }
    } else if (registry instanceof LazyActionHandlerValidatorRegistry) {
      LazyActionHandlerValidatorRegistry classRegistry = (LazyActionHandlerValidatorRegistry) registry;

      while (bindings.hasNext()) {
        BindingDescriptor<ActionHandlerValidatorMap> binding = bindings.next();
        ActionHandlerValidatorMap map = binding.getBean();
        classRegistry.addActionHandlerValidatorClass(map.getActionClass(), map.getActionHandlerValidatorClass());
      }
    }
  }
}