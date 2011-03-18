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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the default implementation of the {@link PlaceManager}.
 * 
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
public abstract class PlaceManagerImpl implements PlaceManager,
    ValueChangeHandler<String>, ClosingHandler {

  private final EventBus eventBus;
  private String currentHistoryToken = "";

  private String currentHRef = "";
  private boolean internalError;
  private String onLeaveQuestion;
  private List<PlaceRequest> placeHierarchy = new ArrayList<PlaceRequest>();
  private String previousHistoryToken;

  private final TokenFormatter tokenFormatter;

  private HandlerRegistration windowClosingHandlerRegistration;
  private boolean locked;
  private Command defferedNavigation;

  public PlaceManagerImpl(EventBus eventBus, TokenFormatter tokenFormatter) {
    this.eventBus = eventBus;
    this.tokenFormatter = tokenFormatter;    
    registerTowardsHistory();
  }

  @Override
  public String buildHistoryToken(PlaceRequest request) {
    return tokenFormatter.toPlaceToken(request);
  }

  @Override
  public String buildRelativeHistoryToken(int level) {
    List<PlaceRequest> placeHierarchyCopy = updatePlaceHierarchy(level);
    if (placeHierarchyCopy.size() == 0) {
      return "";
    }
    return tokenFormatter.toHistoryToken(placeHierarchyCopy);
  }

  @Override
  public String buildRelativeHistoryToken(PlaceRequest request) {
    return buildRelativeHistoryToken(request, 0);
  }

  @Override
  public String buildRelativeHistoryToken(PlaceRequest request, int level) {
    List<PlaceRequest> placeHierarchyCopy = updatePlaceHierarchy(level);
    placeHierarchyCopy.add(request);
    return tokenFormatter.toHistoryToken(placeHierarchyCopy);
  }

  /**
   * If a confirmation question is set (see {@link #setOnLeaveConfirmation(String)}),
   * this asks the user if he wants to leave the current page.
   * 
   * @return true if the user accepts to leave. false if he refuses.
   */
  private boolean confirmLeaveState() {
    if (onLeaveQuestion == null) {
      return true;
    }
    boolean confirmed = Window.confirm(onLeaveQuestion);
    if (confirmed) {
      // User has confirmed, don't ask any more question.
      setOnLeaveConfirmation(null);
    } else {
      NavigationRefusedEvent.fire(this);
      setBrowserHistoryToken(currentHistoryToken, false);
    }
    return confirmed;
  }

  /**
   * Fires the {@link PlaceRequestInternalEvent} for the given
   * {@link PlaceRequest}.
   * 
   * @param request The {@link PlaceRequest} to fire.
   */
  private void doRevealPlace(PlaceRequest request) {
    PlaceRequestInternalEvent requestEvent = new PlaceRequestInternalEvent(
        request);
    fireEvent(requestEvent);
    if (!requestEvent.isHandled()) {
      unlock();      
      error(tokenFormatter.toHistoryToken(placeHierarchy));
    } else if (!requestEvent.isAuthorized()) {
      unlock();
      illegalAccess(tokenFormatter.toHistoryToken(placeHierarchy));
    }
  }

  /**
   * Called whenever an error occurred that requires the error page
   * to be shown to the user.
   * This method will detect infinite reveal loops and throw an
   * {@link RuntimeException} in that case.
   * 
   * @param invalidHistoryToken The history token that was not recognised.
   */
  private void error(String invalidHistoryToken) {   
    startError();
    revealErrorPlace(invalidHistoryToken);
    stopError();
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    getEventBus().fireEventFromSource(event, this);
  }

  String getBrowserHistoryToken() {
    return History.getToken();
  }

  String getCurrentHref() {
    return Window.Location.getHref();
  }

  @Override
  public List<PlaceRequest> getCurrentPlaceHierarchy() {
    return placeHierarchy;
  }

  @Override
  public PlaceRequest getCurrentPlaceRequest() {
    try {
      return placeHierarchy.get(placeHierarchy.size() - 1);
    } catch (ArrayIndexOutOfBoundsException e) {
        return new PlaceRequest();
    }
  }

  @Override
  public void getCurrentTitle(SetPlaceTitleHandler handler) {
    getTitle(placeHierarchy.size() - 1, handler);
  }
  
  @Override
  public EventBus getEventBus() {
    return eventBus;
  }

  @Override
  public int getHierarchyDepth() {
    return placeHierarchy.size();
  }

  /**
   * Checks that the place manager is not locked and that the user allows
   * the application to navigate (see {@link #confirmLeaveState()}. If the
   * application is allowed to navigate, this method locks navigation.
   * 
   * @return true if the place manager can get the lock false otherwise. 
   */
  private boolean getLock() {
    if (locked) {
      return false;
    }
    if (!confirmLeaveState()) {
      return false;
    }
    lock();
    return true;
  }

  @Override
  public void getTitle(int index, SetPlaceTitleHandler handler)
      throws IndexOutOfBoundsException {
    GetPlaceTitleEvent event = new GetPlaceTitleEvent(
        placeHierarchy.get(index), handler);
    fireEvent(event);
    // If nobody took care of the title, indicate it's null
    if (!event.isHandled()) {
      handler.onSetPlaceTitle(null);
    }
  }

  @Override
  public boolean hasPendingNavigation() {
    return defferedNavigation != null;
  }

  /**
   * Called whenever the user tries to access an page to which he doesn't
   * have access, and we need to reveal the user-defined unauthorized place.
   * This method will detect infinite reveal loops and throw an
   * {@link RuntimeException} in that case.
   * 
   * @param historyToken The history token that was not recognised.
   */
  private void illegalAccess(String historyToken) {   
    startError();
    revealUnauthorizedPlace(historyToken);
    stopError();
  }

  private void lock() {
    if (!locked) {
      locked = true;
      LockInteractionEvent.fire(this, true);
    }
  }

  @Override
  public final void navigateBack() {
    if (previousHistoryToken != null) {
      setBrowserHistoryToken(previousHistoryToken, true);
    } else {
      revealDefaultPlace();
    }
  }

  /**
   * Handles change events from {@link History}.
   */
  @Override
  public final void onValueChange(final ValueChangeEvent<String> event) {
    if (locked) {
      defferedNavigation = new Command() {
        @Override
        public void execute() {
          onValueChange(event);
        }
      };
      return;
    }
    if (!getLock()) {
      return;
    }
    String historyToken = event.getValue();
    try {
      if (historyToken.trim().equals("")) {
        unlock();
        revealDefaultPlace();
      } else {
        placeHierarchy = tokenFormatter.toPlaceRequestHierarchy(historyToken);
        doRevealPlace(getCurrentPlaceRequest());
      }
    } catch (TokenFormatException e) {
      unlock();
      error(historyToken);
      NavigationEvent.fire(this, null);
    }
  }

  @Override
  public final void onWindowClosing(ClosingEvent event) {
    event.setMessage(onLeaveQuestion);
    Scheduler.get().scheduleDeferred(new Command() {
      @Override
      public void execute() {
        Window.Location.replace(currentHRef);
      }
    });
  }

  void registerTowardsHistory() {
    History.addValueChangeHandler(this);
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
  public final void revealPlace(final PlaceRequest request) {
    if (locked) {
      defferedNavigation = new Command() {
        @Override
        public void execute() {
          revealPlace(request);
        }
      };
      return;
    }
    if (!getLock()) {
      return;
    }
    placeHierarchy.clear();
    placeHierarchy.add(request);
    doRevealPlace(request);
  }

  @Override
  public final void revealPlaceHierarchy(
      final List<PlaceRequest> placeRequestHierarchy) {
    if (locked) {
      defferedNavigation = new Command() {
        @Override
        public void execute() {
          revealPlaceHierarchy(placeRequestHierarchy);
        }
      };
      return;
    }
    if (!getLock()) {
      return;
    }
    if (placeRequestHierarchy.size() == 0) {
      revealDefaultPlace();
    } else {
      placeHierarchy = placeRequestHierarchy;
      doRevealPlace(getCurrentPlaceRequest());
    }
  }
  
  @Override
  public void revealRelativePlace(final int level) {
    if (locked) {
      defferedNavigation = new Command() {
        @Override
        public void execute() {
          revealRelativePlace(level);
        }
      };
      return;
    }
    if (!getLock()) {
      return;
    }
    placeHierarchy = updatePlaceHierarchy(level);
    int hierarchySize = placeHierarchy.size();
    if (hierarchySize == 0) {
      revealDefaultPlace();
    } else {
      PlaceRequest request = placeHierarchy.get(hierarchySize - 1);
      doRevealPlace(request);
    }
  }

  @Override
  public void revealRelativePlace(PlaceRequest request) {
    revealRelativePlace(request, 0);
  }

  @Override
  public void revealRelativePlace(final PlaceRequest request, final int level) {
    if (locked) {
      defferedNavigation = new Command() {
        @Override
        public void execute() {
          revealRelativePlace(request, level);
        }
      };
      return;
    }
    if (!getLock()) {
      return;
    }
    placeHierarchy = updatePlaceHierarchy(level);
    placeHierarchy.add(request);
    doRevealPlace(request);
  }

  @Override
  public void revealUnauthorizedPlace(String unauthorizedHistoryToken) {
    revealErrorPlace(unauthorizedHistoryToken);
  }

  /**
   * This method saves the history token, allowing the {@link #navigateBack()} method to
   * be used and making it possible to correctly restore the browser's URL if the
   * user refuses to navigate. (See {@link #onWindowClosing(ClosingEvent)})
   * 
   * @param historyToken The current history token, a string.
   */
  private void saveHistoryToken(String historyToken) {
    previousHistoryToken = currentHistoryToken;
    currentHistoryToken = historyToken;
    currentHRef = getCurrentHref();
  }

  void setBrowserHistoryToken(String historyToken, boolean issueEvent) {
    History.newItem(historyToken, issueEvent);
  }

  @Override
  public final void setOnLeaveConfirmation(String question) {
    if (question == null && onLeaveQuestion == null) {
      return;
    }
    if (question != null && onLeaveQuestion == null) {
      windowClosingHandlerRegistration = Window.addWindowClosingHandler(this);
    }
    if (question == null && onLeaveQuestion != null) {
      windowClosingHandlerRegistration.removeHandler();
    }
    onLeaveQuestion = question;
  }

  /**
   * Start revealing an error or unauthorized page. This method will
   * throw an exception if an infinite loop is detected.
   * 
   * @see #stopError()
   */
  private void startError() {
    if (this.internalError) {
      throw new RuntimeException(
          "Encountered repeated errors resulting in an infinite loop. Make sure all users have access " +
          "to the pages revealed by revealErrorPlace and revealUnauthorizedPlace. (Note that the default " +
          "implementations call revealDefaultPlace)");
    }
    internalError = true;
  }

  /**
   * Indicates that an error page has successfully been revealed. Makes it possible
   * to detect infinite loops.
   * 
   * @see #startError()
   */
  private void stopError() {
    internalError = false;
  }

  @Override
  public void unlock() {
    if (locked) {
      locked = false;
      LockInteractionEvent.fire(this, false);
      if (hasPendingNavigation()) {
        Command navigation = defferedNavigation;
        defferedNavigation = null;
        navigation.execute();
      }
    }
  }
  
  @Override
  public void updateHistory(PlaceRequest request) {
    try {
      // Make sure the request match
      assert request.hasSameNameToken(getCurrentPlaceRequest()) : "Internal error, PlaceRequest passed to updateHistory doesn't match the tail of the place hierarchy.";
      placeHierarchy.set(placeHierarchy.size() - 1, request);
      String historyToken = tokenFormatter.toHistoryToken(placeHierarchy);
      String browserHistoryToken = getBrowserHistoryToken();
      if (browserHistoryToken == null
          || !browserHistoryToken.equals(historyToken)) {
        setBrowserHistoryToken(historyToken, false);
        saveHistoryToken(historyToken);
      }      
    } catch (TokenFormatException e) {
      // Do nothing.
    }
  }
 
  /**
   * Returns a modified copy of the place hierarchy based on the specified
   * {@code level}.
   * 
   * @param level If negative, take back that many elements from the tail of the
   *          hierarchy. If positive, keep only that many elements from the head
   *          of the hierarchy. Passing {@code 0} leaves the hierarchy
   *          untouched.
   */
  private List<PlaceRequest> updatePlaceHierarchy(int level) {
    int size = placeHierarchy.size();
    if (level < 0) {
      if (-level >= size) {
        return new ArrayList<PlaceRequest>();
      } else {
        return new ArrayList<PlaceRequest>(placeHierarchy.subList(0, size
            + level));
      }
    } else if (level > 0) {
      if (level >= size) {
        return new ArrayList<PlaceRequest>(placeHierarchy);
      } else {
        return new ArrayList<PlaceRequest>(placeHierarchy.subList(0, level));
      }
    }
    return new ArrayList<PlaceRequest>(placeHierarchy);
  }
}
