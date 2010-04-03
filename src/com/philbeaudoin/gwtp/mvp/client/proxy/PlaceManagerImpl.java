package com.philbeaudoin.gwtp.mvp.client.proxy;

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


import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.philbeaudoin.gwtp.mvp.client.EventBus;

public abstract class PlaceManagerImpl implements PlaceManager, ValueChangeHandler<String>, ClosingHandler {

  private final EventBus eventBus;
  private final TokenFormatter tokenFormatter;

  private String onLeaveQuestion = null;
  private HandlerRegistration windowClosingHandlerRegistration = null;
  private String currentHistoryToken = "";
  private String currentLocation = "";
  private String previousHistoryToken = null;


  public PlaceManagerImpl( EventBus eventBus, TokenFormatter tokenFormatter ) {
    this.eventBus = eventBus;
    this.tokenFormatter = tokenFormatter;

    // Register ourselves with the History API.
    History.addValueChangeHandler( this );
  }

  /**
   * Updates History if it has changed, without firing another
   * {@link ValueChangeEvent}.
   * 
   * @param request The request to display in the updated history.
   */
  private void updateHistory( PlaceRequest request ) {
    try {
      String requestToken = tokenFormatter.toHistoryToken( request );
      String historyToken = History.getToken();
      if ( historyToken == null || !historyToken.equals( requestToken ) ) {
        History.newItem( requestToken, false );
      }
      previousHistoryToken = currentHistoryToken;
      currentHistoryToken = requestToken;
      currentLocation = Window.Location.getHref();
    } catch ( TokenFormatException e ) {
      // Do nothing.
    }
  }

  @Override
  public void revealCurrentPlace() {
    History.fireCurrentHistoryState();
  }

  @Override
  public void revealErrorPlace(String invalidHistoryToken) {
    revealDefaultPlace();
  }
  
  @Override
  public final void onPlaceChanged( PlaceRequest placeRequest ) {
    try {
      if ( placeRequest.hasSameNameToken( tokenFormatter.toPlaceRequest( History.getToken() ) ) ) {
        // Only update if the change comes from a place that matches
        // the current location.
        updateHistory( placeRequest );
      }
    } catch ( TokenFormatException e ) {
      // Do nothing...
    }
  }

  @Override
  public final void onPlaceRevealed( PlaceRequest placeRequest) {
    updateHistory( placeRequest );
  }

  @Override
  public final void revealPlace( PlaceRequest request ) {
    if( !doRevealPlace(request) )
        revealErrorPlace( request.toString() );
  }


  /**
   * Handles change events from {@link History}.
   */
  @Override
  public final void onValueChange( ValueChangeEvent<String> event ) {
    String historyToken = event.getValue();
    try {
      if( !doRevealPlace( tokenFormatter.toPlaceRequest( historyToken ) ) ) {
        if ( historyToken.trim() == "" )
          revealDefaultPlace();
        else
          revealErrorPlace( historyToken );
      }
    } catch ( TokenFormatException e ) {
      revealErrorPlace( historyToken );
    }
  }

  /**
   * Fires the {@link PlaceRequestEvent} for the given {@link PlaceRequest}. 
   * 
   * @param request The {@link PlaceRequest} to fire.
   * @return {@code true} if the request has been handled, {@code false} otherwise.
   */
  private final boolean doRevealPlace( PlaceRequest request ) {
    PlaceRequestEvent requestEvent = new PlaceRequestEvent( request );
    eventBus.fireEvent(requestEvent);
    return requestEvent.isHandled();
  }
  
  @Override
  public final void setOnLeaveConfirmation( String question ) {
    if( question == null && onLeaveQuestion  == null ) return;
    if( question != null && onLeaveQuestion == null )
      windowClosingHandlerRegistration = Window.addWindowClosingHandler(this);
    if( question == null && onLeaveQuestion != null )
      windowClosingHandlerRegistration.removeHandler();
    onLeaveQuestion = question;
  }

  @Override
  public final void onWindowClosing(Window.ClosingEvent event) {
    event.setMessage(onLeaveQuestion);
    DeferredCommand.addCommand( new Command() {
      @Override
      public void execute() {
        Window.Location.replace(currentLocation);
      }
    });
  }

  @Override
  public final boolean confirmLeaveState() {
    if( onLeaveQuestion == null ) return true;
    boolean confirmed =  Window.confirm( onLeaveQuestion );
    if( confirmed ) {
      // User has confirmed, don't ask any more question.
      setOnLeaveConfirmation( null );
    } else {
      History.newItem(currentHistoryToken, false);
    }
    return confirmed;
  }

  @Override
  public final void navigateBack() {
    if( previousHistoryToken != null )
      History.newItem( previousHistoryToken );
    else
      revealDefaultPlace();
  }
}
