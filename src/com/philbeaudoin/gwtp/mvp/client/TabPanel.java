package com.philbeaudoin.gwtp.mvp.client;

public interface TabPanel {

  /**
   * Adds a new tab to the widget.
   * 
   * @param text The text to display on the tab.
   * @param historyToken The history token the tab points to.
   * @param priority The priority of the {@link Tab} to add.
   * @return The newly added {@link Tab}.
   */
  public Tab addTab( String text, String historyToken, float priority );
  
  
  /**
   * Removes a tab from the widget.
   * 
   * @param tab The tab to remove
   */
  public void removeTab( Tab tab );

  
  /**
   * Removes all tabs from the widget.
   */
  public void removeTabs();

  /**
   * Sets the currently active tab.
   * 
   * @param tab The tab to activate
   */
  public void setActiveTab( Tab tab );
  
}
