package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.event.shared.GwtEvent;
import com.philbeaudoin.gwtp.mvp.client.EventBus;
import com.philbeaudoin.gwtp.mvp.client.Presenter;

/**
 * Use this type of event to reveal content that should get added as the 
 * {@link com.google.gwt.user.client.ui.RootPanel}.
 * This type of content is usually meant to use the browser like a regular webpage, adding a vertical
 * scrollbar as the content overflow.
 * 
 * @author Philippe Beaudoin
 */
public class RevealRootContentEvent extends GwtEvent<RevealRootContentHandler> {

  private static final Type<RevealRootContentHandler> TYPE = new Type<RevealRootContentHandler>();

  public static Type<RevealRootContentHandler> getType() {
      return TYPE;
  }  
  
  private final Presenter content;

  public static void fire(
      final EventBus eventBus,
      final Presenter content ) {
    eventBus.fireEvent( new RevealRootContentEvent(content) );
  }

  public RevealRootContentEvent( Presenter content ) {
    this.content = content;   
  }
  
  @Override
  protected void dispatch(RevealRootContentHandler handler) {
    handler.onRevealContent( this );
  }

  @Override
  public Type<RevealRootContentHandler> getAssociatedType() {
    return getType();
  }

  public Presenter getContent() {
    return content;
  }

}
