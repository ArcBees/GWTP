package com.philbeaudoin.gwtp.dispatch.server;

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



import java.util.Map;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * This is a lazy-loading implementation of the registry. It will only create
 * action handlers when they are first used. All {@link ActionHandler}
 * implementations <b>must</b> have a public, default constructor.
 * 
 * @author David Peterson
 */
public class LazyActionHandlerRegistry implements ClassActionHandlerRegistry {

    private final Map<Class<? extends Action<?>>, Class<? extends ActionHandler<?, ?>>> handlerClasses;

    private final Map<Class<? extends Action<?>>, ActionHandler<?, ?>> handlers;

    public LazyActionHandlerRegistry() {
        handlerClasses = new java.util.HashMap<Class<? extends Action<?>>, Class<? extends ActionHandler<?, ?>>>(
                100 );
        handlers = new java.util.HashMap<Class<? extends Action<?>>, ActionHandler<?, ?>>( 100 );
    }

    public <A extends Action<R>, R extends Result> void addHandlerClass( Class<A> actionClass,
            Class<? extends ActionHandler<A, R>> handlerClass ) {
        handlerClasses.put( actionClass, handlerClass );
    }

    public <A extends Action<R>, R extends Result> void removeHandlerClass( Class<A> actionClass,
            Class<? extends ActionHandler<A, R>> handlerClass ) {
        Class<? extends ActionHandler<?, ?>> oldHandlerClass = handlerClasses.get( actionClass );
        if ( oldHandlerClass == handlerClass ) {
            handlerClasses.remove( actionClass );
            handlers.remove( actionClass );
        }
    }

    @SuppressWarnings("unchecked")
    public <A extends Action<R>, R extends Result> ActionHandler<A, R> findHandler( A action ) {
        ActionHandler<?, ?> handler = handlers.get( action.getClass() );
        if ( handler == null ) {
            Class<? extends ActionHandler<?, ?>> handlerClass = handlerClasses.get( action.getClass() );
            if ( handlerClass != null ) {
                handler = createInstance( handlerClass );
                if ( handler != null )
                    handlers.put( handler.getActionType(), handler );
            }
        }

        return (ActionHandler<A, R>) handler;
    }

    protected ActionHandler<?, ?> createInstance( Class<? extends ActionHandler<?, ?>> handlerClass ) {
        try {
            return handlerClass.newInstance();
        } catch ( InstantiationException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void clearHandlers() {
        handlers.clear();
    }

}
