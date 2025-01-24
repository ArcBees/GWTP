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

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

/**
 * <b>Important:</b> For simplicity, places do not participate in dependency
 * injection and can be created with <code>new</code>.
 * <p>
 * A place represents a particular 'bookmark' or location inside the
 * application. A place is stateful - it may represent a location with it's
 * current settings, such as a particular ID value, or other unique indicators
 * that will allow a user to track back to that location later, either via a
 * browser bookmark, or by clicking the 'back' button.
 */
public interface Place {

    /**
     * Checks if the associated presenter can be revealed.
     * <p>
     * The default implementation of this method always return <code>true</code>,
     * but subclasses should override this and check to make sure the current user
     * has the privileges to see the place. Make sure the places you request in
     * {@link PlaceManager#revealDefaultPlace()} and
     * {@link PlaceManager#revealErrorPlace(String)} can reveal themselves,
     * otherwise your application could get into an infinite loop.
     *
     * @return <code>true</code> if the presenter can be revealed,
     *         <code>false</code> otherwise.
     */
    boolean canReveal();

    /**
     * Places are equal if their name token matches.
     *
     * @return <code>true</code> if the places name token matches,
     *         <code>false</code> otherwise.
     */
    @Override
    boolean equals(Object o);

    /**
     * The name token is the first part of the history token, before the
     * parameters. It is meant to be a unique identifier of a place. An exception
     * will be thrown if two places are registered with the same name token.
     *
     * @return The name token for this place.
     */
    String getNameToken();

    @Override
    int hashCode();

    /**
     * Makes sure the method matches the passed request.
     *
     * @param request The request to check.
     * @return <code>true</code> if the ID matches this place's name.
     */
    boolean matchesRequest(PlaceRequest request);

    @Override
    String toString();

}
