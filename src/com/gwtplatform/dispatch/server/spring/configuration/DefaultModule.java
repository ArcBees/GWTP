package com.gwtplatform.dispatch.server.spring.configuration;

import org.springframework.context.annotation.Import;

import com.gwtplatform.dispatch.server.AbstractHttpSessionSecurityCookieFilter;
import com.gwtplatform.dispatch.server.IRequestProvider;
import com.gwtplatform.dispatch.server.spring.DispatchModule;
import com.gwtplatform.dispatch.server.spring.HttpSessionSecurityCookieFilter;
import com.gwtplatform.dispatch.server.spring.request.DefaultRequestProvider;

@Import(DispatchModule.class)
public class DefaultModule {

	private/* @Value("cookie") */String securityCookieName;

	String getSecurityCookieName() {
		return securityCookieName;
	}

	AbstractHttpSessionSecurityCookieFilter getCookieFilter() {
		return new HttpSessionSecurityCookieFilter(getSecurityCookieName());
	}

	IRequestProvider getRequestProvider() {
		return new DefaultRequestProvider();
	}
}
