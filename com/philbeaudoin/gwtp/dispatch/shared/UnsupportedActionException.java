package com.philbeaudoin.gwtp.dispatch.shared;

public class UnsupportedActionException extends ServiceException {

  private static final long serialVersionUID = -3362561625013898012L;

  /**
   * For serialization.
   */
  UnsupportedActionException() {
  }

  @SuppressWarnings({"unchecked"})
  public UnsupportedActionException( Action<? extends Result> action ) {
    this( ( Class<? extends Action<? extends Result>> ) action.getClass() );
  }

  public UnsupportedActionException( Class<? extends Action<? extends Result>> actionClass ) {
    super( "No handler is registered for " + actionClass.getName() );
  }

}
