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



import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * Instances of this interface allow {@link ActionHandler} classes to be
 * registered for specific {@link Action} types. This is typcially to allow
 * lazy-loading of handlers.
 * 
 * @author David Peterson
 * @see LazyActionHandlerRegistry
 * 
 */
public interface ClassActionHandlerRegistry extends ActionHandlerRegistry {
    /**
     * Registers the specified {@link ActionHandler} class with the registry.
     * The class will only
     * 
     * @param actionClass
     *            The action class the handler handles.
     * @param handlerClass
     *            The handler class.
     */
    public <A extends Action<R>, R extends Result> void addHandlerClass( Class<A> actionClass,
            Class<? extends ActionHandler<A, R>> handlerClass );

    /**
     * Removes any registration of the specified class, as well as any instances
     * which have been created.
     * 
     * @param handlerClass
     */
    public <A extends Action<R>, R extends Result> void removeHandlerClass( Class<A> actionClass,
            Class<? extends ActionHandler<A, R>> handlerClass );
}
