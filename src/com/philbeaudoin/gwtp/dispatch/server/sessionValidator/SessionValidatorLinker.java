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

package com.philbeaudoin.gwtp.dispatch.server.sessionValidator;

import java.util.List;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * This class links any registered {@link ActionValidator} instances with
 * the default {@link SessionValidatorRegistry}
 * 
 * @author Christian Goudreau
 * 
 */
public class SessionValidatorLinker {
    private SessionValidatorLinker() {}

    @SuppressWarnings("unchecked")
    @Inject
    public static void linkValidators(Injector injector, SessionValidatorRegistry registry) {
        List<Binding<SessionValidatorMap>> bindings = injector.findBindingsByType(TypeLiteral.get(SessionValidatorMap.class));

        if (registry instanceof InstanceSessionValidatorRegistry) {
            InstanceSessionValidatorRegistry instanceRegistry = (InstanceSessionValidatorRegistry) registry;

            for (Binding<SessionValidatorMap> binding : bindings) {
                Class<? extends ActionValidator> secureSessionValidatorClass = binding.getProvider().get().getSecureSessionValidatorClass();
                ActionValidator secureSessionValidator = injector.getInstance(secureSessionValidatorClass);
                instanceRegistry.addSecureSessionValidator(binding.getProvider().get().getActionClass(), secureSessionValidator);
            }
        } else if (registry instanceof ClassSessionValidatorRegistry) {
            ClassSessionValidatorRegistry classRegistry = (ClassSessionValidatorRegistry) registry;

            for (Binding<SessionValidatorMap> binding : bindings) {
                SessionValidatorMap map = binding.getProvider().get();
                classRegistry.addSecureSessionValidatorClass(map.getActionClass(), map.getSecureSessionValidatorClass());
            }
        }
    }
}