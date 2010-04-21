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

package com.philbeaudoin.gwtp.mvp.client.proxy;

/**
 * Place managers work as an intermediary between the GWT {@link com.google.gwt.user.client.History} API
 * and {@link ProxyPlaceAbstract}s. It sets up event listener relationships to synchronize them.
 *
 * @author David Peterson
 * @author Philippe Beaudoin
 */
public interface PlaceManager {

  /**
   * Programmatically reveals the specified place. 
   * This will result in a {@link PlaceRequestEvent} being fired.
   *
   * @param request The {@link PlaceRequest} corresponding to the place to reveal. 
   */
  void revealPlace(PlaceRequest request);
  
  /**
   * Reveals the place corresponding to the current value of the history token
   * in the URL bar. This will result in a {@link PlaceRequestEvent} being fired.
   */
  public void revealCurrentPlace();

  /**
   * Reveals the default place. This is invoked when the history token is empty
   * and no places handled it. Application-specific place managers should fire the 
   * {@link PlaceRequestEvent} corresponding to their default place.
   */
  public void revealDefaultPlace();
  
  /**
   * Reveals an error place. This is invoked when the history token was not
   * handled by any place within the application. Application-specific place
   * managers should fire the {@link PlaceRequestEvent} corresponding to a 
   * place that displays an error. The default implementation is simply to
   * call {@link #revealDefaultPlace()}.
   * 
   * @param invalidHistoryToken The history token that was not recognised.
   */
  public void revealErrorPlace( String invalidHistoryToken );

  /**
   * Sets the question that will be displayed whenever the user tries to
   * navigate away from the current page. Navigating away can happen
   * either occur by changing the program state (the history token),
   * by entering an external URL or by closing the window. All cases will 
   * be handled.
   * <p /> 
   * If the user indicates that he doesn't accept the navigation, then the
   * navigation will be cancelled and the current page will remain. 
   * <p />
   * @param question The question to display. Pass null to accept navigation 
   *                 directly, without asking a question.
   */
  public void setOnLeaveConfirmation( String question );
  
  /**
   * <b>Important:</b> Do not call directly from outside the gwtp.mvp package.
   * 
   * If a confirmation question is set (see {@link #setOnLeaveConfirmation()}), this asks
   * the user if he wants to leave the current page.
   * 
   * @return true if the user accepts to leave. false if he refuses.
   */
  public boolean confirmLeaveState();
  
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
