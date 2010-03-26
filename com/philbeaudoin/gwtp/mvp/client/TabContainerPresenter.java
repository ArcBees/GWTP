package com.philbeaudoin.gwtp.mvp.client;

import com.philbeaudoin.gwtp.mvp.client.proxy.TabContentProxy;


public interface TabContainerPresenter extends Presenter {

  /**
   * Adds a new tab to this presenter.
   * 
   * @param tabProxy The {@link TabContentProxy} containing information on the tab to add.
   * @return The newly added tab.
   */
  public Tab addTab(TabContentProxy<?> tabProxy);

}
