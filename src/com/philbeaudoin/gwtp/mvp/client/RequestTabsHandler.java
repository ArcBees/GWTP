package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.event.shared.EventHandler;

public interface RequestTabsHandler extends EventHandler {

	/**
	 * Called whenever the {@link TabContainerPresenter} is instantiated
	 * and needs to know which tabs it contains.
	 * 
	 * @param event The event.
	 */
	public void onRequestTabs( RequestTabsEvent event );
	
}
