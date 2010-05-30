/**
 * Copyright 2010 GWT-Platform
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

package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.philbeaudoin.gwtp.mvp.client.EventBus;
import com.philbeaudoin.gwtp.mvp.client.Presenter;
import com.philbeaudoin.gwtp.mvp.client.PresenterImpl;

/**
 * A useful mixing class to define a {@link Proxy} that is also
 * a {@link Place}. You can usually inherit from the simpler 
 * form {@link ProxyPlace}.
 * <p />
 * @param <P> Type of the associated {@link Presenter}.
 * @param <Proxy_> Type of the associated {@link Proxy}.
 *
 * @author David Peterson
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
public class ProxyPlaceAbstract<P extends Presenter, Proxy_ extends Proxy<P>>
implements ProxyPlace<P> {

  protected ProxyFailureHandler failureHandler;
  protected EventBus eventBus;
  protected PlaceManager placeManager;
  protected Proxy_ proxy;
  protected Place place;

  /**
   * Creates a {@link ProxyPlaceAbstract}. That is, the {@link Proxy} of a 
   * {@link Presenter} attached to a {@link Place}. This presenter 
   * can be invoked by setting a history token that matches
   * its name token in the URL bar.
   */
  public ProxyPlaceAbstract() {}

  /**
   * Injects the various resources and performs other bindings. 
   * <p />
   * Never call directly, it should only be called by GIN.
   * Method injection is used instead of constructor injection, because the 
   * latter doesn't work well with GWT generators.
   * 
   * @param failureHandler The {@link ProxyFailureHandler}.
   * @param placeManager The {@link PlaceManager}.
   * @param eventBus The {@link EventBus}.
   */
  @Inject
  protected void bind( ProxyFailureHandler failureHandler, final PlaceManager placeManager, EventBus eventBus ) {
    this.failureHandler = failureHandler;
    this.eventBus = eventBus;
    this.placeManager = placeManager;
    eventBus.addHandler( PlaceRequestEvent.getType(), new PlaceRequestHandler() {
      public void onPlaceRequest( PlaceRequestEvent event ) {
        if( event.isHandled() )
          return;
        PlaceRequest request = event.getRequest();
        if ( matchesRequest( request ) ) {
          event.setHandled();
          if (canReveal() ) {
            handleRequest( request );
          } else {
            placeManager.revealUnauthorizedPlace( request.getNameToken() );
          }
        }
      }
    } );
  }


  ///////////////////////
  // Inherited from Proxy
  
  @Override
  public final void reveal() {
    handleRequest( new PlaceRequest(getNameToken()) );
  }

  @Override
  public final void reveal(PlaceRequest request) {
    handleRequest(request);
  }

  @Override
  public void getRawPresenter(AsyncCallback<Presenter> callback) {
    proxy.getRawPresenter(callback);
  }

  @Override
  public void getPresenter(AsyncCallback<P> callback) {
    proxy.getPresenter(callback);
  }

  @Override
  public void onPresenterChanged( Presenter presenter ) {
    proxy.onPresenterChanged( presenter );
    placeManager.onPlaceChanged( ((PresenterImpl<?,?>)presenter).prepareRequest( new PlaceRequest(getNameToken())) );  
  }

  @Override
  public void onPresenterRevealed( Presenter presenter ) {
    proxy.onPresenterRevealed( presenter );    
    placeManager.onPlaceRevealed( ((PresenterImpl<?,?>)presenter).prepareRequest( new PlaceRequest(getNameToken())) );  
  }


  ///////////////////////
  // Inherited from Place

  @Override
  public final boolean equals( Object o ) {
    return place.equals(o);
  }

  @Override
  public final int hashCode() {
    return place.hashCode();
  }

  @Override
  public final String toString() {
    return place.toString();
  }

  @Override
  public String getNameToken() {
    return place.getNameToken();
  }

  @Override
  public boolean matchesRequest(PlaceRequest request) {
    return place.matchesRequest(request);
  }

  @Override
  public boolean canReveal() {
    return place.canReveal();
  }

  ///////////////////////
  // Private methods

  /**
   * Prepares the presenter with the information contained in the current 
   * request, then reveals it. Will refuse to reveal the display and do 
   * nothing if {@link canReveal()} returns <code>false</code>. 
   *
   * @param request The request to handle. Can pass <code>null</code> if
   *                no request is used, in which case the presenter will
   *                be directly revealed. 
   */
  private final void handleRequest( final PlaceRequest request ) {
    proxy.getPresenter( new AsyncCallback<P>() {

      @Override
      public void onFailure(Throwable caught) {
        failureHandler.onFailedGetPresenter(caught);
      }

      @Override
      public void onSuccess(final P presenter) {
        // Everything should be bound before we prepare the presenter from the request,
        // in case it wants to fire some events. That's why we will do this in a 
        // deferred command.
        DeferredCommand.addCommand( new Command() {
          @Override
          public void execute() {
            PresenterImpl<?,?> presenterImpl = (PresenterImpl<?,?>)presenter;
            presenterImpl.prepareFromRequest( request );
            if( !presenter.isVisible() )
              presenterImpl.forceReveal();  // This will trigger a reset in due time
            else
              ResetPresentersEvent.fire( eventBus ); // We have to do the reset ourselves                
          } } );
      }
    } );

  }

}

