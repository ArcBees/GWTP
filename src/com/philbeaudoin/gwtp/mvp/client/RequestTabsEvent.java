package com.philbeaudoin.gwtp.mvp.client;

/**
 * Copyright 2010 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
