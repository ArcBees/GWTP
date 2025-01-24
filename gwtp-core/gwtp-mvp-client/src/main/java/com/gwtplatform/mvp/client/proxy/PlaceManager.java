/*
 * Copyright 2011 ArcBees Inc.
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

import java.util.List;

import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

/**
 * Place managers work as an intermediary between the GWT {@link com.google.gwt.user.client.History}
 * API and {@link ProxyPlaceAbstract}. It sets up event listener relationships to synchronize them.
 */
public interface PlaceManager extends HasHandlers {

    /**
     * Builds a string corresponding to the history token to reveal the specified
     * {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest}. This can be used with a
     * {@link com.google.gwt.user.client.ui.Hyperlink}.
     * <p>
     * Invoking this history token will discard all the place hierarchy, effectively revealing the
     * request as a top-level place. To keep the place hierarchy, see
     * {@link #buildRelativeHistoryToken(com.gwtplatform.mvp.shared.proxy.PlaceRequest)},
     * {@link #buildRelativeHistoryToken(com.gwtplatform.mvp.shared.proxy.PlaceRequest, int)} or
     * {@link #buildRelativeHistoryToken(int)}.
     *
     * @param request The {@link com.gwtplatform.mvp.shared.proxy.PlaceRequest} corresponding to the place for which to
     *                build a history token.
     * @return The history token.
     * @see #revealPlace(com.gwtplatform.mvp.shared.proxy.PlaceRequest)
     */
    String buildHistoryToken(PlaceRequest request);

    /**
     * Builds a string corresponding to the history token to reveal the specified place from the
     * current place hierarchy.
     * <p>
     * Examples, suppose the current hierarchy is {@code requestA > requestB > requestC}
     * <ul>
     * <li>Calling {@code revealRelativePlace(-1)} will make a link to
     * {@code requestA > requestB}</li>
     * <li>Calling {@code revealRelativePlace(1)} will make a link to {@code requestA}</li>
     * <li>Calling {@code revealRelativePlace(0)} will make a link to
     * {@code requestA > requestB > requestC}</li>
     * <li>Calling {@code revealRelativePlace(-3)} or less will make a link to {@code ""}</li>
     * <li>Calling {@code revealRelativePlace(3)} or more will make a link to
     * {@code requestA > requestB > requestC}</li>
     * </ul>
     *
     * @param level If negative, take back that many elements from the tail of the hierarchy. If
     *              positive, keep only that many elements from the head of the hierarchy. Passing {@code 0}
     *              makes a link to the current place.
     * @see #revealRelativePlace(int)
     */
    String buildRelativeHistoryToken(int level);

    /**
     * Builds a string corresponding to the history token to reveal the specified {@link PlaceRequest}
     * as a child of the current place hierarchy. Identical to calling
     * {@link #buildRelativeHistoryToken(PlaceRequest, int)} with a level of {@code 0}.
     * <p>
     * To get the history token for revealing as a top-level place, see {@link #buildHistoryToken}. To
     * navigate back to a specific place in the hierarchy, see
     * {@link #buildRelativeHistoryToken(int)}.
     *
     * @param request The {@link PlaceRequest} corresponding to the place for which to build a history
     *                token.
     * @return The history token.
     * @see #revealRelativePlace(PlaceRequest)
     */
    String buildRelativeHistoryToken(PlaceRequest request);

    /**
     * Builds a string corresponding to the history token to reveal the specified {@link PlaceRequest}
     * relative to the other places in the current place hierarchy.
     * <p>
     * To get the history token for revealing as a top-level place, see {@link #buildHistoryToken}. To
     * navigate back to a specific place in the hierarchy, see
     * {@link #buildRelativeHistoryToken(int)}.
     * <p>
     * Examples, suppose the current hierarchy is {@code requestA > requestB > requestC}
     * <ul>
     * <li>Calling {@code buildRelativeHistoryToken(requestD, 0)} will make a link to
     * {@code requestA > requestB > requestC > requestD}</li>
     * <li>Calling {@code buildRelativeHistoryToken(requestD, -1)} will make a link to
     * {@code requestA > requestB > requestD}</li>
     * <li>Calling {@code buildRelativeHistoryToken(requestD, 1)} will make a link to
     * {@code requestA > requestD}</li>
     * <li>Calling {@code buildRelativeHistoryToken(requestD, -3)} will make a link to
     * {@code requestD}</li>
     * <li>Calling {@code buildRelativeHistoryToken(requestD, 3)} will make a link to
     * {@code requestA > requestB > requestC > requestD}</li>
     * </ul>
     *
     * @param request The {@link PlaceRequest} corresponding to the place for which to build a history
     *                token.
     * @param level   If {@code 0}, then simply appends the {@code request} to the current page
     *                hierarchy. If negative, take back that many elements from the tail of the hierarchy before
     *                appending the {@code request}. If positive, keep only that many elements from the head of the
     *                hierarchy before appending the {@code request}.
     * @return The history token.
     * @see #revealRelativePlace(PlaceRequest, int)
     */
    String buildRelativeHistoryToken(PlaceRequest request, int level);

    /**
     * Access the current place hierarchy, with the current {@link PlaceRequest} being the last
     * element of this list.
     *
     * @return The current {@link PlaceRequest}.
     * @see #getCurrentPlaceRequest()
     */
    List<PlaceRequest> getCurrentPlaceHierarchy();

    /**
     * Access the current place request, that is, the tail of the place request hierarchy. If the
     * hierarchy is empty this returns an empty {@link PlaceRequest}.
     *
     * @return The current {@link PlaceRequest}, or an empty one if the hierarchy is empty.
     * @see #getCurrentPlaceHierarchy()
     */
    PlaceRequest getCurrentPlaceRequest();

    /**
     * Makes it possible to access the {@link EventBus} object associated with that presenter.
     *
     * @return The EventBus associated with that presenter.
     */
    EventBus getEventBus();

    /**
     * Retrieves the number of elements in the place hierarchy.
     *
     * @return The depth of the place hierarchy.
     */
    int getHierarchyDepth();

    /**
     * Calls {@link com.google.gwt.user.client.History#back() History#back()}. This may cause the user's browser to
     * leave your application, if case you call this method from the first page visited.
     */
    void navigateBack();

    /**
     * Updates History without firing a {@link com.google.gwt.event.logical.shared.ValueChangeEvent}.
     * Only the last {@link PlaceRequest} of the place request hierarchy is modified.
     * <p>
     * This method will only work if the passed {@link PlaceRequest} has the same name token as the
     * current place request (see {@link #getCurrentPlaceRequest()}.
     * <p>
     * If {@code true} is passed as a second parameter, then this method causes a new token to be
     * added to the browser history, affecting the behavior of the browser's <em>back</em> button.
     *
     * @param request          The {@link PlaceRequest} to display in the updated history.
     * @param updateBrowserUrl {@code true} If the browser URL should be updated, {@code false}
     *                         otherwise.
     */
    void updateHistory(PlaceRequest request, boolean updateBrowserUrl);

    /**
     * Reveals the place corresponding to the current value of the history token in the URL bar. This
     * will result in a {@link PlaceRequestInternalEvent} being fired.
     */
    void revealCurrentPlace();

    /**
     * Reveals the default place. This is invoked when the history token is empty and no places
     * handled it. Application-specific place managers should build a {@link PlaceRequest}
     * corresponding to their default presenter and call {@link #revealPlace(PlaceRequest, boolean)}
     * with it. Consider passing {@code false} as the second parameter of {@code revealPlace},
     * otherwise a new token will be inserted in the browser's history and hitting the browser's
     * <em>back</em> button will not take the user out of the application.
     * <p>
     * <b>Important!</b> Make sure you build a valid {@link PlaceRequest} and that the user has access
     * to it, otherwise you might create an infinite loop.
     */
    void revealDefaultPlace();

    /**
     * Reveals the place to display when a user tries to access an invalid place. This is invoked when
     * the history token was not handled by any place within the application. Application-specific
     * place managers should build a {@link PlaceRequest} corresponding to the desired presenter and
     * call {@link #revealPlace(PlaceRequest, boolean)} with it. Consider passing {@code false} as the
     * second parameter of {@code revealPlace}, otherwise a new token will be inserted in the
     * browser's history and hitting the browser's <em>back</em> button will take the user back to the
     * invalid page he initially tried to access.
     * <p>
     * The default implementation is simply to call {@link #revealDefaultPlace()}.
     * <p>
     * <b>Important!</b> Make sure you build a valid {@link PlaceRequest} and that the user has access
     * to it, otherwise you might create an infinite loop.
     *
     * @param invalidHistoryToken The history token that was not recognised.
     */
    void revealErrorPlace(String invalidHistoryToken);

    /**
     * Reveals the place to display when a user has been refused the access to a specific place. This
     * is invoked when the history token is valid but the corresponding place's
     * {@link Place#canReveal()} returned {@code false}. Application-specific place managers should
     * build a {@link PlaceRequest} corresponding to the desired presenter and call
     * {@link #revealPlace(PlaceRequest, boolean)} with it. Consider passing {@code true} as the
     * second parameter of {@code revealPlace} if you navigate, say, to a login page and want the user
     * to be able to navigate back to the unauthorized page after login. If you are displaying a page
     * that simply mentions the error you may want to pass {@code false}, otherwise a new token will
     * be inserted in the browser's history and hitting the browser's <em>back</em> button will take
     * the user
     * back to the unauthorized page he initially tried to access.
     * <p>
     * The default implementation is simply to call {@link #revealErrorPlace(String)}.
     * <p>
     * <b>Important!</b> Make sure you build a valid {@link PlaceRequest} and that the user has access
     * to it, otherwise you might create an infinite loop.
     *
     * @param unauthorizedHistoryToken The history token that was not authorized.
     */
    void revealUnauthorizedPlace(String unauthorizedHistoryToken);

    /**
     * Sets the question that will be displayed whenever the user tries to navigate away from the
     * current page. Navigating away can happen either occur by changing the program state (the
     * history token), by entering an external URL or by closing the window. All cases will be
     * handled.
     * <p>
     * If the user indicates that he doesn't accept the navigation, then the navigation will be
     * cancelled, {@link NavigationRefusedEvent} will be triggered and the current page will remain.
     * <p>
     *
     * @param question The question to display. Pass {@code null} to accept navigation directly,
     *                 without asking a question.
     */
    void setOnLeaveConfirmation(String question);

    /**
     * Programmatically reveals the specified place, updating the browser URL in the process. If you
     * don't want the browser's URL to be updated see {@link #revealPlace(PlaceRequest, boolean)}.
     * <p>
     * This discards the current place hierarchy, effectively revealing the request as a top-level
     * place. To keep the current place hierarchy, see {@link #revealRelativePlace(PlaceRequest)},
     * {@link #revealRelativePlace(PlaceRequest, int)} or {@link #revealRelativePlace(int)}. To reveal
     * an entire place hierarchy, see {@link #revealPlaceHierarchy}.
     *
     * @param request The {@link PlaceRequest} corresponding to the place to reveal.
     * @see #buildHistoryToken(PlaceRequest)
     */
    void revealPlace(PlaceRequest request);

    /**
     * Programmatically reveals the specified place.
     * <p>
     * This discards the current place hierarchy, effectively revealing the request as a top-level
     * place. To keep the current place hierarchy, see {@link #revealRelativePlace(PlaceRequest)},
     * {@link #revealRelativePlace(PlaceRequest, int)} or {@link #revealRelativePlace(int)}. To reveal
     * an entire place hierarchy, see {@link #revealPlaceHierarchy}.
     *
     * @param request          The {@link PlaceRequest} corresponding to the place to reveal.
     * @param updateBrowserUrl {@code true} If the browser URL should be updated, {@code false}
     *                         otherwise.
     * @see #buildHistoryToken(PlaceRequest)
     */
    void revealPlace(PlaceRequest request, boolean updateBrowserUrl);

    /**
     * Programmatically reveals the specified hierarchy of places place, updating the browser URL in
     * the process.
     * <p>
     * This discards the current place hierarchy, replacing it with the specified place hierarchy. To
     * keep the current place hierarchy, see {@link #revealRelativePlace(PlaceRequest)},
     * {@link #revealRelativePlace(PlaceRequest, int)} or {@link #revealRelativePlace(int)}. To reveal
     * a single {@link Place} instead of a hierarchy, see {@link #revealPlace}.
     */
    void revealPlaceHierarchy(List<PlaceRequest> placeRequestHierarchy);

    /**
     * Programmatically reveals the specified place from the current place hierarchy.
     * <p>
     * Examples. Suppose the current hierarchy is {@code requestA > requestB > requestC}:
     * <ul>
     * <li>Calling {@code revealRelativePlace(-1)} makes it {@code requestA > requestB}</li>
     * <li>Calling {@code revealRelativePlace(1)} makes it {@code requestA}</li>
     * <li>Calling {@code revealRelativePlace(0)} makes it {@code requestA > requestB > requestC}</li>
     * <li>Calling {@code revealRelativePlace(-3)} or less calls {@link #revealDefaultPlace()}</li>
     * <li>Calling {@code revealRelativePlace(3)} or more makes it
     * {@code requestA > requestB > requestC}</li>
     * </ul>
     *
     * @param level If negative, take back that many elements from the tail of the hierarchy. If
     *              positive, keep only that many elements from the head of the hierarchy. Passing {@code 0}
     *              reveals the current place.
     * @see #buildRelativeHistoryToken(int)
     */
    void revealRelativePlace(int level);

    /**
     * Programmatically reveals the specified place as a child of the current place hierarchy.
     * Identical to calling {@link #revealRelativePlace(PlaceRequest, int)} with a level of {@code 0}.
     * This will result in a {@link PlaceRequestInternalEvent} being fired.
     * <p>
     * To reveal as a top-level place, see {@link #revealPlace}. To navigate back to a specific place
     * in the hierarchy, see {@link #revealRelativePlace(int)}.
     *
     * @param request The {@link PlaceRequest} corresponding to the place to reveal.
     * @see #buildRelativeHistoryToken(PlaceRequest)
     */
    void revealRelativePlace(PlaceRequest request);

    /**
     * Programmatically reveals the specified place relative to the other places in the current place
     * hierarchy. This will result in a {@link PlaceRequestInternalEvent} being fired.
     * <p>
     * To reveal as a top-level place, see {@link #revealPlace}. To navigate back to a specific place
     * in the hierarchy, see {@link #revealRelativePlace(int)}.
     * <p>
     * Examples. Suppose the current hierarchy is {@code requestA > requestB > requestC}:
     * <ul>
     * <li>Calling {@code revealRelativePlace(requestD, 0)} makes it
     * {@code requestA > requestB > requestC > requestD}</li>
     * <li>Calling {@code revealRelativePlace(requestD, -1)} makes it
     * {@code requestA > requestB > requestD}</li>
     * <li>Calling {@code revealRelativePlace(requestD, 1)} makes it {@code requestA > requestD}</li>
     * <li>Calling {@code revealRelativePlace(requestD, -3)} or less makes it {@code requestD}</li>
     * <li>Calling {@code revealRelativePlace(requestD, 3)} or more makes it
     * {@code requestA > requestB > requestC > requestD}</li>
     * </ul>
     *
     * @param request The {@link PlaceRequest} corresponding to the place to reveal.
     * @param level   If {@code 0}, then simply appends the {@code request} to the current page
     *                hierarchy. If negative, take back that many elements from the tail of the hierarchy before
     *                appending the {@code request}. If positive, keep only that many elements from the head of the
     *                hierarchy before appending the {@code request}.
     * @see #buildRelativeHistoryToken(PlaceRequest, int)
     */
    void revealRelativePlace(PlaceRequest request, int level);

    /**
     * Resets the navigation lock if it is currently set. You should usually not call this directly,
     * instead it is meant to be used with presenters that use manual reveal via
     * {@link ProxyPlace#manualReveal(com.gwtplatform.mvp.client.Presenter)} and
     * {@link ProxyPlace#manualRevealFailed()}.
     *
     * @see com.gwtplatform.mvp.client.Presenter#useManualReveal()
     * @see ProxyPlace#manualReveal(com.gwtplatform.mvp.client.Presenter)
     * @see ProxyPlace#manualRevealFailed()
     */
    void unlock();

    /**
     * Checks if the {@link PlaceManager} has to perform any pending navigation that were not
     * immediately executed because it was requested while the navigation was locked.
     *
     * @return {@code true} if there are any pending navigation requests, {@code false} otherwise.
     */
    boolean hasPendingNavigation();

}
