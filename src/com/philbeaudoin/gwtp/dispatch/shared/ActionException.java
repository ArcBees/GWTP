package com.philbeaudoin.gwtp.dispatch.shared;

/**
 * These are thrown by {@link com.philbeaudoin.gwtp.dispatch.server.Dispatch#execute(Action)} if there is a
 * problem executing a particular {@link Action}.
 * 
 * @author David Peterson
 */
public class ActionException extends Exception {

  private static final long serialVersionUID = -1423773155541528952L;

  public ActionException() {
  }

  public ActionException( String message ) {
    super( message );
  }

  public ActionException( Throwable cause ) {
    super( cause.getMessage() );
  }

  public ActionException( String message, Throwable cause ) {
    super( message + " (" + cause.getMessage() + ")" );
  }

}
