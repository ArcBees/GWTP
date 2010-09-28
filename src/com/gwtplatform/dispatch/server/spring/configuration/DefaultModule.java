package com.gwtplatform.dispatch.server.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.gwtplatform.dispatch.server.AbstractHttpSessionSecurityCookieFilter;
import com.gwtplatform.dispatch.server.IRequestProvider;
import com.gwtplatform.dispatch.server.spring.DispatchModule;
import com.gwtplatform.dispatch.server.spring.HttpSessionSecurityCookieFilter;
import com.gwtplatform.dispatch.server.spring.request.DefaultRequestProvider;

@Import(DispatchModule.class)
public class DefaultModule {

	private/* @Value("cookie") */String securityCookieName;

	@Bean
	String getSecurityCookieName() {
		return securityCookieName;
	}

	@Bean
	AbstractHttpSessionSecurityCookieFilter getCookieFilter() {
		return new HttpSessionSecurityCookieFilter(getSecurityCookieName());
	}

	@Bean
	IRequestProvider getRequestProvider() {
		return new DefaultRequestProvider();
	}
}
