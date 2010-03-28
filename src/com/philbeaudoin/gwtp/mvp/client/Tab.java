package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public interface Tab extends HasText {

  /**
   * Sets the text displayed on the tab.
   * 
   * @param text The text.
   * 
   * @see HasText#setText(String)
   */
  @Override
  public abstract void setText(String text);

  /**
   * Gets the text displayed on the tab.
   * 
   * @return The text.
   * 
   * @see HasText#getText()
   */
  @Override
  public abstract String getText();

  /**
   * Sets the history token this tab links to.
   * 
   * @param historyToken The history token.
   */
  public abstract void setTargetHistoryToken(String historyToken);

  
  /**
   * A tab priority indicates where it should appear within the tab 
   * strip. A tab with low priority will be placed more towards the 
   * left of the strip. Two tabs with the same priority will be placed
   * in an arbitrary order.
   * 
   * @return The priority.
   */
  public abstract float getPriority();


  /**
   * Every tab should be able to return itself as an instance of a 
   * widget class.
   * 
   * @return The tab as a {@link Widget}.
   */
  public abstract Widget asWidget();

  /**
   * Should not be called directly. Call
   * {@link TabPanel#setActiveTab(Tab)} instead.
   */
  public void activate();
  
  /**
   * Should not be called directly. Call
   * {@link TabPanel#setActiveTab(Tab)} instead.
   */
  public void deactivate();
  
}