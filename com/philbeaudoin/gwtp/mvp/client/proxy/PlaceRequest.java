package com.philbeaudoin.gwtp.mvp.client.proxy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a 'request' for a place location. It includes the 'id'
 * of the place as well as any parameter values. It can convert from and to
 * String tokens for use with the GWT History.
 * <p/>
 * <p/>
 * Place request tokens are formatted like this:
 * <p/>
 * <code>#nameToken(;key=value)*</code>
 * <p/>
 * <p/>
 * There is a mandatory 'nameToken' value, followed by 0 or more key/value pairs,
 * separated by semi-colons (';'). A few examples follow:
 * <p/>
 * <ul>
 * <li> <code>#users</code> </li>
 * <li> <code>#user;name=j.blogs</code> </li>
 * <li> <code>#user-email;name=j.blogs;type=home</code> </li>
 * </ul>
 * The separators (';' and '=') can be modified in {@link ParameterTokenFormatter}.
 *
 * @author David Peterson
 * @author Philippe Beaudoin
 */
public class PlaceRequest {

  private final String nameToken;

  private final Map<String, String> params;

  /**
   * Builds a request with the specified name token and
   * without parameters.
   * 
   * @param nameToken The name token for the request.
   */
  public PlaceRequest( String nameToken ) {
    this.nameToken = nameToken;
    // Note: No parameter map attached.
    //       Calling PlaceRequest#with(String, String) will
    //       invoke the other constructor and instantiate a map.
    //       This choice makes it efficient to instantiate
    //       parameter-less PlaceRequest and slightly more
    //       costly to instantiate PlaceRequest with parameters.    
    this.params = null;
  }

  /**
   * Builds a place request that copies all the parameters of the passed
   * request and adds a new parameter.  
   * 
   * @param req   The {@link PlaceRequest} to copy.
   * @param name  The new parameter name.
   * @param value The new parameter value.
   */
  private PlaceRequest( PlaceRequest req, String name, String value ) {
    this.nameToken = req.nameToken;
    this.params = new java.util.HashMap<String, String>();
    if ( req.params != null )
      this.params.putAll( req.params );
    if ( value != null )
      this.params.put( name, value );
  }

  public String getNameToken() {
    return nameToken;
  }

  public Set<String> getParameterNames() {
    if ( params != null ) {
      return params.keySet();
    } else {
      return Collections.emptySet();
    }
  }

  public String getParameter( String key, String defaultValue ) {
    String value = null;

    if ( params != null )
      value = params.get( key );

    if ( value == null )
      value = defaultValue;
    return value;
  }

  /**
   * Checks if this place request has the same name token as the one passed in.
   * 
   * @param other The {@link PlaceRequest} to check against.
   * @return <code>true</code> if both requests share the same name token. <code>false</code> otherwise.
   */
  public boolean hasSameNameToken(PlaceRequest other) {
    return nameToken.equals(other.nameToken);
  }

  /**
   * Checks if this place request matches the name token passed.
   * 
   * @param nameToken The name token to match.
   * @return <code>true</code> if the request matches. <code>false</code> otherwise.
   */
  public boolean matchesNameToken(String nameToken) {
    return this.nameToken.equals(nameToken);
  }
  
  /**
   * Returns a new instance of the request with the specified parameter name
   * and value. If a parameter with the same name was previously specified,
   * the new request contains the new value.
   *
   * @param name  The new parameter name.
   * @param value The new parameter value.
   * @return The new place request instance.
   */
  public PlaceRequest with( String name, String value ) {
    // Note: Copying everything to a new PlaceRequest is slightly
    //       less efficient than modifying the current request, but
    //       it reduces unexpected side-effects. Moreover, it lets
    //       us instantiate the parameter map only when needed.
    //       (See the PlaceRequest constructors.)
    return new PlaceRequest( this, name, value );
  }

  @Override
  public boolean equals( Object obj ) {
    if ( obj instanceof PlaceRequest ) {
      PlaceRequest req = (PlaceRequest) obj;
      if ( !nameToken.equals( req.nameToken ) )
        return false;

      if ( params == null )
        return req.params == null;
      else
        return params.equals( req.params );
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 11 * ( nameToken.hashCode() + ( params == null ? 0 : params.hashCode() ) );
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append( "{" ).append( nameToken );
    if ( params != null && params.size() > 0 ) {
      out.append( ": " );
      for ( Map.Entry<String, String> entry : params.entrySet() ) {
        out.append( entry.getKey() ).append( " = " ).append( entry.getValue() ).append( ";" );
      }
    }
    out.append( "}" );
    return out.toString();
  }
}
