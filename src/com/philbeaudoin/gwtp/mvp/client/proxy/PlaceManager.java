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

import java.util.List;

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
     * Programmatically reveals the specified hierarchy of places place. 
     * This will result in a {@link PlaceRequestInternalEvent} being fired.
     * <p />
     * This discards all the place hierarchy, replacing it with the specified
     * place hierarchy. To keep the place hierarchy, see 
     * {@link #revealRelativePlace(PlaceRequest)},
     * {@link #revealRelativePlace(PlaceRequest, int)} or 
     * {@link #revealRelativePlace(int)}.
     * To reveal a single {@link Place} instead of a hierarchy, see {@link #revealPlace}.
     *
     * @param request A list of {@link PlaceRequest} corresponding to the place hierarchy to reveal. 
     */
  void revealPlaceHierarchy( List<PlaceRequest> placeRequestHierarchy );
  
  /**
   * Programmatically reveals the specified place. 
   * This will result in a {@link PlaceRequestInternalEvent} being fired.
   * <p />
   * This discards all the place hierarchy, effectively revealing the
   * request as a top-level place. To keep the place hierarchy, see 
   * {@link #revealRelativePlace(PlaceRequest)},
   * {@link #revealRelativePlace(PlaceRequest, int)} or 
   * {@link #revealRelativePlace(int)}.
     * To reveal an entire place hierarchy, see {@link #revealPlaceHierarchy}.
   *
   * @see #buildHistoryToken(PlaceRequest)
   *
   * @param request The {@link PlaceRequest} corresponding to the place to reveal. 
   */
  void revealPlace(PlaceRequest request);

  /**
   * Programmatically reveals the specified place as a child of the current place hierarchy. 
   * Identical to calling {@link #revealRelativePlace(PlaceRequest, int)} with a level of {@code 0}.
   * This will result in a {@link PlaceRequestInternalEvent} being fired.
   * <p />
   * To reveal as a top-level place, see {@link #revealPlace}. To navigate back to a specific place
   * in the hierarchy, see {@link #revealRelativePlace(int)}.
   *
   * @see #buildRelativeHistoryToken(PlaceRequest)
   *
   * @param request The {@link PlaceRequest} corresponding to the place to reveal.
   */
  void revealRelativePlace(PlaceRequest request);
  
  /**
   * Programmatically reveals the specified place relative to the other places in
   * the current place hierarchy. This will result in a {@link PlaceRequestInternalEvent} being fired.
   * <p />
   * To reveal as a top-level place, see {@link #revealPlace}. To navigate back to a specific place
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
   * @see #buildRelativeHistoryToken(PlaceRequest, int)
   *
   * @param request The {@link PlaceRequest} corresponding to the place to reveal.
   * @param level If {@code 0}, then simply appends the {@code request} to the current page hierarchy.
   *              If negative, take back that many elements from the tail of the hierarchy
   *              before appending the {@code request}. If positive, keep only that many elements from
   *              the head of the hierarchy before appending the {@code request}.
   */
  void revealRelativePlace(PlaceRequest request, int level);

  /**
   * Programmatically reveals the specified place from the current place hierarchy. 
   * This will result in a {@link PlaceRequestInternalEvent} being fired.
   * <p />
   * Examples, suppose the current hierarchy is {@code requestA > requestB > requestC}
   * <ul>
   * <li> Calling {@code revealRelativePlace(-1)} makes it {@code requestA > requestB}</li>
   * <li> Calling {@code revealRelativePlace(1)} makes it {@code requestA}</li>
   * <li> Calling {@code revealRelativePlace(0)} makes it {@code requestA > requestB > requestC}</li>
   * <li> Calling {@code revealRelativePlace(-3)} or less calls {@link #revealDefaultPlace()}</li>
   * <li> Calling {@code revealRelativePlace(3)} or more makes it {@code requestA > requestB > requestC}</li>
   * </ul>
   * @see #buildRelativeHistoryToken(int)
   * 
   * @param level If negative, take back that many elements from the tail of the hierarchy. 
   *              If positive, keep only that many elements from the head of the hierarchy.
   *              Passing {@code 0} reveals the current place.
   */
  void revealRelativePlace(int level);

  /**
   * Reveals the place corresponding to the current value of the history token
   * in the URL bar. This will result in a {@link PlaceRequestInternalEvent} being fired.
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
  
  /**
   * Builds a string corresponding to the history token to reveal
   * the specified {@link PlaceRequest}. This can be used with a
   * {@link com.google.gwt.user.client.ui.Hyperlink}.
   * <p />
   * Invoking this history token will discard all the place hierarchy, 
   * effectively revealing the request as a top-level place. 
   * To keep the place hierarchy, see 
   * {@link #buildRelativeHistoryToken(PlaceRequest)},
   * {@link #buildRelativeHistoryToken(PlaceRequest, int)} or 
   * {@link #buildRelativeHistoryToken(int)}.
   *
   * @see #revealPlace(PlaceRequest)
   *
   * @param request The {@link PlaceRequest} corresponding to the place for which to build a history token.
   * @return The history token.
   */
  public String buildHistoryToken( PlaceRequest request );

  /**
   * Builds a string corresponding to the history token to reveal
   * the specified {@link PlaceRequest} as a child of the current place hierarchy. 
   * Identical to calling {@link #buildRelativeHistoryToken(PlaceRequest, int)} with a level of {@code 0}.
   * <p />
   * To get the history token for revealing as a top-level place, see {@link #buildHistoryToken}. 
   * To navigate back to a specific place in the hierarchy, see {@link #buildRelativeHistoryToken(int)}.
   * 
   * @see #revealRelativePlace(PlaceRequest)
   *
   * @param request The {@link PlaceRequest} corresponding to the place for which to build a history token.
   * @return The history token.
   */
  public String buildRelativeHistoryToken( PlaceRequest request );
  

  /**
   * Builds a string corresponding to the history token to reveal
   * the specified {@link PlaceRequest} relative to the other places in
   * the current place hierarchy.
   * <p />
   * To get the history token for revealing as a top-level place, see {@link #buildHistoryToken}. 
   * To navigate back to a specific place in the hierarchy, see {@link #buildRelativeHistoryToken(int)}.
   * <p />
   * Examples, suppose the current hierarchy is {@code requestA > requestB > requestC}
   * <ul>
   * <li> Calling {@code buildRelativeHistoryToken(requestD, 0)} will make a link to {@code requestA > requestB > requestC > requestD}</li>
   * <li> Calling {@code buildRelativeHistoryToken(requestD, -1)} will make a link to {@code requestA > requestB > requestD}</li>
   * <li> Calling {@code buildRelativeHistoryToken(requestD, 1)} will make a link to {@code requestA > requestD}</li>
   * <li> Calling {@code buildRelativeHistoryToken(requestD, -3)} will make a link to {@code requestD}</li>
   * <li> Calling {@code buildRelativeHistoryToken(requestD, 3)} will make a link to {@code requestA > requestB > requestC > requestD}</li>
   * </ul>
   * @see #revealRelativePlace(PlaceRequest, int)
   *
   * @param request The {@link PlaceRequest} corresponding to the place for which to build a history token.
   * @param level If {@code 0}, then simply appends the {@code request} to the current page hierarchy.
   *              If negative, take back that many elements from the tail of the hierarchy
   *              before appending the {@code request}. If positive, keep only that many elements from
   *              the head of the hierarchy before appending the {@code request}.
   * @return The history token.
   */
  public String buildRelativeHistoryToken( PlaceRequest request, int level );  


  /**
   * Builds a string corresponding to the history token to reveal
   * the specified place from the current place hierarchy. 
   * <p />
   * Examples, suppose the current hierarchy is {@code requestA > requestB > requestC}
   * <ul>
   * <li> Calling {@code revealRelativePlace(-1)} will make a link to {@code requestA > requestB}</li>
   * <li> Calling {@code revealRelativePlace(1)} will make a link to {@code requestA}</li>
   * <li> Calling {@code revealRelativePlace(0)} will make a link to {@code requestA > requestB > requestC}</li>
   * <li> Calling {@code revealRelativePlace(-3)} or less will make a link to {@code ""}</li>
   * <li> Calling {@code revealRelativePlace(3)} or more will make a link to {@code requestA > requestB > requestC}</li>
   * </ul>
   * @see #revealRelativePlace(int)
   * 
   * @param level If negative, take back that many elements from the tail of the hierarchy. 
   *              If positive, keep only that many elements from the head of the hierarchy.
   *              Passing {@code 0} makes a link to the current place.
   */
  public String buildRelativeHistoryToken( int level );  
  
  
  /**
   * Retrieves the number of elements in the place hierarchy. The title
   * of each of these elements can be obtained through {@link #getCurrentTitle(int)}. 
   * 
   * @return The depth of the place hierarchy.
   */
  public int getHierarchyDepth();
  
  /**
   * Retrieves the title of the currently displayed place, or {@code null} if it doesn't have a
   * title. Same as calling {@link #getCurrentTitle(int)} with a {@code level} of 0.
   * <p />
   * Instead of returning the title directly, this method accepts a callback and will call
   * {@link SetPlaceTitleHandler#onSetPlaceTitle} as soon as the title is available. This makes
   * it possible for the user to query the title from the server based on the {@link PlaceRequest}
   * parameters, for example. 
   * 
   * @param handler The {@link SetPlaceTitleHandler} to invoke when the place title is available.
   *                This will be invoked with {@code null} if the place doesn't have a title.
   */
  public void getCurrentTitle( SetPlaceTitleHandler handler );

  /**
   * Retrieves the title of the a specific place within the place hierarchy.
   * <p />
   * Instead of returning the title directly, this method accepts a callback and will call
   * {@link SetPlaceTitleHandler#onSetPlaceTitle} as soon as the title is available. This makes
   * it possible for the user to query the title from the server based on the {@link PlaceRequest}
   * parameters, for example. 
   * 
   * @see #getCurrentTitle(SetPlaceTitleHandler)
   * 
   * @param index The index of the place to reveal within the hierarchy.
   * @param handler The {@link SetPlaceTitleHandler} to invoke when the place title is available.
   *                This will be invoked with {@code null} if the place doesn't have a title.
   * @throws IndexOutOfBoundsException If the index is less than {@code 0} or greater or equal to
   *                                   {@link #getHierarchyDepth()}. 
   */
  public void getCurrentTitle( int index, SetPlaceTitleHandler handler ) throws IndexOutOfBoundsException;

  /**
   * Access the current place hierarchy, with the current {@link PlaceRequest} being the
   * last element of this list.
   * 
   * @return The current {@link PlaceRequest}.
   * 
   * @see {@link #getCurrentPlaceRequest()}
   */
  public List<PlaceRequest> getCurrentPlaceHierarchy();

  /**
   * Access the current place request, that is, the tail of the place request hierarchy.
   * 
   * @return The current {@link PlaceRequest}.
   * 
   * @see {@link #getCurrentPlaceHierarchy()}
   */
  public PlaceRequest getCurrentPlaceRequest();

  
}
