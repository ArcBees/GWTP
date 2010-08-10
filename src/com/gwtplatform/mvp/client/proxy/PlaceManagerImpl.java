/**
 * Copyright 2010 Gwt-Platform
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

package com.gwtplatform.mvp.client.proxy;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.gwtplatform.mvp.client.EventBus;

/**
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
public abstract class PlaceManagerImpl implements PlaceManager, ValueChangeHandler<String>, ClosingHandler {

  private final EventBus eventBus;
  private final TokenFormatter tokenFormatter;

  private String onLeaveQuestion = null;
  private HandlerRegistration windowClosingHandlerRegistration = null;
  private String currentHistoryToken = "";
  private String currentHRef = "";
  private String previousHistoryToken = null;

  private List<PlaceRequest> placeHierarchy = new ArrayList<PlaceRequest>();
  
  private int errorPlaceAttempts = 0;

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
      // Make sure the request match
      assert request.hasSameNameToken( getCurrentPlaceRequest() ) : 
          "Internal error, PlaceRequest passed to updateHistory doesn't match the tail of the place hierarchy.";
      placeHierarchy.set( placeHierarchy.size()-1, request );      
      String historyToken = tokenFormatter.toHistoryToken( placeHierarchy );
      String browserHistoryToken = History.getToken();
      if ( browserHistoryToken == null || !browserHistoryToken.equals( historyToken ) ) {
        History.newItem( historyToken, false );
      }
      previousHistoryToken = currentHistoryToken;
      currentHistoryToken = historyToken;
      currentHRef = Window.Location.getHref();
    } catch ( TokenFormatException e ) {
      // Do nothing.
    }
  }

  @Override
  public void revealCurrentPlace() {
    History.fireCurrentHistoryState();
  }

  @Override
  public void revealUnauthorizedPlace(String unauthorizedHistoryToken) {
    revealErrorPlace(unauthorizedHistoryToken);
  }

  @Override
  public void revealErrorPlace(String invalidHistoryToken) {
	errorPlaceAttempts++;
	if(errorPlaceAttempts<=1){  
		revealDefaultPlace();
	}else{
		throw new RuntimeException("revealErrorPlace is set to revealDefaultPlace.  However revealDefaultPlace is causing an error which if left to continue will result in an infinite loop.");
	}
  }

  @Override
  public final void onPlaceChanged( PlaceRequest placeRequest ) {
    try {
      if ( placeRequest.hasSameNameToken( getCurrentPlaceRequest() ) ) {
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
  public final void revealPlaceHierarchy( List<PlaceRequest> placeRequestHierarchy ) {
	errorPlaceAttempts = 0;
    if( !confirmLeaveState() )
      return;
    if( placeRequestHierarchy.size() == 0 )
        revealDefaultPlace();
    else {
        placeHierarchy = placeRequestHierarchy;
        doRevealPlace( getCurrentPlaceRequest() );
    }
  }
  
  @Override
  public final void revealPlace( PlaceRequest request ) {
	errorPlaceAttempts = 0;
    if( !confirmLeaveState() )
      return;
    placeHierarchy.clear();
    placeHierarchy.add( request );
    doRevealPlace( request );
  }

  @Override
  public void revealRelativePlace(PlaceRequest request) {
    revealRelativePlace( request, 0 );
  }

  @Override
  public void revealRelativePlace(PlaceRequest request, int level) {
    if( !confirmLeaveState() )
      return;
    placeHierarchy = updatePlaceHierarchy(level);
    placeHierarchy.add( request );
    doRevealPlace( request );
  }

  @Override
  public void revealRelativePlace(int level) {
    if( !confirmLeaveState() )
      return;
    placeHierarchy = updatePlaceHierarchy(level);
    int hierarchySize = placeHierarchy.size();
    if( hierarchySize == 0 )
      revealDefaultPlace();
    else {
      PlaceRequest request = placeHierarchy.get(hierarchySize-1); 
      doRevealPlace( request );
    }
  }

  /**
   * Returns a modified copy of the place hierarchy based on the specified {@code level}.
   * 
   * @param level If negative, take back that many elements from the tail of the hierarchy. 
   *              If positive, keep only that many elements from the head of the hierarchy.
   *              Passing {@code 0} leaves the hierarchy untouched.
   */
  private List<PlaceRequest> updatePlaceHierarchy(int level) {
    int size = placeHierarchy.size();
    if( level < 0 ) {
      if( -level >= size )
        return new ArrayList<PlaceRequest>();
      else
        return new ArrayList<PlaceRequest>( placeHierarchy.subList(0, size+level) );
    } else if( level > 0 ) {
      if( level >= size )
        return new ArrayList<PlaceRequest>(placeHierarchy);
      else
        return new ArrayList<PlaceRequest>( placeHierarchy.subList(0, level) );
    }
    return new ArrayList<PlaceRequest>( placeHierarchy );
  }

  /**
   * Handles change events from {@link History}.
   */
  @Override
  public final void onValueChange( ValueChangeEvent<String> event ) {
    if( !confirmLeaveState() )
      return;
    String historyToken = event.getValue();
    try {
      if( historyToken.trim().equals("") )
          revealDefaultPlace();
      else {
          placeHierarchy = tokenFormatter.toPlaceRequestHierarchy( historyToken );
          doRevealPlace( getCurrentPlaceRequest() );
      }
    } catch ( TokenFormatException e ) {
      revealErrorPlace( historyToken );
      NavigationEvent.fire( eventBus, null );
    }
  }

  /**
   * Fires the {@link PlaceRequestInternalEvent} for the given {@link PlaceRequest}. 
   * 
   * @param request The {@link PlaceRequest} to fire.
   */
  private final void doRevealPlace( PlaceRequest request ) {
    PlaceRequestInternalEvent requestEvent = new PlaceRequestInternalEvent( request );
    eventBus.fireEvent(requestEvent);
    if( !requestEvent.isHandled() )
        revealErrorPlace( tokenFormatter.toHistoryToken( placeHierarchy ) );
    else if( !requestEvent.isAuthorized() )
        revealUnauthorizedPlace( tokenFormatter.toHistoryToken( placeHierarchy ) );
    NavigationEvent.fire( eventBus, request );    
  }

  @Override
  public List<PlaceRequest> getCurrentPlaceHierarchy() {
    return placeHierarchy;
  }
  
  @Override
  public PlaceRequest getCurrentPlaceRequest() {
    return placeHierarchy.get( placeHierarchy.size()-1 );
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
        Window.Location.replace(currentHRef);
      }
    });
  }


  /**
   * If a confirmation question is set (see {@link #setOnLeaveConfirmation()}), this asks
   * the user if he wants to leave the current page.
   * 
   * @return true if the user accepts to leave. false if he refuses.
   */
  final private boolean confirmLeaveState() {
    if( onLeaveQuestion == null ) return true;
    boolean confirmed =  Window.confirm( onLeaveQuestion );
    if( confirmed ) {
      // User has confirmed, don't ask any more question.
      setOnLeaveConfirmation( null );
    } else {
      NavigationRefusedEvent.fire( eventBus );
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

  @Override
  public String buildHistoryToken( PlaceRequest request ) {
    return tokenFormatter.toPlaceToken( request );
  }

  @Override
  public String buildRelativeHistoryToken( PlaceRequest request ) {
    return buildRelativeHistoryToken( request, 0 );
  }
  
  @Override
  public String buildRelativeHistoryToken( PlaceRequest request, int level ) {
    List<PlaceRequest> placeHierarchyCopy = updatePlaceHierarchy(level);
    placeHierarchyCopy.add( request );
    return tokenFormatter.toHistoryToken(placeHierarchyCopy);    
  }

  @Override
  public String buildRelativeHistoryToken( int level ) {
    List<PlaceRequest> placeHierarchyCopy = updatePlaceHierarchy(level);
    if( placeHierarchyCopy.size() == 0 )
      return "";
    return tokenFormatter.toHistoryToken(placeHierarchyCopy);    
  }

  @Override
  public int getHierarchyDepth() {
    return placeHierarchy.size();
  }
  
  @Override
  public void getCurrentTitle( SetPlaceTitleHandler handler ) {
    getCurrentTitle(0, handler);
  }

  @Override
  public void getCurrentTitle( int index, SetPlaceTitleHandler handler ) throws IndexOutOfBoundsException {
    GetPlaceTitleEvent event = new GetPlaceTitleEvent( placeHierarchy.get(index), handler );
    eventBus.fireEvent( event );
    // If nobody took care of the title, indicate it's null
    if( !event.isHandled() )
      handler.onSetPlaceTitle(null);
  }
}
