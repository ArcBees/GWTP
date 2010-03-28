package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.event.shared.GwtEvent;
import com.philbeaudoin.gwtp.mvp.client.EventBus;
import com.philbeaudoin.gwtp.mvp.client.Presenter;

/**
 * This event should be handled by {@link Proxy} classes. Upon handling
 * this event, the proxy should <b>first</b> set the content appropriately 
 * in the presenter, and then reveal the presenter.
 * 
 * @author Philippe Beaudoin
 */
public class RevealContentEvent extends GwtEvent<RevealContentHandler<?>> {

  private final Type<RevealContentHandler<?>> type;
  private final Presenter content;

  public static void fire(
      final EventBus eventBus,
      final Type<RevealContentHandler<?>> type,
      final Presenter content ) {
    eventBus.fireEvent( new RevealContentEvent(type, content) );
  }

  public RevealContentEvent( Type<RevealContentHandler<?>> type, Presenter content ) {
    this.type = type;
    this.content = content;   
  }
  
  @Override
  protected void dispatch(RevealContentHandler<?> handler) {
    handler.onRevealContent( this );
  }

  @Override
  public Type<RevealContentHandler<?>> getAssociatedType() {
    return type;
  }

  public Presenter getContent() {
    return content;
  }

}
