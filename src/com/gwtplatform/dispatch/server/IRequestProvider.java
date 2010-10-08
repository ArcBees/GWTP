package com.gwtplatform.dispatch.server;

import javax.servlet.http.HttpServletRequest;

public interface IRequestProvider {

	HttpServletRequest getServletRequest();
}
