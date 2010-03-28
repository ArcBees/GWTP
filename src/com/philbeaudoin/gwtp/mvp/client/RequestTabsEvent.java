package com.philbeaudoin.gwtp.mvp.client;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

public class RequestTabsEvent extends GwtEvent<RequestTabsHandler> {

	private final Type<RequestTabsHandler> type;
	private final TabContainerPresenter tabContainer;

	/**
	 * Creates an event for requesting the tabs that should be displayed
	 * in a {@link TabContainerPresenter}.
	 * 
	 * @param type The specific {@link Type} of this event.
	 * @param tabContainer The {@link TabContainerPresenter} making the request.
	 */
	public RequestTabsEvent( final Type<RequestTabsHandler> type, 
			TabContainerPresenter tabContainer ) {
		this.type = type;
		this.tabContainer = tabContainer;
	}
	
	@Override
	protected void dispatch(RequestTabsHandler handler) {
		handler.onRequestTabs( this );
	}

	@Override
	public Type<RequestTabsHandler> getAssociatedType() {
		return type;
	}

	public TabContainerPresenter getTabContainer() {
		return tabContainer;
	}

}
