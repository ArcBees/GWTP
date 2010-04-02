package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.google.gwt.event.shared.EventHandler;
import com.philbeaudoin.gwtp.mvp.client.RootPresenter;

/**
 * This is the handler interface for {@link RevealRootContentEvent}. 
 * It is used solely by {@link RootPresenter}.
 * 
 * @author Philippe Beaudoin
 */
public interface RevealRootContentHandler extends EventHandler {

  /**
   * Called whenever a presenter wants to sets itself as the root
   * content of the application, that is, within GWT's
   * {@link com.google.gwt.user.client.ui.RootPanel}.
   * 
   * @param event The event containing the presenter that wants to bet set as root content.
   */
  public abstract void onRevealContent(RevealRootContentEvent event);
 
  
}
