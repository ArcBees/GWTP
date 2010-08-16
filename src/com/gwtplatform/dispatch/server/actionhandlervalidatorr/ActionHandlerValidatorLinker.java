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

package com.gwtplatform.dispatch.server.actionhandlervalidatorr;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

import com.gwtplatform.dispatch.server.actionhandlerr.ActionHandler;
import com.gwtplatform.dispatch.server.actionvalidatorr.ActionValidator;

import java.util.List;

/**
 * This class links any registered {@link ActionHandler} and
 * {@link ActionValidator} instances with the default
 * {@link ActionHandlerValidatorRegistry}
 * 
 * @author Christian Goudreau
 * 
 */
public class ActionHandlerValidatorLinker {
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Inject
  public static void linkValidators(Injector injector,
      ActionHandlerValidatorRegistry registry) {
    List<Binding<ActionHandlerValidatorMap>> bindings = injector.findBindingsByType(TypeLiteral.get(ActionHandlerValidatorMap.class));

    if (registry instanceof EagerActionHandlerValidatorRegistry) {
      EagerActionHandlerValidatorRegistry instanceRegistry = (EagerActionHandlerValidatorRegistry) registry;

      for (Binding<ActionHandlerValidatorMap> binding : bindings) {
        Class<? extends ActionValidator> actionValidatorClass = binding.getProvider().get().getActionHandlerValidatorClass().getActionValidatorClass();
        Class<? extends ActionHandler<?, ?>> handlerClass = binding.getProvider().get().getActionHandlerValidatorClass().getActionHandlerClass();

        ActionHandlerValidatorInstance actionHandlerValidatorInstance = null;
        ActionValidator actionValidator = instanceRegistry.findActionValidator(actionValidatorClass);

        if (actionValidator == null) {
          actionValidator = injector.getInstance(actionValidatorClass);
        }

        actionHandlerValidatorInstance = new ActionHandlerValidatorInstance(
            actionValidator, injector.getInstance(handlerClass));

        instanceRegistry.addActionHandlerValidator(
            binding.getProvider().get().getActionClass(),
            actionHandlerValidatorInstance);
      }
    } else if (registry instanceof LazyActionHandlerValidatorRegistry) {
      LazyActionHandlerValidatorRegistry classRegistry = (LazyActionHandlerValidatorRegistry) registry;

      for (Binding<ActionHandlerValidatorMap> binding : bindings) {
        ActionHandlerValidatorMap map = binding.getProvider().get();
        classRegistry.addActionHandlerValidatorClass(map.getActionClass(),
            map.getActionHandlerValidatorClass());
      }
    }
  }

  private ActionHandlerValidatorLinker() {
  }
}
