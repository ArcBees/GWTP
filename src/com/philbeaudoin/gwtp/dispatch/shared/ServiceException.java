/**
 * This is thrown by services when there is a low-level problem while processing an action execution.
 *
 * @author David Peterson
 */

package com.philbeaudoin.gwtp.dispatch.shared;

public class ServiceException extends Exception {

  private static final long serialVersionUID = 3975903306855473017L;

  public ServiceException() {
  }

  public ServiceException( String message ) {
    super( message );
  }

  public ServiceException( String message, Throwable cause ) {
    super( message + " (" + cause + ")" );
  }

  public ServiceException( Throwable cause ) {
    super( cause.getMessage() );
  }
}
