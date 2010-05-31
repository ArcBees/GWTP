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

package com.philbeaudoin.gwtp.mvp.client.proxy;

/**
 * Place managers work as an intermediary between the GWT {@link com.google.gwt.user.client.History} API
 * and {@link ProxyPlaceAbstract}s. It sets up event listener relationships to synchronize them.
 *
 * @author David Peterson
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
public interface PlaceManager {

  /**
   * Programmatically reveals the specified place. 
   * This will result in a {@link PlaceRequestEvent} being fired.
   * <p />
   * This discards all the place hierarchy, effectively revealing the
   * request as a root place. To keep the place hierarchy, see 
   * {@link #revealRelativePlace(PlaceRequest)},
   * {@link #revealRelativePlace(PlaceRequest, int)} or 
   * {@link #revealRelativePlace(int)}.
   *
   * @param request The {@link PlaceRequest} corresponding to the place to reveal. 
   */
  void revealPlace(PlaceRequest request);

  /**
   * Programmatically reveals the specified place as a child of the current place hierarchy. 
   * Identical to calling {@link #revealRelativePlace(PlaceRequest, int)} with a level of {@code 0}.
   * This will result in a {@link PlaceRequestEvent} being fired.
   * <p />
   * To reveal as a root place, see {@link #revealPlace}. To navigate back to a specific place
   * in the hierarchy, see {@link #revealRelativePlace(int)}.
   *
   * @param request The {@link PlaceRequest} corresponding to the place to reveal.
   */
  void revealRelativePlace(PlaceRequest request);
  
  /**
   * Programmatically reveals the specified place relative to the other places in
   * the current place hierarchy. This will result in a {@link PlaceRequestEvent} being fired.
   * <p />
   * To reveal as a root place, see {@link #revealPlace}. To navigate back to a specific place
   * in the hierarchy, see {@link #revealRelativePlace(int)}.
   * <p />
   * Examples, suppose the current hierarchy is {@code requestA > requestB > requestC}
   * <ul>
   * <li> Calling {@code revealRelativePlace(requestD, 0)} makes it {@code requestA > requestB > requestC > requestD}</li>
   * <li> Calling {@code revealRelativePlace(requestD, -1)} makes it {@code requestA > requestB > requestD}</li>
   * <li> Calling {@code revealRelativePlace(requestD, 1)} makes it {@code requestA > requestD}</li>
   * <li> Calling {@code revealRelativePlace(requestD, -3)} or less makes it {@code requestD}</li>
   * <li> Calling {@code revealRelativePlace(requestD, 3)} or more makes it {@code requestA > requestB > requestC > requestD}</li>
   * </ul>
   * @param request The {@link PlaceRequest} corresponding to the place to reveal.
   * @param level If {@code 0}, then simply appends the {@code request} to the current page hierarchy.
   *              If negative, take back that many elements from the tail of the hierarchy
   *              before appending the {@code request}. If positive, keep only that many elements from
   *              the head of the hierarchy before appending the {@code request}.
   */
  void revealRelativePlace(PlaceRequest request, int level);

  /**
   * Programmatically reveals the specified place from the current place hierarchy. 
   * This will result in a {@link PlaceRequestEvent} being fired.
   * <p />
   * To reveal as a root place, see {@link #revealPlace}. To navigate back to a specific place
   * in the hierarchy, see {@link #revealRelativePlace(int)}.
   * <p />
   * Examples, suppose the current hierarchy is {@code requestA > requestB > requestC}
   * <ul>
   * <li> Calling {@code revealRelativePlace(-1)} makes it {@code requestA > requestB}</li>
   * <li> Calling {@code revealRelativePlace(1)} makes it {@code requestA}</li>
   * <li> Calling {@code revealRelativePlace(0)} makes it {@code requestA > requestB > requestC}</li>
   * <li> Calling {@code revealRelativePlace(-3)} or less calls {@link #revealDefaultPlace()}</li>
   * <li> Calling {@code revealRelativePlace(3)} or more makes it {@code requestA > requestB > requestC}</li>
   * </ul>
   * @param level If negative, take back that many elements from the tail of the hierarchy. 
   *              If positive, keep only that many elements from the head of the hierarchy.
   *              Passing {@code 0} reveals the current place.
   */
  void revealRelativePlace(int level);

  /**
   * Reveals the place corresponding to the current value of the history token
   * in the URL bar. This will result in a {@link PlaceRequestEvent} being fired.
   */
  public void revealCurrentPlace();

  /**
   * Reveals the default place. This is invoked when the history token is empty
   * and no places handled it. Application-specific place managers should build
   * a {@link PlaceRequest} corresponding to their default presenter and call 
   * {@link #revealPlace(PlaceRequest)} with it. 
   * <p />
   * <b>Important!</b> Make sure you build a valid {@link PlaceRequest} and
   * that the user has access to it, otherwise you might create an infinite loop.
   */
  public void revealDefaultPlace();

  /**
   * Reveals the place to display when a user has been refused the access
   * to a specific place. This is invoked when the history token is valid but
   * the corresponding place's {@link Place#canReveal()} returned {@code false}.
   * Application-specific place managers should build
   * a {@link PlaceRequest} corresponding to the desired presenter and call 
   * {@link #revealPlace(PlaceRequest)} with it. 
   * The default implementation is simply to call
   * {@link #revealErrorPlace()}.
   * <p />
   * <b>Important!</b> Make sure you build a valid {@link PlaceRequest} and
   * that the user has access to it, otherwise you might create an infinite loop.
   * 
   * @param unauthorizedHistoryToken
   *          The history token that was not authorized.
   */
  public void revealUnauthorizedPlace( String unauthorizedHistoryToken );
  
  /**
   * Reveals the place to display when a user tries to access an invalid place. 
   * This is invoked when the history token was not handled by any place within 
   * the application. Application-specific place
   * managers should build a {@link PlaceRequest} corresponding to the desired 
   * presenter and call {@link #revealPlace(PlaceRequest)} with it. The default 
   * implementation is simply to call {@link #revealDefaultPlace()}.
   * <p />
   * <b>Important!</b> Make sure you build a valid {@link PlaceRequest} and
   * that the user has access to it, otherwise you might create an infinite loop.
   * 
   * @param invalidHistoryToken The history token that was not recognised.
   */
  public void revealErrorPlace( String invalidHistoryToken );

  /**
   * Sets the question that will be displayed whenever the user tries to
   * navigate away from the current page. Navigating away can happen either
   * occur by changing the program state (the history token), by entering an
   * external URL or by closing the window. All cases will be handled.
   * <p />
   * If the user indicates that he doesn't accept the navigation, then the
   * navigation will be cancelled, {@link NavigationRefusedEvent} will be
   * triggered and the current page will remain.
   * <p />
   * 
   * @param question
   *          The question to display. Pass {@code null} to accept navigation
   *          directly, without asking a question.
   */
  public void setOnLeaveConfirmation( String question );
  
  /**
   * Called whenever the current place has changed in a way that requires history parameters to be 
   * modified. 
   *
   * @param placeRequest The {@link PlaceRequest} portraying the change.
   */
  public void onPlaceChanged( PlaceRequest placeRequest );

  /**
   * Called whenever a new place has been revealed.
   *
   * @param placeRequest The {@link PlaceRequest} for the place that has just been revealed.
   */
  public void onPlaceRevealed( PlaceRequest placeRequest );

  /**
   * Navigate back to last visited history token.
   */
  public void navigateBack();

  
}
