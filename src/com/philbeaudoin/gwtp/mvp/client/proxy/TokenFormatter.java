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
 * Provides services to convert a  {@link PlaceRequest} to and from a History token value.
 */
public interface TokenFormatter {

  /**
   * Converts a place request hierarchy (a list of {@link PlaceRequest}) 
   * into a history token.
   * 
   * @param placeRequestHierarchy The place request hierarchy.
   * @return The history token.
   */
  String toHistoryToken( List<PlaceRequest> placeRequestHierarchy ) throws TokenFormatException;

  /**
   * Converts a history token into a place request hierarchy (a list of {@link PlaceRequest}). 
   *
   * @param historyToken The history token.
   * @return The place request hierarchy.
   * @throws TokenFormatException if there is an error converting.
   */
  List<PlaceRequest> toPlaceRequestHierarchy( String historyToken ) throws TokenFormatException;
  
  /**
   * Converts a {@link PlaceRequest} into a place token.
   * 
   * @param placeRequest The place request.
   * @return The history token.
   */
  String toPlaceToken( PlaceRequest placeRequest ) throws TokenFormatException;

  /**
   * Converts a place token into a {@link PlaceRequest}. A place token is different than
   * a history token, since the history token can contain a hierarchy of different place
   * requests.
   *
   * @param placeToken The place token.
   * @return The place request.
   * @throws TokenFormatException if there is an error converting.
   */
  PlaceRequest toPlaceRequest( String placeToken ) throws TokenFormatException;

}
