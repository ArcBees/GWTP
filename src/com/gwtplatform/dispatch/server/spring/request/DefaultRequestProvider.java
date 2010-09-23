package com.gwtplatform.dispatch.server.spring.request;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.gwtplatform.dispatch.server.IRequestProvider;

public class DefaultRequestProvider implements IRequestProvider {

	public DefaultRequestProvider() {
	}

	//It should be in this way, or the HttpServletRequest should be autowired
	@Override
	public HttpServletRequest getServletRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

}
