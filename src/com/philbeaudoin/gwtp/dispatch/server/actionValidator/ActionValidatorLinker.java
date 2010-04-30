/**
 * Copyright 2010 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.dispatch.server.actionValidator;

import java.util.List;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * This class links any registered {@link ActionValidator} instances with
 * the default {@link ActionValidatorRegistry}
 * 
 * @author Christian Goudreau
 * 
 */
public class ActionValidatorLinker {
    private ActionValidatorLinker() {}

    @SuppressWarnings("unchecked")
    @Inject
    public static void linkValidators(Injector injector, ActionValidatorRegistry registry) {
        List<Binding<ActionValidatorMap>> bindings = injector.findBindingsByType(TypeLiteral.get(ActionValidatorMap.class));

        if (registry instanceof InstanceActionValidatorRegistry) {
            InstanceActionValidatorRegistry instanceRegistry = (InstanceActionValidatorRegistry) registry;

            for (Binding<ActionValidatorMap> binding : bindings) {
                Class<? extends ActionValidator> actionValidatorClass = binding.getProvider().get().getActionValidatorClass();
                ActionValidator actionValidator = injector.getInstance(actionValidatorClass);
                instanceRegistry.addActionValidator(binding.getProvider().get().getActionClass(), actionValidator);
            }
        } else if (registry instanceof ClassActionValidatorRegistry) {
            ClassActionValidatorRegistry classRegistry = (ClassActionValidatorRegistry) registry;

            for (Binding<ActionValidatorMap> binding : bindings) {
                ActionValidatorMap map = binding.getProvider().get();
                classRegistry.addActionValidatorClass(map.getActionClass(), map.getActionValidatorClass());
            }
        }
    }
}