package com.philbeaudoin.gwtp.mvp.client.proxy;

public class TokenFormatException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 3707135366220900675L;

  public TokenFormatException() {
  }

  public TokenFormatException( String message ) {
    super( message );
  }

  public TokenFormatException( Throwable cause ) {
    super( cause );
  }

  public TokenFormatException( String message, Throwable cause ) {
    super( message, cause );
  }

}
