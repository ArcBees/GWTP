package com.philbeaudoin.gwtp.mvp.client.proxy;

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
