package com.philbeaudoin.gwtp.dispatch.server.guice;

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
