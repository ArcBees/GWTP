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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Formats tokens from String values into PlaceRequest values and back again. This implementation
 * parses the token format like so:
 *
 * <pre>[name](;param=value)*</pre>
 */
public final class ParameterTokenFormatter implements TokenFormatter {


  private static final String HIERARCHY_SEPARATOR = "/";

  private static final String HIERARCHY_PATTERN = HIERARCHY_SEPARATOR + "(?!" + HIERARCHY_SEPARATOR + ")";

  private static final String HIERARCHY_ESCAPE = HIERARCHY_SEPARATOR + HIERARCHY_SEPARATOR;

  private static final String PARAM_SEPARATOR = ";";

  private static final String PARAM_PATTERN = PARAM_SEPARATOR + "(?!" + PARAM_SEPARATOR + ")";

  private static final String PARAM_ESCAPE = PARAM_SEPARATOR + PARAM_SEPARATOR;

  private static final String VALUE_SEPARATOR = "=";

  private static final String VALUE_PATTERN = VALUE_SEPARATOR + "(?!" + VALUE_SEPARATOR + ")";

  private static final String VALUE_ESCAPE = VALUE_SEPARATOR + VALUE_SEPARATOR;

  public ParameterTokenFormatter() {}


  @Override
  public String toHistoryToken(List<PlaceRequest> placeRequestHierarchy)
      throws TokenFormatException {
    StringBuilder out = new StringBuilder();
    for ( int i=0; i < placeRequestHierarchy.size(); ++i ) {
      if( i != 0 )
        out.append( HIERARCHY_SEPARATOR );
      out.append( toPlaceToken(placeRequestHierarchy.get(i)) );
    }
    return out.toString();
  }

  @Override
  public List<PlaceRequest> toPlaceRequestHierarchy(String historyToken)
      throws TokenFormatException {
    
    List<String> placeTokens = toPlaceTokenList( historyToken );
 
    List<PlaceRequest> result = new ArrayList<PlaceRequest>();
    
    for( String placeToken : placeTokens ) 
      result.add( toPlaceRequest( placeToken ) );
 
    return result;
  }
  
  @Override
  public String toPlaceToken( PlaceRequest placeRequest ) {
    StringBuilder out = new StringBuilder();
    out.append( placeRequest.getNameToken() );

    Set<String> params = placeRequest.getParameterNames();
    if ( params != null && params.size() > 0 ) {
      for ( String name : params ) {
        out.append( PARAM_SEPARATOR );
        out.append( escape( name ) ).append( VALUE_SEPARATOR )
        .append( escape( placeRequest.getParameter( name, null )) );
      }
    }
    return out.toString();
  }

  private List<String> toPlaceTokenList( String historyToken ) throws TokenFormatException {
    String[] placeTokens = historyToken.split( HIERARCHY_PATTERN );
    List<String> result = new ArrayList<String>();
    String completePlaceToken = "";
    for( String placeToken : placeTokens ) {
      completePlaceToken = completePlaceToken + placeToken;
      
      // Will end with a separator if we had an escaped separator
      if( completePlaceToken.endsWith(HIERARCHY_SEPARATOR) ) 
        continue;
      
      result.add( completePlaceToken );      
      completePlaceToken = "";
    }
    return result;
  }

  @Override
  public PlaceRequest toPlaceRequest( String placeToken ) throws TokenFormatException {
    PlaceRequest req = null;

    int split = placeToken.indexOf( PARAM_SEPARATOR );
    if ( split == 0 ) {
      throw new TokenFormatException( "Place history token is missing." );
    } else if ( split == -1 ) {
      req = new PlaceRequest( placeToken );
    } else if ( split >= 0 ) {
      req = new PlaceRequest( placeToken.substring( 0, split ) );
      String paramsChunk = placeToken.substring( split + 1 );
      String[] paramTokens = paramsChunk.split( PARAM_PATTERN );
      String completeParamToken = "";
      for ( String paramToken : paramTokens ) {
        completeParamToken = completeParamToken + paramToken;

        // Will end with a separator if we had an escaped separator
        if( completeParamToken.endsWith(PARAM_SEPARATOR) ) 
          continue;

        String[] param = completeParamToken.split( VALUE_PATTERN );
        completeParamToken = "";
        String key = "";
        int i = 0;
        for ( ; i <  param.length; ++i ) {
          key = key + param[i];

          // Will end with a separator if we had an escaped separator
          if( !key.endsWith(VALUE_SEPARATOR) ) 
            break;
        }
        ++i;
        String value = "";
        for ( ; i <  param.length; ++i ) {
          value = value + param[i];

          // Will end with a separator if we had an escaped separator
          if( !value.endsWith(VALUE_SEPARATOR) ) 
            break;
        }        
        ++i;

        if ( i != param.length )
          throw new TokenFormatException( "Bad parameter: Parameters require a single '"
              + VALUE_SEPARATOR + "' between the key and value." );
        req = req.with( key, value );
      }
    }

    return req;

  }

  private static String escape( String value ) {
    return value.replaceAll( HIERARCHY_SEPARATOR, HIERARCHY_ESCAPE )
    .replaceAll( PARAM_SEPARATOR, PARAM_ESCAPE )
    .replaceAll( VALUE_SEPARATOR, VALUE_ESCAPE );
  }
}
