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

package com.gwtplatform.mvp.client.proxy;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.gwtplatform.mvp.client.EventBus;
import com.gwtplatform.mvp.client.Presenter;

/**
 * A useful mixing class to define a {@link Proxy} that is also a {@link Place}.
 * You can usually inherit from the simpler form {@link ProxyPlace}.
 * <p />
 * 
 * @param <P> Type of the associated {@link Presenter}.
 * @param <Proxy_> Type of the associated {@link Proxy}.
 * 
 * @author David Peterson
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
@SuppressWarnings("deprecation")
// TODO: Remove after making members private
public class ProxyPlaceAbstract<P extends Presenter<?>, Proxy_ extends Proxy<P>>
    implements ProxyPlace<P> {

  /**
   * The {@link EventBus} for the application.
   * 
   * Deprecated to use directly, use {@link #getEventBus()} instead.
   */
  @Deprecated
  protected EventBus eventBus; // TODO: Make private.

  protected ProxyFailureHandler failureHandler;
  protected Place place;
  protected PlaceManager placeManager;
  protected Proxy_ proxy;

  /**
   * Creates a {@link ProxyPlaceAbstract}. That is, the {@link Proxy} of a
   * {@link Presenter} attached to a {@link Place}. This presenter can be
   * invoked by setting a history token that matches its name token in the URL
   * bar.
   */
  public ProxyPlaceAbstract() {
  }

  @Override
  public boolean canReveal() {
    return place.canReveal();
  }

  // /////////////////////
  // Inherited from Proxy

  @Override
  public final boolean equals(Object o) {
    return place.equals(o);
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    eventBus.fireEvent(event);
  }

  @Override
  public final EventBus getEventBus() {
    return eventBus;
  }

  @Override
  public String getNameToken() {
    return place.getNameToken();
  }

  // /////////////////////
  // Inherited from Place

  @Override
  public void getPresenter(AsyncCallback<P> callback) {
    proxy.getPresenter(callback);
  }

  @Override
  public void getRawPresenter(AsyncCallback<Presenter<?>> callback) {
    proxy.getRawPresenter(callback);
  }

  @Override
  public final int hashCode() {
    return place.hashCode();
  }

  @Override
  public boolean matchesRequest(PlaceRequest request) {
    return place.matchesRequest(request);
  }

  @Override
  public void onPresenterChanged(Presenter<?> presenter) {
    PlaceRequest request = new PlaceRequest(getNameToken());
    
    proxy.onPresenterChanged(presenter);
    placeManager.onPlaceChanged(presenter.prepareRequest(request));
  }

  @Override
  public void onPresenterRevealed(Presenter<?> presenter) {
    PlaceRequest requestToCompare = placeManager.getCurrentPlaceHierarchy().get(placeManager.getCurrentPlaceHierarchy().size() - 1);
    
    // Do nothing until the currentPlaceHierarchy matches the presenter's token.
    if (requestToCompare.matchesNameToken(getNameToken())) {
      PlaceRequest request = new PlaceRequest(getNameToken());
      
      proxy.onPresenterRevealed(presenter);
      placeManager.onPlaceRevealed(presenter.prepareRequest(request));
    }
  }

  // /////////////////////
  // Protected methods that can be overridden

  @Override
  public final String toString() {
    return place.toString();
  }

  // /////////////////////
  // Private methods

  /**
   * Injects the various resources and performs other bindings.
   * <p />
   * Never call directly, it should only be called by GIN. Method injection is
   * used instead of constructor injection, because the latter doesn't work well
   * with GWT generators.
   * 
   * @param failureHandler The {@link ProxyFailureHandler}.
   * @param placeManager The {@link PlaceManager}.
   * @param eventBus The {@link EventBus}.
   */
  @Inject
  protected void bind(ProxyFailureHandler failureHandler,
      final PlaceManager placeManager, EventBus eventBus) {
    this.failureHandler = failureHandler;
    this.eventBus = eventBus;
    this.placeManager = placeManager;
    eventBus.addHandler(PlaceRequestInternalEvent.getType(),
        new PlaceRequestInternalHandler() {
          @Override
          public void onPlaceRequest(PlaceRequestInternalEvent event) {
            if (event.isHandled()) {
              return;
            }
            PlaceRequest request = event.getRequest();
            if (matchesRequest(request)) {
              event.setHandled();
              if (canReveal()) {
                handleRequest(request);
              } else {
                event.setUnauthorized();
              }
            }
          }
        });
    eventBus.addHandler(GetPlaceTitleEvent.getType(),
        new GetPlaceTitleHandler() {
          @Override
          public void onGetPlaceTitle(GetPlaceTitleEvent event) {
            if (event.isHandled()) {
              return;
            }
            PlaceRequest request = event.getRequest();
            if (matchesRequest(request)) {
              if (canReveal()) {
                event.setHandled();
                getPlaceTitle(event);
              }
            }
          }
        });
  }

  /**
   * Obtains the title for this place and invoke the passed handler when the
   * title is available. By default, places don't have a title and will invoke
   * the hanler with {@code null}, but override this method to provide your own
   * title.
   * 
   * @param event The {@link GetPlaceTitleEvent} to invoke once the title is
   *          available.
   */
  protected void getPlaceTitle(GetPlaceTitleEvent event) {
    event.getHandler().onSetPlaceTitle(null);
  }

  /**
   * Prepares the presenter with the information contained in the current
   * request, then reveals it. Will refuse to reveal the display and do nothing
   * if {@link canReveal()} returns <code>false</code>.
   * 
   * @param request The request to handle. Can pass <code>null</code> if no
   *          request is used, in which case the presenter will be directly
   *          revealed.
   */
  private void handleRequest(final PlaceRequest request) {
    proxy.getPresenter(new AsyncCallback<P>() {

      @Override
      public void onFailure(Throwable caught) {
        failureHandler.onFailedGetPresenter(caught);
      }

      @Override
      public void onSuccess(final P presenter) {
        // Everything should be bound before we prepare the presenter from the
        // request,
        // in case it wants to fire some events. That's why we will do this in a
        // deferred command.
        DeferredCommand.addCommand(new Command() {
          @Override
          public void execute() {
            presenter.prepareFromRequest(request);
            if (!presenter.isVisible()) {
              // This will trigger a reset in due time
              presenter.forceReveal(); 
            } else {
              // We have to do the reset ourselves
              presenter.forceReveal(); 
              ResetPresentersEvent.fire(ProxyPlaceAbstract.this); 
            }
          }
        });
      }
    });
  }

}
