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



import java.util.HashMap;
import java.util.Map;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

import com.google.inject.Singleton;

@Singleton
public class DefaultActionHandlerRegistry implements InstanceActionHandlerRegistry {

  private final Map< Class<? extends Action<? extends Result>>, 
      ActionHandler<? extends Action<? extends Result>, ? extends Result> > handlers;

  public DefaultActionHandlerRegistry() {
    handlers = new HashMap< Class<? extends Action<? extends Result>>, 
        ActionHandler<? extends Action<? extends Result>, ? extends Result> >( 100 );
  }

  @Override
  public <A extends Action<R>, R extends Result> void addHandler( ActionHandler<A, R> handler ) {
    handlers.put( handler.getActionType(), handler );
  }

  @Override
  public <A extends Action<R>, R extends Result> boolean removeHandler( ActionHandler<A, R> handler ) {
    return handlers.remove( handler.getActionType() ) != null;
  }

  @SuppressWarnings("unchecked")
  public <A extends Action<R>, R extends Result> ActionHandler<A, R> findHandler( A action ) {
    return (ActionHandler<A, R>) handlers.get( action.getClass() );
  }

  public void clearHandlers() {
    handlers.clear();
  }

}
