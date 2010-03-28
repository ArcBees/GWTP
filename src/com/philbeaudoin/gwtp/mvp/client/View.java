package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * The interface for singleton view classes that handles all
 * the UI-related code for a {@link Presenter}.
 * 
 * @author Philippe Beaudoin
 */
public interface View {

  /**
   * Requests the view to set content within a specific slot,
   * clearing anything that was already contained there.
   * If the view doesn't know about this slot, it can silently 
   * ignore the request.
   * 
   * @param slot An opaque object indicating the slot to add into.
   * @param content The content to add, a {@link Widget}.
   */
  public void setContent( Object slot, Widget content );
  
  /**
   * Requests the view to add content within a specific slot.
   * If the view doesn't know about this slot, it can silently 
   * ignore the request .
   * 
   * @param slot An opaque object indicating the slot to add into.
   * @param content The content to add, a {@link Widget}.
   */
  public void addContent( Object slot, Widget content );
  
  /**
   * Requests the view to clear the content of a specific slot.
   * If the view doesn't know about this slot, it can silently 
   * ignore the request.
   * 
   * @param slot An opaque object indicating the slot to add into.
   * @param content The content to add, a {@link Presenter}.
   */
  public void clearContent( Object slot );
  
  /**
   * Retrieves this view as a {@link Widget} so that it can be inserted within the DOM.
   * 
   * @return This view as a DOM object.
   */
  public Widget asWidget();

}