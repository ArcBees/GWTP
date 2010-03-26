package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.event.shared.GwtEvent;
import com.philbeaudoin.gwtp.mvp.client.EventBus;
import com.philbeaudoin.gwtp.mvp.client.Presenter;

/**
 * Use this type of event to reveal content that should get added as the 
 * {@link com.google.gwt.user.client.ui.RootLayoutPanel}.
 * This type of content is constrained to lie within the browser window, and to resize with it.
 * You will be responsible for adding your own scrollbars as content overflow, usually via
 * {@link com.google.gwt.user.client.ui.ScrollPanel}.
 * 
 * @author Philippe Beaudoin
 */
public class RevealRootLayoutContentEvent extends GwtEvent<RevealRootLayoutContentHandler> {

  private static final Type<RevealRootLayoutContentHandler> TYPE = new Type<RevealRootLayoutContentHandler>();

  public static Type<RevealRootLayoutContentHandler> getType() {
    return TYPE;
  }  

  private final Presenter content;

  public static void fire(
      final EventBus eventBus,
      final Presenter content ) {
    eventBus.fireEvent( new RevealRootLayoutContentEvent(content) );
  }

  public RevealRootLayoutContentEvent( Presenter content ) {
    this.content = content;   
  }

  @Override
  protected void dispatch(RevealRootLayoutContentHandler handler) {
    handler.onRevealContent( this );
  }

  @Override
  public Type<RevealRootLayoutContentHandler> getAssociatedType() {
    return getType();
  }

  public Presenter getContent() {
    return content;
  }

}
