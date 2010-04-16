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

package com.philbeaudoin.gwtp.dispatch.server.guice;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import com.philbeaudoin.gwtp.dispatch.server.ActionHandler;
import com.philbeaudoin.gwtp.dispatch.server.LazyActionHandlerRegistry;
import com.philbeaudoin.gwtp.dispatch.shared.Action;

/**
 * This will use Guice to create instances of registered {@link ActionHandler}s
 * on in a lazy manner. That is, they are only created upon the first request of
 * a handler for the {@link Action} it is registered with, rather than requiring
 * the class to be constructed when the registry is initialised.
 * 
 * @author David Peterson
 */
@Singleton
public class GuiceLazyActionHandlerRegistry extends LazyActionHandlerRegistry {
    private final Injector injector;

    @Inject
    public GuiceLazyActionHandlerRegistry( Injector injector ) {
        this.injector = injector;
    }

    @Override
    protected ActionHandler<?, ?> createInstance( Class<? extends ActionHandler<?, ?>> handlerClass ) {
        return injector.getInstance( handlerClass );
    }

}
