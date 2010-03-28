package com.philbeaudoin.gwtp.mvp.client.proxy;

import com.philbeaudoin.gwtp.mvp.client.Presenter;
import com.philbeaudoin.gwtp.mvp.client.Tab;

public interface TabContentProxy<P extends Presenter> extends Proxy<P> {

  /**
   * Retrieves the tab object associated with this presenter.
   * 
   * @return The tab object.
   */
  public Tab getTab();

  /**
   * Retrieves the text label to show on that tab.
   * 
   * @return The text label.
   */
  public String getLabel();

  
  /**
   * Retrieves the history token to show when this tab is
   * displayed. In the fairly typical scenario where a tab directly
   * contains a {@link ProxyPlace}, this should return the name token
   * of the proxy place. In the case of tabs that contain other
   * tab presenters, this should return the name token of a leaf-level
   * proxy.
   * 
   * @return The default history token to show.
   */
  public String getHistoryToken();

  /**
   * A tab priority indicates where it should appear within the tab 
   * strip. A tab with low priority will be placed more towards the 
   * left of the strip. Two tabs with the same priority will be placed
   * in an arbitrary order.
   * 
   * @return The priority.
   */
  public float getPriority();
  
}
