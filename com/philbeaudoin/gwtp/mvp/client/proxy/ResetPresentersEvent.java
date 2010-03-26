package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.event.shared.GwtEvent;
import com.philbeaudoin.gwtp.mvp.client.EventBus;

public class ResetPresentersEvent extends GwtEvent<ResetPresentersHandler> {

  private static final Type<ResetPresentersHandler> type = new Type<ResetPresentersHandler>();

  public static Type<ResetPresentersHandler> getType() {
    return type;    
  }
  
  public static void fire( final EventBus eventBus ) {
    eventBus.fireEvent( new ResetPresentersEvent() );
  }

  @Override
  protected void dispatch(ResetPresentersHandler handler) {
    handler.onResetPresenters( this );
  }

  @Override
  public Type<ResetPresentersHandler> getAssociatedType() {
    return getType();
  }
  
}
