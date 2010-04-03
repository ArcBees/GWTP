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

/**
 * Provides services to convert a  {@link PlaceRequest} to and from a History token value.
 */
public interface TokenFormatter {
  /**
   * Converts a {@link PlaceRequest} into a {@link com.google.gwt.user.client.History} token.
   * 
   * @param placeRequest The place request
   * @return The history token
   */
  String toHistoryToken( PlaceRequest placeRequest ) throws TokenFormatException;

  /**
   * Converts a {@link com.google.gwt.user.client.History} token into a {@link PlaceRequest}.
   *
   * @param token The token.
   * @return The place request
   * @throws TokenFormatException if there is an error converting.
   */
  PlaceRequest toPlaceRequest( String token ) throws TokenFormatException;
}
