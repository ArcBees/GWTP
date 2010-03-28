package com.philbeaudoin.gwtp.mvp.client.proxy;

public class PlaceImpl implements Place {

  private final String nameToken;
  
  public PlaceImpl(String nameToken) {
    this.nameToken = nameToken;
  }
  
  @Override
  public final boolean equals( Object o ) {
    if ( o instanceof Place ) {
      Place place = (Place) o;
      return getNameToken().equals( place.getNameToken() );
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return 17 * getNameToken().hashCode();
  }

  @Override
  public final String toString() {
    return getNameToken();
  }

  @Override
  public boolean canReveal() {
    return true;
  }

  @Override
  public final boolean matchesRequest( PlaceRequest request ) {
    return request.matchesNameToken( getNameToken() );
  }

  @Override
  public String getNameToken() {
    return nameToken;
  }

}
