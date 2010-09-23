package com.gwtplatform.dispatch.server.guice.request;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.gwtplatform.dispatch.server.IRequestProvider;

@Singleton
public class DefaultRequestProvider implements IRequestProvider {

	private Provider<HttpServletRequest> requestProvider;

	@Inject
	public DefaultRequestProvider(Provider<HttpServletRequest> requestProvider) {
		this.requestProvider = requestProvider;
	}

	@Override
	public HttpServletRequest getServletRequest() {
		return requestProvider.get();
	}

}
