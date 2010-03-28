package com.philbeaudoin.gwtp.mvp.client.proxy;

/**
 * <b>Important:</b> For simplicity, places do not participate in 
 * dependency injection and can be created with <code>new</code>.
 * <p />
 * A place represents a particular 'bookmark' or location inside the
 * application. A place is stateful - it may represent a location with it's
 * current settings, such as a particular ID value, or other unique indicators
 * that will allow a user to track back to that location later, either via a
 * browser bookmark, or by clicking the 'back' button.
 *
 * @author Philippe Beaudoin
 */
public interface Place {

  /**
   * Places are equal if their name token matches.
   * 
   * @param The other {@link Place} to compare with.
   * @return <code>true</code> if the places name token matches, <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Object o);

  @Override
  public int hashCode();

  @Override
  public String toString();
  
  /**
   * The name token is the first part of the history token, before the 
   * parameters. It is meant to be a unique identifier of a place.
   * An exception will be thrown if two places are registered with the
   * same name token.
   *
   * @return The name token for this place.
   */
  public String getNameToken();

  /**
   * Makes sure the method matches the passed request.
   *
   * @param request The request to check.
   * @return <code>true</code> if the ID matches this place's name.
   */
  public boolean matchesRequest(PlaceRequest request);

  /**
   * Checks if the associated presenter can be revealed.
   * <p />
   * The default implementation of this method always return
   * <code>true</code>, but subclasses should override this and
   * check to make sure the current user has the privileges
   * to see the place. Make sure the places you request in 
   * {@link PlaceManager#revealDefaultPlace()} and
   * {@link PlaceManager#revealErrorPlace(String)} can 
   * reveal themselves, otherwise your application could get into
   * an infinite loop.
   *
   * @return <code>true</code> if the presenter can be revealed, <code>false</code> otherwise.
   */
  public boolean canReveal();  
  
}