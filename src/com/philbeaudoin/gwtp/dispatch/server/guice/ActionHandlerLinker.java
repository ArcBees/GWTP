package com.philbeaudoin.gwtp.dispatch.server.guice;

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



import java.util.List;

import com.philbeaudoin.gwtp.dispatch.server.ActionHandler;
import com.philbeaudoin.gwtp.dispatch.server.ActionHandlerRegistry;
import com.philbeaudoin.gwtp.dispatch.server.ClassActionHandlerRegistry;
import com.philbeaudoin.gwtp.dispatch.server.InstanceActionHandlerRegistry;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * This class links any registered {@link ActionHandler} instances with the
 * default {@link ActionHandlerRegistry}.
 * 
 * @author David Peterson
 * 
 */
public final class ActionHandlerLinker {

    private ActionHandlerLinker() {
    }

    @Inject
    @SuppressWarnings("unchecked")
    public static void linkHandlers( Injector injector, ActionHandlerRegistry registry ) {
        List<Binding<ActionHandlerMap>> bindings = injector.findBindingsByType( TypeLiteral
                .get( ActionHandlerMap.class ) );

        if ( registry instanceof InstanceActionHandlerRegistry ) {
            InstanceActionHandlerRegistry instanceRegistry = ( InstanceActionHandlerRegistry ) registry;

            for ( Binding<ActionHandlerMap> binding : bindings ) {
                Class<? extends ActionHandler<?, ?>> handlerClass = binding.getProvider().get()
                        .getActionHandlerClass();
                ActionHandler<?, ?> handler = injector.getInstance( handlerClass );
                instanceRegistry.addHandler( handler );
            }
        } else if ( registry instanceof ClassActionHandlerRegistry ) {
            ClassActionHandlerRegistry classRegistry = ( ClassActionHandlerRegistry ) registry;

            for ( Binding<ActionHandlerMap> binding : bindings ) {
                ActionHandlerMap map = binding.getProvider().get();
                classRegistry.addHandlerClass( map.getActionClass(), map.getActionHandlerClass() );
            }
        }

    }
}
